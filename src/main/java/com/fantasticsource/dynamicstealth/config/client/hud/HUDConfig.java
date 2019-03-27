package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class HUDConfig
{
    @Config.Name("Main HUD Style")
    @Config.LangKey(DynamicStealth.MODID + ".config.mainHUDStyle")
    public MainHUDStyleConfig mainStyle = new MainHUDStyleConfig();

    @Config.Name("On-point HUD Filter")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDFilter")
    public OPHUDFilterConfig ophudFilter = new OPHUDFilterConfig();

    @Config.Name("On-point HUD Style")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDStyle")
    public OPHUDStyleConfig ophudStyle = new OPHUDStyleConfig();

    @Config.Name("Targeting Filter")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingFilter")
    public TargetingFilterConfig targetingFilter = new TargetingFilterConfig();

    @Config.Name("Targeting HUD Style")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingStyle")
    public TargetingHUDStyleConfig targetingStyle = new TargetingHUDStyleConfig();
}
