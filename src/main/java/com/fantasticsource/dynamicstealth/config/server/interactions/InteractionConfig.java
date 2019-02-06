package com.fantasticsource.dynamicstealth.config.server.interactions;

import net.minecraftforge.common.config.Config;

public class InteractionConfig
{
    @Config.Name("Attack")
    public AttackConfig attack = new AttackConfig();
}
