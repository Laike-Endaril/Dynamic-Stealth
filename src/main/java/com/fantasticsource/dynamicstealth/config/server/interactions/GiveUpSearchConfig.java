package com.fantasticsource.dynamicstealth.config.server.interactions;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class GiveUpSearchConfig
{
    @Config.Name("Recover all HP")
    @Config.LangKey(DynamicStealth.MODID + ".config.giveUpSearchFullHP")
    @Config.Comment("If set to true, all hp is instantly recovered when giving up searching for a target")
    public boolean fullHPRecovery = false;

    @Config.Name("CNPCs Warp Home")
    @Config.LangKey(DynamicStealth.MODID + ".config.giveUpSearchCNPCWarp")
    @Config.Comment("If set to true, cnpcs warp home when they give up searching for a target")
    public boolean cnpcsWarpHome = false;
}
