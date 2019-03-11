package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class TargetingHUDStyleConfig
{
    @Config.Name("Glow")
    @Config.Comment("If true, the currently targeted entity is highlighted with a glow effect")
    public boolean glow = true;

    @Config.Name("State-Colored Glow")
    @Config.Comment("If this and Glow are both set to true, the currently targeted entity glows in a color pertaining to its current state instead of white")
    public boolean stateColoredGlow = true;

    @Config.Name("Reticle Opacity")
    @Config.Comment(
            {
                    "How visible the targeting reticle is, transparency-wise",
                    "",
                    "0 means invisible, 1 means completely opaque"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double reticleAlpha = 1;

    @Config.Name("Default Reticle Color")
    @Config.Comment(
            {
                    "The color of the targeting reticle, if state-colored reticle is false",
                    "",
                    "This uses the format RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String defaultReticleColor = "FFFFFF";

    @Config.Name("State-Colored Reticle")
    @Config.Comment("If true, the targeting reticle is drawn in a color pertaining to the target's current state")
    public boolean stateColoredReticle = true;

    @Config.Name("Reticle Spacing")
    @Config.Comment("How far the parts of the targeting reticle are from the center")
    @Config.RangeInt(min = 0)
    public int reticleSpacing = 7;

    @Config.Name("Reticle Size")
    @Config.Comment("The size of the reticle parts")
    @Config.RangeInt(min = 1)
    public int reticleSize = 10;

    @Config.Name("Text Opacity")
    @Config.Comment(
            {
                    "How visible the text of the targeting HUD is, transparency-wise",
                    "",
                    "0 means invisible, 1 means completely opaque"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double textAlpha = 0.7;

    @Config.Name("Default Text Color")
    @Config.Comment(
            {
                    "The color of the targeting HUD text, if state-colored text is false",
                    "",
                    "This uses the format RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String defaultTextColor = "FFFFFF";

    @Config.Name("State-Colored Text")
    @Config.Comment("If true, the targeting HUD text is drawn in a color pertaining to the target's current state")
    public boolean stateColoredText = true;

    @Config.Name("Text Scale")
    @Config.Comment("The scale of the text for the targeting HUD")
    @Config.RangeDouble(min = 0.1)
    public double textScale = 0.6;

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

    @Config.Name("Default Arrow Color")
    @Config.Comment(
            {
                    "The color of the directional indicator for the main target, if state-colored arrow is false",
                    "This arrow is only visible when the current target is off-screen",
                    "",
                    "This uses the format RRGGBB color format (if you google RRGGBB you'll find a color picker you can use)"
            })
    public String defaultArrowColor = "FFFFFF";

    @Config.Name("State-Colored Arrow")
    @Config.Comment(
            {
                    "If true, the directional indicator for the main target is drawn in a color pertaining to the target's current state",
                    "This arrow is only visible when the current target is off-screen"
            })
    public boolean stateColoredArrow = true;

    @Config.Name("Arrow Size")
    @Config.Comment(
            {
                    "The size of the directional indicator for the main target",
                    "This arrow is only visible when the current target is off-screen"
            })
    @Config.RangeInt(min = 1)
    public int arrowSize = 32;

    @Config.Name("Components")
    public TargetingHUDComponents components = new TargetingHUDComponents();
}
