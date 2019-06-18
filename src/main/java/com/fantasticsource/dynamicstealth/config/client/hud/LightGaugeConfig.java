package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class LightGaugeConfig
{
    @Config.Name("000 Show Light Gauge")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightGaugeShow")
    @Config.Comment("Whether to show the light gauge")
    public boolean showLightGauge = true;

    @Config.Name("010 Light Gauge Opacity")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightGaugeAlpha")
    @Config.Comment(
            {
                    "How visible the light gauge is, transparency-wise",
                    "",
                    "0 means invisible, 1 means completely opaque"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double lightGaugeAlpha = 0.4;

    @Config.Name("020 Light Gauge Color (Empty)")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightGaugeColorEmpty")
    @Config.Comment(
            {
                    "The color of an empty light gauge piece",
                    "",
                    "This uses the format RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String lightGaugeColorEmpty = "221133";

    @Config.Name("025 Light Gauge Color (Full)")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightGaugeColorFull")
    @Config.Comment(
            {
                    "The color of a filled light gauge piece",
                    "",
                    "This uses the format RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String lightGaugeColorFull = "FFFF00";

    @Config.Name("040 Light Gauge Size")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightGaugeSize")
    @Config.Comment("The size of the light gauge")
    @Config.RangeInt(min = 1)
    public int lightGaugeSize = 50;

    @Config.Name("050 Light Gauge X Position")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightGaugeX")
    @Config.Comment(
            {
                    "The x position of the light gauge",
                    "",
                    "0 is far left, 0.5 is center, 1 is far right"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double lightGaugeX = 0.5;

    @Config.Name("055 Light Gauge Y Position")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightGaugeY")
    @Config.Comment(
            {
                    "The y position of the light gauge",
                    "",
                    "0 is top, 0.5 is center, 1 is bottom"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double lightGaugeY = 0.5;
}
