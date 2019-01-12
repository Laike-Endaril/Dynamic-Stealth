package com.fantasticsource.dynamicstealth.server.Senses;

import com.fantasticsource.dynamicstealth.common.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.server.Attributes;
import com.fantasticsource.dynamicstealth.server.Threat;
import com.fantasticsource.mctools.Speedometer;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.common.DynamicStealth.TRIG_TABLE;
import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.server.configdata.EntityVisionData.*;

public class Sight
{
    private static final int DROP_SEEN_DELAY = 60;

    private static final DynamicStealthConfig.ServerSettings.Senses senses = serverSettings.senses;
    private static final DynamicStealthConfig.ServerSettings.Senses.Vision vision = senses.vision;

    public static long currentTick;


    //For each searcher                , map of entities, last reported stealth level, and when last stealth level was recorded
    private static Map<EntityLivingBase, Map<Entity, Pair<Double, Long>>> seenEntities = new LinkedHashMap<>();


    @SubscribeEvent
    public static void update(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            currentTick = event.world.getTotalWorldTime();
            seenEntities.entrySet().removeIf(e -> update(e.getValue()));
        }
    }

    private static boolean update(Map<Entity, Pair<Double, Long>> seenMap)
    {
        if (seenMap == null) return true;
        seenMap.entrySet().removeIf(e -> currentTick - e.getValue().getValue() >= DROP_SEEN_DELAY);
        return seenMap.size() == 0;
    }


    public static boolean recentlySeen(EntityLivingBase searcher, Entity target)
    {
        Map<Entity, Pair<Double, Long>> map = seenEntities.get(searcher);
        if (map == null) return false;

        Pair<Double, Long> data = map.get(target);
        if (data == null) return false;

        if (currentTick - data.getValue() < DROP_SEEN_DELAY) return true;
        else
        {
            map.remove(target);
            return false;
        }
    }


    public static double visualStealthLevel(EntityLivingBase searcher, Entity target, boolean useCache, boolean updateCache)
    {
        Map<Entity, Pair<Double, Long>> map = seenEntities.get(searcher);

        if (useCache && map != null)
        {
            Pair<Double, Long> data = map.get(target);
            if (data != null && data.getValue() == currentTick) return data.getKey();
        }

        searcher.world.profiler.startSection("DS Sight checks");
        double result = visualStealthLevel(searcher, target);
        searcher.world.profiler.endSection();

        if (updateCache)
        {
            if (map == null)
            {
                map = new LinkedHashMap<>();
                seenEntities.put(searcher, map);
            }

            map.put(target, new Pair<>(result, currentTick));
        }

        return result;
    }

    private static double visualStealthLevel(EntityLivingBase searcher, Entity target)
    {
        //Hard checks (absolute)
        if (searcher == null || target == null) return 2;
        if (target instanceof EntityPlayerMP && ((EntityPlayerMP) target).capabilities.disableDamage) return 2;

        int angleLarge = angleLarge(searcher);
        if (!target.isEntityAlive() || angleLarge == 0) return 2;


        //Angles and Distances (absolute, base FOV)
        double distSquared = searcher.getDistanceSq(target);
        int distanceFar = distanceFar(searcher);
        if (distSquared > Math.pow(distanceFar, 2)) return 2;

        double distanceThreshold;
        int angleSmall = angleSmall(searcher);
        if (angleSmall == 180) distanceThreshold = distanceFar;
        else
        {
            //Using previous values here to give the player a chance, because client-side rendering always runs behind what's actually happening
            double angleDif = Vec3d.fromPitchYaw(searcher.prevRotationPitch, searcher.prevRotationYawHead).normalize().dotProduct(new Vec3d(target.posX - searcher.posX, target.posY - searcher.posY, target.posZ - searcher.posZ).normalize());

            //And because Vec3d.fromPitchYaw occasionally returns values barely out of the range of (-1, 1)...
            if (angleDif < -1) angleDif = -1;
            else if (angleDif > 1) angleDif = 1;

            angleDif = Tools.radtodeg(TRIG_TABLE.arccos(angleDif)); //0 in front, 180 in back
            if (angleDif > angleLarge) return 2;
            if (angleDif < angleSmall) distanceThreshold = distanceFar;
            else
            {
                int distanceNear = distanceNear(searcher);
                distanceThreshold = distanceNear + (distanceFar - distanceNear) * (angleLarge - angleDif) / (angleLarge - angleSmall);
            }
        }


        //Setup for checks against EntityLivingBase-only targets
        boolean isLivingBase = target instanceof EntityLivingBase;
        EntityLivingBase targetLivingBase = isLivingBase ? (EntityLivingBase) target : null;

        //Glowing (absolute, after Angles)
        if (vision.g_absolutes.seeGlowing && isLivingBase && targetLivingBase.getActivePotionEffect(MobEffects.GLOWING) != null) return 0;


        //LOS check (absolute, after Angles, after Glowing)
        if (!los(searcher, target)) return 2;


        //Lighting (absolute, factor, after Angles, after Glowing, after LOS)
        double lightFactor = lightLevelTotal(target);
        if (searcher.getActivePotionEffect(MobEffects.NIGHT_VISION) != null || naturalNightVision(searcher))
        {
            lightFactor = Math.min(15, lightFactor + vision.c_lighting.nightVisionAddition);
        }

        int lightLow = lightLow(searcher);
        if (lightFactor <= lightLow) return 2;
        int lightHigh = lightHigh(searcher);
        lightFactor = lightFactor >= lightHigh ? 1 : lightFactor / (lightHigh - lightLow);


        //Speeds (factor)
        double speedFactor = Speedometer.getSpeed(target);
        double speedLow = speedLow(searcher);
        double speedHigh = speedHigh(searcher);
        speedFactor = speedFactor >= speedHigh ? 1 : speedFactor <= speedLow ? 0 : (speedFactor - speedLow) / (speedHigh - speedLow);


        //Blindness (multiplier)
        double blindnessMultiplier = searcher.getActivePotionEffect(MobEffects.BLINDNESS) != null ? vision.a_stealthMultipliers.blindnessMultiplier : 1;


        //Invisibility (multiplier)
        double invisibilityMultiplier = isLivingBase && targetLivingBase.getActivePotionEffect(MobEffects.INVISIBILITY) != null ? vision.a_stealthMultipliers.invisibilityMultiplier : 1;


        //Alerted multiplier
        double alertMultiplier = searcher instanceof EntityLiving && Threat.get(searcher).threatLevel > 0 ? vision.b_visibilityMultipliers.alertMultiplier : 1;

        //Seen multiplier
        double seenMultiplier = recentlySeen(searcher, target) ? vision.b_visibilityMultipliers.seenMultiplier : 1;

        //Crouching (multiplier)
        double crouchingMultiplier = target.isSneaking() ? vision.a_stealthMultipliers.crouchingMultiplier : 1;


        //Mob Heads (multiplier)
        double mobHeadMultiplier = 1;
        if (isLivingBase)
        {
            ItemStack helmet = targetLivingBase.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (helmet.getItem() == Items.SKULL)
            {
                int damage = helmet.getItemDamage();
                if (target instanceof EntitySkeleton && damage == 0 || target instanceof EntityWitherSkeleton && damage == 1 || target instanceof EntityZombie && damage == 2 || target instanceof EntityCreeper && damage == 4)
                {
                    mobHeadMultiplier = vision.a_stealthMultipliers.mobHeadMultiplier;
                }
            }
            else if ((helmet.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN) && helmet.getItem() == Item.getItemFromBlock(Blocks.LIT_PUMPKIN)) && target instanceof EntitySnowman) mobHeadMultiplier = vision.a_stealthMultipliers.mobHeadMultiplier;
        }


        //Armor
        double armorMultiplier = isLivingBase ? Math.max(0, 1 + vision.b_visibilityMultipliers.armorMultiplierCumulative * targetLivingBase.getTotalArmorValue()) : 1;


        //Fire
        double fireMultiplier = !isLivingBase ? 1 : !targetLivingBase.isBurning() ? 1 : vision.b_visibilityMultipliers.onFireMultiplier;


        //Combine multipliers
        double stealthMultiplier = Tools.min(blindnessMultiplier, invisibilityMultiplier, crouchingMultiplier, mobHeadMultiplier);
        double visibilityMultiplier = Tools.max(armorMultiplier, fireMultiplier, alertMultiplier, seenMultiplier);
        double baseMultiplier = Tools.min(Tools.max((lightFactor + speedFactor) / 2 * stealthMultiplier * visibilityMultiplier, 0), 1);

        double visReduction = !isLivingBase ? 0 : targetLivingBase.getEntityAttribute(Attributes.VISIBILITY_REDUCTION).getAttributeValue();
        double attributeMultiplier = visReduction == 0 ? Double.MAX_VALUE : searcher.getEntityAttribute(Attributes.SIGHT).getAttributeValue() / visReduction;


        //Final calculation
        return Math.sqrt(distSquared) / (distanceThreshold * baseMultiplier * attributeMultiplier);
    }

    public static double lightLevelTotal(Entity entity)
    {
        BlockPos blockpos = new BlockPos(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
        return entity.world.getLightFromNeighbors(blockpos);
    }

    public static boolean los(Entity searcher, Entity target)
    {
        return LOS.rayTraceBlocks(searcher.world, new Vec3d(searcher.posX, searcher.posY + searcher.getEyeHeight(), searcher.posZ), new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ), false, true);
    }


    public static ExplicitPriorityQueue<EntityLivingBase> seenEntities(EntityPlayerMP player)
    {
        ExplicitPriorityQueue<EntityLivingBase> queue = new ExplicitPriorityQueue<>(10);
        double stealthLevel;
        Entity[] loadedEntities = player.world.loadedEntityList.toArray(new Entity[player.world.loadedEntityList.size()]);

        if (serverSettings.senses.usePlayerSenses)
        {
            for (Entity entity : loadedEntities)
            {
                if (entity instanceof EntityLivingBase && entity != player)
                {
                    stealthLevel = visualStealthLevel(player, entity, true, true);
                    if (stealthLevel <= 1) queue.add((EntityLivingBase) entity, stealthLevel);
                }
            }
        }
        else
        {
            for (Entity entity : loadedEntities)
            {
                if (entity instanceof EntityLivingBase && entity != player)
                {
                    double distSquared = player.getDistanceSq(entity);
                    if (distSquared <= 2500 && los(player, entity))
                    {
                        double angleDif = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYawHead).normalize().dotProduct(new Vec3d(entity.posX - player.posX, entity.posY - player.posY, entity.posZ - player.posZ).normalize());

                        //And because Vec3d.fromPitchYaw occasionally returns values barely out of the range of (-1, 1)...
                        if (angleDif < -1) angleDif = -1;
                        else if (angleDif > 1) angleDif = 1;

                        angleDif = TRIG_TABLE.arccos(angleDif); //0 in front, pi in back

                        if (angleDif / Math.PI * 180 <= 70) queue.add((EntityLivingBase) entity, Math.pow(angleDif, 2) * distSquared);
                    }
                }
            }
        }

        return queue;
    }
}
