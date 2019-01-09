package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.mctools.Speedometer;
import com.fantasticsource.tools.Tools;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntitySenses;
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

import java.util.List;

import static com.fantasticsource.dynamicstealth.common.DynamicStealth.TRIG_TABLE;
import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.ServerSettings;
import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.server.configdata.EntityVisionData.*;

public class EntitySensesEdit extends EntitySenses
{
    private static final ServerSettings.Senses senses = serverSettings.senses;
    private static final ServerSettings.Senses.Vision vision = senses.vision;

    EntityLivingBase entity;
    List<Entity> seenEntities = Lists.<Entity>newArrayList();
    List<Entity> unseenEntities = Lists.<Entity>newArrayList();

    public EntitySensesEdit(EntityLiving entityIn)
    {
        super(null);
        entity = entityIn;
    }

    public static double stealthLevel(EntityLivingBase searcher, Entity target)
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
        EntityLivingBase targetLiving = isLivingBase ? (EntityLivingBase) target : null;

        //Glowing (absolute, after Angles)
        if (vision.g_absolutes.seeGlowing && isLivingBase && targetLiving.getActivePotionEffect(MobEffects.GLOWING) != null) return 0;


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
        double invisibilityMultiplier = isLivingBase && targetLiving.getActivePotionEffect(MobEffects.INVISIBILITY) != null ? vision.a_stealthMultipliers.invisibilityMultiplier : 1;


        //Alerted multiplier
        double alertMultiplier = searcher instanceof EntityLiving && Threat.get(searcher).threatLevel > 0 ? vision.b_visibilityMultipliers.alertMultiplier : 1;

        //Crouching (multiplier)
        double crouchingMultiplier = target.isSneaking() ? vision.a_stealthMultipliers.crouchingMultiplier : 1;


        //Mob Heads (multiplier)
        double mobHeadMultiplier = 1;
        if (isLivingBase)
        {
            ItemStack helmet = targetLiving.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
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
        double armorMultiplier = isLivingBase ? Math.max(0, 1 + vision.b_visibilityMultipliers.armorMultiplierCumulative * targetLiving.getTotalArmorValue()) : 1;


        //Fire
        double fireMultiplier = !isLivingBase ? 1 : !targetLiving.isBurning() ? 1 : vision.b_visibilityMultipliers.onFireMultiplier;


        //Combine multipliers
        double stealthMultiplier = Tools.min(blindnessMultiplier, invisibilityMultiplier, crouchingMultiplier, mobHeadMultiplier);
        double visibilityMultiplier = Tools.max(armorMultiplier, fireMultiplier, alertMultiplier);
        double baseMultiplier = Tools.min(Tools.max((lightFactor + speedFactor) / 2 * stealthMultiplier * visibilityMultiplier, 0), 1);

        double visReduction = targetLiving.getEntityAttribute(Attributes.VISIBILITY_REDUCTION).getAttributeValue();
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

    @Override
    public void clearSensingCache()
    {
        seenEntities.clear();
        unseenEntities.clear();
    }

    @Override
    public boolean canSee(Entity entityIn)
    {
        //This should actually include not only sight, but all other senses as well, since it's the one and only sense method called by vanilla classes

        if (seenEntities.contains(entityIn)) return true;
        if (unseenEntities.contains(entityIn)) return false;

        entity.world.profiler.startSection("canSee");

        boolean seen = stealthLevel(entity, entityIn) <= 1;

        entity.world.profiler.endSection();

        if (seen) seenEntities.add(entityIn);
        else unseenEntities.add(entityIn);

        return seen;
    }
}
