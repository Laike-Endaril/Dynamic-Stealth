package com.fantasticsource.dynamicstealth.config.client;

import net.minecraftforge.common.config.Config;

public class EntityFadeConfig
{
    @Config.Name("Minimum Entity Opacity")
    @Config.Comment(
            {
                    "The opacity of an entity when you just barely see it",
                    "",
                    "If set to 0, entities on the edge of your vision are nearly invisible",
                    "",
                    "If set to 1, entities always appear at full visibility"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double mobOpacityMin = 0;

    @Config.Name("Full Opacity At...")
    @Config.Comment(
            {
                    "The stealth rating at or above which an entity is drawn at full opacity",
                    "",
                    "If set to 1, entities are only drawn at full opacity in the very best of visibility conditions",
                    "",
                    "Decreasing the value makes entities appear opaque at further distances and in lower lighting, etc"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double fullOpacityAt = 0.5;
}
