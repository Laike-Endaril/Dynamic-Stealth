package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class MainHUDStyleConfig
{
    @Config.Name("Stealth Gauge Opacity")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeAlpha")
    @Config.Comment(
            {
                    "How visible the stealth gauge is, transparency-wise",
                    "",
                    "0 means invisible, 1 means completely opaque"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double stealthGaugeAlpha = 0.6;

    @Config.Name("Stealth Gauge Color")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeColor")
    @Config.Comment(
            {
                    "The color of the stealth gauge",
                    "",
                    "This uses the format RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String stealthGaugeColor = "FFFFFF";

    @Config.Name("Stealth Gauge Rim Color")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeRimColor")
    @Config.Comment(
            {
                    "The color of the stealth gauge's rim and arrow",
                    "",
                    "This uses the format RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String stealthGaugeRimColor = "222222";

    @Config.Name("Stealth Gauge Size")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeSize")
    @Config.Comment("The size of the stealth gauge")
    @Config.RangeInt(min = 1)
    public int stealthGaugeSize = 50;

    @Config.Name("Stealth Gauge Mode")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeMode")
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

    @Config.Name("Stealth Gauge X Position")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeX")
    @Config.Comment(
            {
                    "The x position of the stealth gauge",
                    "",
                    "0 is far left, 0.5 is center, 1 is far right"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double stealthGaugeX = 1;

    @Config.Name("Stealth Gauge Y Position")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeY")
    @Config.Comment(
            {
                    "The y position of the stealth gauge",
                    "",
                    "0 is top, 0.5 is center, 1 is bottom"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double stealthGaugeY = 1;
}
