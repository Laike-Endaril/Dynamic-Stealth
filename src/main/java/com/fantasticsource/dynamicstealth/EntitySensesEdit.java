package com.fantasticsource.dynamicstealth;

import com.fantasticsource.tools.Tools;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static com.fantasticsource.dynamicstealth.DynamicStealth.TRIG_TABLE;
import static com.fantasticsource.dynamicstealth.DynamicStealthConfig.*;
import static com.fantasticsource.dynamicstealth.EntityData.*;

public class EntitySensesEdit extends EntitySenses
{
    EntityLivingBase entity;
    List<Entity> seenEntities = Lists.<Entity>newArrayList();
    List<Entity> unseenEntities = Lists.<Entity>newArrayList();

    public EntitySensesEdit(EntityLiving entityIn)
    {
        super(null);
        entity = entityIn;
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

        boolean seen = !stealthCheck(entity, entityIn);

        entity.world.profiler.endSection();

        if (seen) seenEntities.add(entityIn);
        else unseenEntities.add(entityIn);

        return seen;
    }

    public static boolean stealthCheck(EntityLivingBase searcher, Entity target)
    {
        //Hard checks (absolute)
        if (searcher == null || target == null) return true;
        if (target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage) return true;
        if (!target.isEntityAlive() || angleLarge(searcher) == 0) return true;


        //Angles and Distances (absolute, base FOV)
        double distSquared = searcher.getDistanceSq(target);
        if (distSquared > distanceFarSquared(searcher)) return true;

        double distanceThreshold;
        if (angleSmall(searcher) == 180) distanceThreshold = distanceFar(searcher);
        else
        {
            //Using previous values here to give the player a chance, because client-side rendering always runs behind what's actually happening
            double angleDif = Vec3d.fromPitchYaw(searcher.prevRotationPitch, searcher.prevRotationYawHead).normalize().dotProduct(new Vec3d(target.posX - searcher.posX, target.posY - searcher.posY, target.posZ - searcher.posZ).normalize());

            //And because Vec3d.fromPitchYaw occasionally returns values barely out of the range of (-1, 1)...
            if (angleDif < -1) angleDif = -1;
            else if (angleDif > 1) angleDif = 1;

            angleDif = Tools.radtodeg(TRIG_TABLE.arccos(angleDif)); //0 in front, 180 in back
            if (angleDif > angleLarge(searcher)) return true;
            if (angleDif < angleSmall(searcher)) distanceThreshold = distanceFar(searcher);
            else distanceThreshold = distanceNear(searcher) + distanceRange(searcher) * (angleLarge(searcher) - angleDif) / angleRange(searcher);
        }


        //Glowing (absolute, after Angles)
        if (g_absolutes.seeGlowing && target.isGlowing()) return false;


        //LOS check (absolute, after Angles, after Glowing)
        if (!los(searcher, target)) return true;


        //Lighting (absolute, factor, after Angles, after Glowing, after LOS)
        double lightFactor = light(target);
        if (searcher.getActivePotionEffect(MobEffects.NIGHT_VISION) != null || naturalNightVision(searcher))
        {
            lightFactor = Math.min(15, lightFactor + c_lighting.nightVisionAddition);
        }

        if (lightFactor <= lightLow(searcher)) return true;
        lightFactor = lightFactor >= lightHigh(searcher) ? 1 : lightFactor / lightRange(searcher);


        //Speeds (factor)
        double speedFactor = Speedometer.getSpeed(target);
        speedFactor = speedFactor >= speedHigh(searcher) ? 1 : speedFactor <= speedLow(searcher) ? 0 : (speedFactor - speedLow(searcher)) / speedRange(searcher);


        //Blindness (multiplier)
        double blindnessMultiplier = searcher.getActivePotionEffect(MobEffects.BLINDNESS) != null ? a_stealthMultipliers.blindnessMultiplier : 1;


        //Invisibility (multiplier)
        boolean isLivingBase = target instanceof EntityLivingBase;
        EntityLivingBase targetLiving = isLivingBase ? (EntityLivingBase) target : null;
        double invisibilityMultiplier = isLivingBase && targetLiving.getActivePotionEffect(MobEffects.INVISIBILITY) != null ? a_stealthMultipliers.invisibilityMultiplier : 1;


        //Crouching (multiplier)
        double crouchingMultiplier = target.isSneaking() ? a_stealthMultipliers.crouchingMultiplier : 1;


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
                    mobHeadMultiplier = a_stealthMultipliers.mobHeadMultiplier;
                }
            }
            else if ((helmet.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN) && helmet.getItem() == Item.getItemFromBlock(Blocks.LIT_PUMPKIN)) && target instanceof EntitySnowman) mobHeadMultiplier = a_stealthMultipliers.mobHeadMultiplier;
        }


        //Armor
        double armorMultiplier = isLivingBase ? Math.max(0, 1 + b_visibilityMultipliers.armorMultiplierCumulative * targetLiving.getTotalArmorValue()) : 1;


        //Fire
        double fireMultiplier = !isLivingBase ? 1 : !targetLiving.isBurning() ? 1 : b_visibilityMultipliers.onFireMultiplier;


        //Combine multipliers
        double stealthMultiplier = Tools.min(blindnessMultiplier, invisibilityMultiplier, crouchingMultiplier, mobHeadMultiplier);
        double visibilityMultiplier = Tools.max(armorMultiplier, fireMultiplier);
        double combinedMultiplier = Math.max(0, Math.min(1, stealthMultiplier * visibilityMultiplier));


        //Final calculation
        //Average the factors, apply the average as a multiplier to distanceThreshold, check distance between entities against threshold, and return
        return Math.sqrt(distSquared) >= distanceThreshold * (lightFactor + speedFactor) / 2 * combinedMultiplier;
    }

    public static double light(Entity entity)
    {
        BlockPos blockpos = new BlockPos(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
        return entity.world.getLightFromNeighbors(blockpos);
    }

    public static boolean los(Entity searcher, Entity target)
    {
        return LOS.rayTraceBlocks(searcher.world, new Vec3d(searcher.posX, searcher.posY + searcher.getEyeHeight(), searcher.posZ), new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ), false, true);
    }
}
