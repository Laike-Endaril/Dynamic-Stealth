package com.fantasticsource.dynamicstealth.config.server.interactions;

import net.minecraftforge.common.config.Config;

public class AttackConfig
{
    @Config.Name("Remove Invisibility On Hit")
    @Config.Comment("If set to true, when one living entity hits another living entity, they both lose invisibility")
    public boolean removeInvisibilityOnHit = true;

    @Config.Name("Remove Blindness On Hit")
    @Config.Comment("If set to true, when one living entity hits another living entity, they both lose blindness")
    public boolean removeBlindnessOnHit = true;
}
