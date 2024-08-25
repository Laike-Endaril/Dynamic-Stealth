package com.fantasticsource.dynamicstealth.compat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;

import java.util.UUID;

public class CompatEBWizardry
{
    public static Potion
            mindTrickPotion,
            mindControlPotion;


    public static UUID mindControllerUUID(EntityLivingBase entity)
    {
        if (mindControlPotion == null || entity.getActivePotionEffect(mindControlPotion) == null) return null;

        NBTTagCompound compound = entity.getEntityData();
        if (!compound.hasKey("controllingEntityLeast")) return null;

        return compound.getUniqueId("controllingEntity");
    }

    public static boolean mindControllerIs(EntityLivingBase entity, EntityLivingBase controller)
    {
        if (mindControlPotion == null) return false;


        UUID id = mindControllerUUID(entity);
        if (controller == null) return id == null;

        return controller.getUniqueID().equals(id);
    }

    public static boolean summonerIs(EntityLivingBase entity, EntityLivingBase summoner)
    {
        if (mindControlPotion == null) return false;


        NBTTagCompound compound = new NBTTagCompound();
        entity.writeToNBT(compound);
        return compound.hasKey("casterUUIDLeast") && summoner.getUniqueID().equals(compound.getUniqueId("casterUUID"));
    }
}
