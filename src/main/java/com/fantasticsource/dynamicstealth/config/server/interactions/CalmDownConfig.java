package com.fantasticsource.dynamicstealth.config.server.interactions;

import net.minecraftforge.common.config.Config;

public class CalmDownConfig
{
    @Config.Name("Recover all HP")
    @Config.Comment("If set to true, all hp is instantly recovered when calming down")
    public boolean fullHPRecovery = false;

    @Config.Name("CNPCs Warp Home")
    @Config.Comment("If set to true, cnpcs warp home when calming down")
    public boolean cnpcsWarpHome = false;
}
