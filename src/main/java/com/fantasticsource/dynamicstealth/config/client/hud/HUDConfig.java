package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class HUDConfig
{
    @Config.Name("On-point HUD Filter")
    public OPHUDFilterConfig filterOP = new OPHUDFilterConfig();

    @Config.Name("On-point HUD Style")
    public OPHUDStyleConfig styleOP = new OPHUDStyleConfig();

    @Config.Name("Detail HUD Filter")
    public DetailHUDFilterConfig filterDetail = new DetailHUDFilterConfig();
}
