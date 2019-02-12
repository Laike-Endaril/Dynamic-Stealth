package com.fantasticsource.dynamicstealth.compat;

import ladysnake.dissolution.api.corporeality.IIncorporealHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CompatDissolution
{
    @CapabilityInject(IIncorporealHandler.class)
    private static Capability INCORPOREAL_HANDLER_CAP;

    public static boolean isPossessing(EntityPlayer player, Entity entity)
    {
        return Compat.dissolution && ((IIncorporealHandler) player.getCapability(INCORPOREAL_HANDLER_CAP, null)).getPossessed() == entity;
    }
}
