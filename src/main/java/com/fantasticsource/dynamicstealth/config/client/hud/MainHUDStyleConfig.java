package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class MainHUDStyleConfig
{
    @Config.Name("Stealth Gauge Opacity")
    @Config.Comment(
            {
                    "How visible the stealth gauge is, transparency-wise",
                    "",
                    "0 means invisible, 1 means completely opaque"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double stealthGaugeAlpha = 0.6;

    @Config.Name("Stealth Gauge Color")
    @Config.Comment(
            {
                    "The color of the stealth gauge",
                    "",
                    "This uses the format RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String stealthGaugeColor = "FFFFFF";

    @Config.Name("Stealth Gauge Rim Color")
    @Config.Comment(
            {
                    "The color of the stealth gauge's rim and arrow",
                    "",
                    "This uses the format RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String stealthGaugeRimColor = "222222";

    @Config.Name("Stealth Gauge Size")
    @Config.Comment("The size of the stealth gauge")
    @Config.RangeInt(min = 1)
    public int stealthGaugeSize = 50;

    @Config.Name("Stealth Gauge Mode")
    @Config.Comment(
            {
                    "The general display mode of the stealth gauge",
                    "",
                    "0 = None",
                    "1 = Rotational",
                    "2 = Animated",
            })
    @Config.RangeInt(min = 0, max = 2)
    public int stealthGaugeMode = 1;
}
