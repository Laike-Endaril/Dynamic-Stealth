package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class HUDConfig
{
    @Config.Name("On-point HUD Filter")
    public OPHUDFilterConfig ophudFilter = new OPHUDFilterConfig();

    @Config.Name("On-point HUD Style")
    public OPHUDStyleConfig ophudStyle = new OPHUDStyleConfig();

    @Config.Name("Targeting Filter")
    public TargetingFilterConfig targetingFilter = new TargetingFilterConfig();

    @Config.Name("Targeting HUD Style")
    public TargetingHUDStyleConfig targetingStyle = new TargetingHUDStyleConfig();
}
