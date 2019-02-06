package com.fantasticsource.dynamicstealth.config.server.helpersystem;

import net.minecraftforge.common.config.Config;

public class OwnershipConfig
{
    @Config.Name("Help Owner")
    @Config.Comment("Help our owner (if we have one)")
    public boolean helpOwner = true;

    @Config.Name("Help If Same Owner")
    @Config.Comment("Help them if we both have the same owner (if we have one)")
    public boolean helpOtherWithSameOwner = true;

    @Config.Name("Dedicated")
    @Config.Comment("DON'T help them if they don't have the same owner (if we have one)")
    public boolean dedicated = true;

    @Config.Name("Help Owned")
    @Config.Comment("Help them if we own them")
    public boolean helpOwned = true;
}
