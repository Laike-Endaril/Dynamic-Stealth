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

        NBTTagCompound compound = entity.writeToNBT(new NBTTagCompound());
        if (!compound.hasKey("ForgeData")) return null;

        compound = compound.getCompoundTag("ForgeData");
        if (!compound.hasKey("controllingEntityLeast")) return null;

        return compound.getUniqueId("controllingEntity");
    }

    public static boolean mindControllerIs(EntityLivingBase entity, EntityLivingBase controller)
    {
        UUID id = mindControllerUUID(entity);
        if (controller == null) return id == null;

        return controller.getUniqueID().equals(id);
    }
}
