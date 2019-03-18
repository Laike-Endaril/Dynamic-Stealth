package com.fantasticsource.dynamicstealth.config.server.helpersystem;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class OwnershipConfig
{
    @Config.Name("Help Owner")
    @Config.LangKey(DynamicStealth.MODID + ".config.helpOwner")
    @Config.Comment("Help our owner (if we have one)")
    public boolean helpOwner = true;

    @Config.Name("Help If Same Owner")
    @Config.LangKey(DynamicStealth.MODID + ".config.helpSameOwner")
    @Config.Comment("Help them if we both have the same owner (if we have one)")
    public boolean helpOtherWithSameOwner = true;

    @Config.Name("Dedicated")
    @Config.LangKey(DynamicStealth.MODID + ".config.helpDedicated")
    @Config.Comment("DON'T help them if they don't have the same owner (if we have one)")
    public boolean dedicated = true;

    @Config.Name("Help Owned")
    @Config.LangKey(DynamicStealth.MODID + ".config.helpOwned")
    @Config.Comment("Help them if we own them")
    public boolean helpOwned = true;
}
