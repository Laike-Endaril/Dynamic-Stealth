package com.fantasticsource.dynamicstealth.server.senses.sight;

import com.fantasticsource.dynamicstealth.common.DSTools;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.compat.CompatDissolution;
import com.fantasticsource.dynamicstealth.config.server.senses.sight.SightConfig;
import com.fantasticsource.dynamicstealth.server.Attributes;
import com.fantasticsource.dynamicstealth.server.HUDData;
import com.fantasticsource.dynamicstealth.server.senses.HidingData;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.fantasticsource.tools.datastructures.Pair;
import com.fantasticsource.tools.datastructures.WrappingQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightData.*;
import static com.fantasticsource.mctools.ServerTickTimer.currentTick;

public class Sight
{
    private static final int SEEN_RECENT_TIMER = 60, GLOBAL_STEALTH_SMOOTHING = 3;

    private static Map<EntityPlayer, Pair<WrappingQueue<Double>, Long>> globalPlayerStealthHistory = new LinkedHashMap<>();

    private static Map<EntityLivingBase, Map<Entity, SeenData>> recentlySeenMap = new LinkedHashMap<>();
    private static Map<Pair<EntityPlayerMP, Boolean>, LinkedHashMap<EntityLivingBase, Double>> playerSeenThisTickMap = new LinkedHashMap<>();


