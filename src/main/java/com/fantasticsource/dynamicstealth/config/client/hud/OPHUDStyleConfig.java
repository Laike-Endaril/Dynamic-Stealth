package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class OPHUDStyleConfig
{
    @Config.Name("0: Use Depth")
    @Config.Comment(
            {
                    "If false, on-point HUDs will display on top of blocks and models, but below shadows (bit glitchy when overlapping shadows)",
                    "",
                    "If true, on-point HUDs will display on top of shadows correctly, but at their position in-world, ie. they can be hidden behind blocks/models"
            })
    public boolean depth = true;

    @Config.Name("1: 3D Vertical Percentage")
    @Config.Comment(
            {
                    "3D position height is <this setting * entity height + vertical offset>",
                    "",
                    "Basically, if you want to use the top of the head as the base position (and then add offsets), set this to 1",
                    "",
                    "At the feet would be 0, and 0.5 would make the base position the 3D center of the entity"
            })
    public double verticalPercent = 1;

    @Config.Name("2: Account For Sneaking")
    @Config.Comment({"If set to true, vertical position is shifted down a bit when the entity is sneaking, similar to default nameplate behavior"})
    public boolean accountForSneak = true;

    @Config.Name("3: 3D Vertical Offset")
    @Config.Comment(
            {
                    "3D position height is <vertical percentage * entity height + this setting>",
                    "",
                    "So if you want the 3D position to be half a  block above the head (synced with nameplate), set vertical percentage to 1, and this setting to 0.5"
            })
    public double verticalOffset = 0.5;

    @Config.Name("4: 3D Horizontal Percentage")
    @Config.Comment(
            {
                    "This setting alters the horizontal 3D position *after* rotation happens",
                    "",
                    "If you set this to 0.5, it will be centered on the left side of the entity, and -0.5 will be centered on the right"
            })
    public double horizontalPercent = 0;

    @Config.Name("5: 2D Horizontal Offset")
    @Config.Comment({"Slides the indicator left and right in relation to your screen"})
    public double horizontalOffset2D = 0;

    @Config.Name("5: 2D Vertical Offset")
    @Config.Comment({"Slides the indicator up and down in relation to your screen"})
    public double verticalOffset2D = -10;

    @Config.Name("7: Scale")
    @Config.Comment({"The scale of the indicator itself; how big the indicator is"})
    public double scale = 0.5;
}
