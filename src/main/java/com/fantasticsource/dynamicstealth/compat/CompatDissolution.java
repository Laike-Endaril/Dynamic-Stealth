package com.fantasticsource.dynamicstealth.compat;

import ladysnake.dissolution.api.corporeality.IIncorporealHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CompatDissolution
{
    @CapabilityInject(IIncorporealHandler.class)
    public static Capability INCORPOREAL_HANDLER_CAP;
}