    public static void update(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            playerSeenThisTickMap.clear();
            recentlySeenMap.entrySet().removeIf(Sight::updateRecentlySeen);
            globalPlayerStealthHistory.entrySet().removeIf(Sight::updateStealthHistory);
        }
    }

    private static boolean updateStealthHistory(Map.Entry<EntityPlayer, Pair<WrappingQueue<Double>, Long>> entry)
    {
        EntityPlayer player = entry.getKey();
        if (!player.isEntityAlive() || !player.world.playerEntities.contains(player)) return true;

        Pair<WrappingQueue<Double>, Long> pair = entry.getValue();
        long tick = currentTick();
        if (pair.getValue() != tick)
        {
            pair.getKey().add(1d);
            pair.setValue(tick);
        }
        return false;
    }

    private static boolean updateRecentlySeen(Map.Entry<EntityLivingBase, Map<Entity, SeenData>> entry)
    {
        EntityLivingBase livingBase = entry.getKey();
        if (!livingBase.isEntityAlive() || !livingBase.world.loadedEntityList.contains(livingBase)) return true;

        entry.getValue().entrySet().removeIf(e ->
        {
            Entity entity = e.getKey();
            return !entity.isEntityAlive() || !entity.world.loadedEntityList.contains(entity);
        });
        return false;
    }


    public static boolean recentlySeen(EntityLivingBase searcher, Entity target)
    {
        if (searcher == null || target == null) return false;

        Map<Entity, SeenData> map = recentlySeenMap.get(searcher);
        if (map == null) return false;

        SeenData data = map.get(target);
        if (data == null) return false;

        return data.seen && currentTick() - data.lastSeenTime < SEEN_RECENT_TIMER;
    }


    public static boolean canSee(EntityLivingBase searcher, Entity target, boolean isAggressive)
    {
        return visualStealthLevel(searcher, target, isAggressive, true, true, searcher.rotationYawHead, searcher.rotationPitch) <= 1;
    }

    public static boolean canSee(EntityLivingBase searcher, Entity target, boolean isAggressive, boolean useCache, boolean saveCache)
    {
        return visualStealthLevel(searcher, target, isAggressive, useCache, saveCache, searcher.rotationYawHead, searcher.rotationPitch) <= 1;
    }

    public static boolean canSee(EntityLivingBase searcher, Entity target, boolean isAggressive, boolean useCache, boolean saveCache, double yaw, double pitch)
    {
        return visualStealthLevel(searcher, target, isAggressive, useCache, saveCache, yaw, pitch) <= 1;
    }

    public static double visualStealthLevel(EntityLivingBase searcher, Entity target, boolean isAggressive)
    {
        return visualStealthLevel(searcher, target, isAggressive, true, true, searcher.rotationYawHead, searcher.rotationPitch);
    }

    public static double visualStealthLevel(EntityLivingBase searcher, Entity target, boolean isAggressive, boolean useCache, boolean saveCache, double yaw, double pitch)
    {
        if (searcher == null || target == null || !searcher.world.isBlockLoaded(searcher.getPosition()) || !target.world.isBlockLoaded(target.getPosition())) return 777;

        searcher.world.profiler.startSection("DStealth: Visual Stealth");
        Map<Entity, SeenData> map = recentlySeenMap.get(searcher);
        long tick = ServerTickTimer.currentTick();

        //If applicable, load from cache and return
        if (map != null && useCache)
        {
            SeenData data = map.get(target);
            if (data != null && data.lastUpdateTime == tick)
            {
                searcher.world.profiler.endSection();
                return data.lastStealthLevel;
            }
        }

        //Calculate
        double result = visualStealthLevelInternal(searcher, target, isAggressive, yaw, pitch);

        if (saveCache)
        {
            //Save first cache
            //This is where the stealth gauge checks are done
            if (isAggressive && target instanceof EntityPlayer && HUDData.isGauged(searcher))
            {
                if (searcher instanceof EntityPlayer || (searcher instanceof EntityLiving && (((EntityLiving) searcher).getAttackTarget() == target || (!EntityThreatData.isPassive(searcher) && !EntityThreatData.bypassesThreat(searcher)))))
                {
                    EntityPlayer player = (EntityPlayer) target;
                    Pair<WrappingQueue<Double>, Long> pair = globalPlayerStealthHistory.computeIfAbsent(player, k -> new Pair<>(new WrappingQueue<>(GLOBAL_STEALTH_SMOOTHING + 2), tick - 1));
                    WrappingQueue<Double> queue = pair.getKey();

                    double clampedResult = Tools.min(Tools.max(-1, result - 1), 1);
                    if (queue.size() != 0 && pair.getValue() == tick)
                    {
                        queue.setNewestToOldest(0, Tools.min(clampedResult, queue.getNewestToOldest(0)));
                    }
                    else queue.add(clampedResult);

                    pair.setValue(tick);
                }
            }

            //Save second cache
            if (map == null)
            {
                map = new LinkedHashMap<>();
                recentlySeenMap.put(searcher, map);
                map.put(target, new SeenData(result));
            }
            else
            {
                SeenData data = map.get(target);
                if (data == null) map.put(target, new SeenData(result));
                else
                {
                    data.lastUpdateTime = tick;
                    data.lastStealthLevel = result;
                    if (result <= 1)
                    {
                        data.seen = true;
                        data.lastSeenTime = tick;
                    }
                }
            }
        }

        searcher.world.profiler.endSection();
        return result;
    }


    public static LinkedHashMap<EntityLivingBase, Double> seenEntities(EntityPlayerMP player)
    {
        player.world.profiler.startSection("DStealth: Seen Entities");
        LinkedHashMap<EntityLivingBase, Double> map = playerSeenThisTickMap.get(new Pair<>(player, false));
        if (map != null)
        {
            player.world.profiler.endSection();
            return (LinkedHashMap<EntityLivingBase, Double>) map.clone();
        }

        map = seenEntitiesInternal(player);
        playerSeenThisTickMap.put(new Pair<>(player, false), (LinkedHashMap<EntityLivingBase, Double>) map.clone());
        player.world.profiler.endSection();
        return map;
    }

    private static LinkedHashMap<EntityLivingBase, Double> seenEntitiesInternal(EntityPlayerMP player)
    {
        LinkedHashMap<EntityLivingBase, Double> result = new LinkedHashMap<>();
        Entity[] loadedEntities = player.world.loadedEntityList.toArray(new Entity[0]);

        if (serverSettings.senses.usePlayerSenses)
        {
            for (Entity entity : loadedEntities)
            {
                if (entity instanceof EntityLivingBase && entity != player)
                {
                    double stealthLevel = visualStealthLevel(player, entity, true);
                    if (stealthLevel <= 1) result.put((EntityLivingBase) entity, stealthLevel);
                }
            }
        }
        else
        {
            for (Entity entity : loadedEntities)
            {
                if (entity instanceof EntityLivingBase && entity != player)
                {
                    result.put((EntityLivingBase) entity, -888d);
                }
            }
        }

        return result;
    }


    private static double visualStealthLevelInternal(EntityLivingBase searcher, Entity target, boolean isAggressive, double yaw, double pitch)
    {
        //Hard checks (absolute)
        if (searcher.world != target.world || target.isDead || target instanceof FakePlayer) return 777;


        if (target instanceof EntityPlayer)
        {
            if (searcher instanceof EntityPlayer)
            {
                if (!HidingData.isHidingFrom((EntityPlayer) target, searcher.getName())) return -777;
            }
            else if (searcher instanceof EntityLiving)
            {
                if (isAggressive && (((EntityPlayer) target).capabilities.isCreativeMode || ((EntityPlayer) target).isSpectator())) return 777;
            }
        }
        if (target instanceof EntityDragon || target instanceof EntityWither) return -777;
        if (searcher instanceof EntityPlayer && CompatDissolution.isPossessing((EntityPlayer) searcher, target)) return -777;
        if (MCTools.isRidingOrRiddenBy(searcher, target)) return -777;


        //Distance, soul sight, and angle (absolute, base FOV)
        double distSquared = searcher.getDistanceSq(target);
        int distanceFar = distanceFar(searcher);

        if (hasSoulSight(searcher))
        {
            if (distSquared > 10000) return 777;
            else return -777;
        }
        else
        {
            if (distSquared > Math.pow(distanceFar, 2)) return 777;
        }

        int angleLarge = angleLarge(searcher);
        if (angleLarge == 0) return 777;

        double distanceThreshold;
        int angleSmall = angleSmall(searcher);
        if (angleSmall == 180) distanceThreshold = distanceFar;
        else
        {
            double angleDif = MCTools.angleDifDeg(searcher.getPositionVector(), (float) yaw, (float) pitch, target.getPositionVector());
            if (angleDif > angleLarge) return 777;
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
        SightConfig sight = serverSettings.senses.sight;
        if (sight.g_absolutes.seeGlowing && isLivingBase && targetLivingBase.getActivePotionEffect(MobEffects.GLOWING) != null) return -777;


        //Attributes (absolute, factor, after angles and glowing)
        double sightAttrib = searcher.getEntityAttribute(Attributes.SIGHT).getAttributeValue();
        if (sightAttrib <= 0) return 777;

        double visReductionAttrib = !isLivingBase ? 0 : targetLivingBase.getEntityAttribute(Attributes.VISIBILITY_REDUCTION).getAttributeValue();
        double attributeMultipliers = visReductionAttrib <= 0 ? 777 : sightAttrib / visReductionAttrib;


        //Lighting and LOS checks (absolute, factor, after Angles, after Glowing)
        double lightFactor = bestLightingAtLOSHit(searcher, target, isLivingBase && isBright(targetLivingBase));
        if (lightFactor == -777) return 777;

        if (hasNightvision(searcher))
        {
            lightFactor = Math.min(15, lightFactor + sight.c_lighting.nightvisionBonus);
        }

        int lightLevelLow = lightLevelLow(searcher);
        double lightMultLow = lightMultLow(searcher);
        if (lightFactor <= lightLevelLow) lightFactor = lightMultLow;
        else
        {
            int lightLevelHigh = lightLevelHigh(searcher);
            double lightMultHigh = lightMultHigh(searcher);
            if (lightFactor >= lightLevelHigh) lightFactor = lightMultHigh;
            else
            {
                lightFactor = (lightFactor - lightLevelLow) / (lightLevelHigh - lightLevelLow) * (lightMultHigh - lightMultLow) + lightMultLow;
            }
        }


        //Blindness (multiplier)
        double blindnessMultiplier = searcher.getActivePotionEffect(MobEffects.BLINDNESS) != null ? sight.a_stealthMultipliers.blindnessMultiplier : 1;


        //Invisibility (multiplier)
        double invisibilityMultiplier = isLivingBase && targetLivingBase.getActivePotionEffect(MobEffects.INVISIBILITY) != null ? sight.a_stealthMultipliers.invisibilityMultiplier : 1;


        //Crouching (multiplier)
        double crouchingMultiplier = target.isSneaking() ? sight.a_stealthMultipliers.crouchingMultiplier : 1;


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
                    mobHeadMultiplier = sight.a_stealthMultipliers.mobHeadMultiplier;
                }
            }
            else if ((helmet.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN) && helmet.getItem() == Item.getItemFromBlock(Blocks.LIT_PUMPKIN)) && target instanceof EntitySnowman) mobHeadMultiplier = sight.a_stealthMultipliers.mobHeadMultiplier;
        }


        //Armor
        double armorMultiplier = 1;
        if (isLivingBase)
        {
            double unnaturalArmor = 0;

            for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET})
            {
                ItemStack stack = targetLivingBase.getItemStackFromSlot(slot);

                Item item = stack.getItem();
                if (item instanceof ISpecialArmor)
                {
                    if (Compat.conarm && item.getClass().getName().contains("conarm.common.items.armor")) unnaturalArmor += Math.abs(((ISpecialArmor) item).getProperties(targetLivingBase, stack, new DamageSource("generic"), 1, Integer.MIN_VALUE).Armor);
                    else unnaturalArmor += ((ISpecialArmor) item).getProperties(targetLivingBase, stack, new DamageSource("generic"), 1, Integer.MIN_VALUE).Armor;
                }
                if (item instanceof ItemArmor) unnaturalArmor += ((ItemArmor) item).damageReduceAmount;
            }
            armorMultiplier += serverSettings.senses.sight.b_visibilityMultipliers.armorMultiplierCumulative * unnaturalArmor;
        }


        //Combine multipliers
        double stealthMultiplier = Tools.min(mobHeadMultiplier, blindnessMultiplier * invisibilityMultiplier * crouchingMultiplier);
        double visibilityMultiplier = armorMultiplier;
        double configMultipliers = Tools.min(Tools.max(stealthMultiplier * visibilityMultiplier, 0), 1);


        //Final calculation
        return Math.sqrt(distSquared) / (distanceThreshold * lightFactor * configMultipliers * attributeMultipliers);
    }


    private static double bestLightingAtLOSHit(Entity searcher, Entity target, boolean forceMaxLight)
    {
        World world = searcher.world;
        if (world != target.world) return -777;

        ExplicitPriorityQueue<Vec3d> queue = new ExplicitPriorityQueue<>();

        if (forceMaxLight)
        {
            for (Vec3d vec : DSTools.entityCheckVectors(target))
            {
                queue.add(vec, 0);
            }
        }
        else
        {
            for (Vec3d vec : DSTools.entityCheckVectors(target))
            {
                queue.add(vec, 15 - DSTools.lightLevelTotal(world, vec));
            }
        }

        Vec3d testVec;
        double result;
        while (queue.size() > 0)
        {
            result = queue.peekPriority();
            testVec = queue.poll();
            if (ImprovedRayTracing.isUnobstructed(searcher.world, new Vec3d(searcher.posX, searcher.posY + searcher.getEyeHeight(), searcher.posZ), testVec, false))
            {
                return 15 - result;
            }
        }

        return -777;
    }


    public static double globalPlayerStealthLevel(EntityPlayer player)
    {
        WrappingQueue<Double> queue;
        long tick = currentTick();

        Pair<WrappingQueue<Double>, Long> pair = globalPlayerStealthHistory.get(player);
        if (pair == null)
        {
            queue = new WrappingQueue<>(GLOBAL_STEALTH_SMOOTHING + 2);
            queue.add(1d);
            globalPlayerStealthHistory.put(player, new Pair<>(queue, tick));
            return 1;
        }

        queue = pair.getKey();
        if (pair.getValue() != tick)
        {
            queue.add(1d);
            pair.setValue(tick);
        }

        int size = queue.size();
        if (size == 1) return 1;
        if (size == 2) return queue.getOldestToNewest(0);

        if (size < GLOBAL_STEALTH_SMOOTHING + 2)
        {
            double result = 1;
            for (int i = size - 2; i >= 0; i--)
            {
                result = Tools.min(result, queue.getOldestToNewest(i));
            }
            return result;
        }

        double first = queue.getOldestToNewest(0), result = 1;
        for (int i = 1; i < size - 1; i++)
        {
            result = Tools.min(result, queue.getOldestToNewest(i));
            if (result < first) break;
        }
        return result;
    }

    private static class SeenData
    {
        boolean seen = false;
        long lastSeenTime;
        long lastUpdateTime = currentTick();
        double lastStealthLevel;

        SeenData(double stealthLevel)
        {
            this(stealthLevel, false);
        }

        SeenData(double stealthLevel, boolean forceSeen)
        {
            lastStealthLevel = stealthLevel;
            if (forceSeen || stealthLevel <= 1)
            {
                seen = true;
                lastSeenTime = currentTick();
            }
        }
    }
}
