package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class StealthGaugeStyleConfig
{
    @Config.Name("010 Stealth Gauge Mode")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeMode")
    @Config.Comment(
            {
                    "The general display mode of the stealth gauge",
                    "",
                    "0 = None",
                    "1 = Rotational",
                    "2 = Animated",
                    "3 = Animated, replaces vanilla cursor",
            })
    @Config.RangeInt(min = 0, max = 3)
    public int stealthGaugeMode = 2;

    @Config.Name("020 Stealth Gauge Speed")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeSpeed")
    @Config.RangeInt(min = 1)
    @Config.Comment(
            {
                    "For modes 2 and 3, lower numbers make the stealth gauge animate smoother, higher numbers make the stealth gauge more accurate"
            })
    public int stealthGaugeSpeed = 30;

    @Config.Name("030 Stealth Gauge X Position")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeX")
    @Config.Comment(
            {
                    "The x position of the stealth gauge",
                    "",
                    "0 is far left, 0.5 is center, 1 is far right"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double stealthGaugeX = 0.5;

    @Config.Name("040 Stealth Gauge Y Position")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeY")
    @Config.Comment(
            {
                    "The y position of the stealth gauge",
                    "",
                    "0 is top, 0.5 is center, 1 is bottom"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double stealthGaugeY = 0.5;

    @Config.Name("050 Stealth Gauge Size")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeSize")
    @Config.Comment("The size of the stealth gauge")
    @Config.RangeInt(min = 1)
    public int stealthGaugeSize = 32;

    @Config.Name("060 Stealth Gauge Opacity")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeAlpha")
    @Config.Comment(
            {
                    "How visible the stealth gauge is, transparency-wise",
                    "",
                    "0 means invisible, 1 means completely opaque"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double stealthGaugeAlpha = 0.4;

    @Config.Name("070 Stealth Gauge Color")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeColor")
    @Config.Comment(
            {
                    "The color of the stealth gauge",
                    "",
                    "This uses the RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String stealthGaugeColor = "FFFFFF";

    @Config.Name("080 Stealth Gauge Rim Color")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeRimColor")
    @Config.Comment(
            {
                    "The color of the stealth gauge's rim and arrow",
                    "",
                    "This uses the RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String stealthGaugeRimColor = "222222";

    @Config.Name("090 Cursor Reversion Delay")
    @Config.LangKey(DynamicStealth.MODID + ".config.cursorReversionDelay")
    @Config.RangeInt(min = 0)
    @Config.Comment(
            {
                    "For mode 3, how many ticks to wait before reverting from eye to cursor once you are fully stealthed"
            })
    public int cursorReversionDelay = 40;
}
