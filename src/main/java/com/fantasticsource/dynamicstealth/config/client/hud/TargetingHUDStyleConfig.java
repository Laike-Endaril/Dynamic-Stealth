package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class TargetingHUDStyleConfig
{
    @Config.Name("Glow")
    @Config.Comment("If true, the currently targeted entity is highlighted with a glow effect")
    public boolean glow = true;

    @Config.Name("Colored Glow")
    @Config.Comment("If this and Glow are both set to true, the currently targeted entity glows in a color pertaining to its current state instead of white")
    public boolean colorGlow = true;

    @Config.Name("Main HUD Opacity")
    @Config.Comment(
            {
                    "How visible the main targeting HUD is, transparency-wise",
                    "",
                    "0 means invisible, 1 means completely opaque"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double mainAlpha = 0.7;

    @Config.Name("Arrow Opacity")
    @Config.Comment(
            {
                    "How visible the directional indicator for the main target is, transparency-wise",
                    "This arrow is only visible when the current target is off-screen",
                    "",
                    "0 means invisible, 1 means completely opaque"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double arrowAlpha = 0.5;

    @Config.Name("Arrow Size")
    @Config.Comment(
            {
                    "The size of the directional indicator for the main target",
                    "This arrow is only visible when the current target is off-screen",
            })
    @Config.RangeInt(min = 1)
    public int arrowSize = 32;
}
