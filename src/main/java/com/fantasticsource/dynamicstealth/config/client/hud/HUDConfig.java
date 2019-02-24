package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class HUDConfig
{
    @Config.Name("Display Detail HUD")
    @Config.Comment({"If set to true AND threat detail HUD is allowed by server, displays a HUD containing threat system information"})
    public boolean displayDetailHUD = true;

    @Config.Name("Max On-Point HUD Count")
    @Config.Comment(
            {
                    "The maximum number of on-point HUDs to display",
                    "",
                    "The actual number of HUDs displayed will vary depending on targets in range and server settings"
            })
    @Config.RangeInt(min = 0)
    public int onPointHUDMax = 9999;

    @Config.Name("On-point HUD Filter")
    public OPHUDFilterConfig filterOP = new OPHUDFilterConfig();

    @Config.Name("On-point HUD Style")
    public OPHUDStyleConfig styleOP = new OPHUDStyleConfig();
}
