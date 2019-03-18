package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class AngleConfig
{
    @Config.Name("Angle (Large/Wide; Near)")
    @Config.LangKey(DynamicStealth.MODID + ".config.angleNear")
    @Config.Comment(
            {
                    "The angle inside which an entity can see another entity at close range (distance <= distanceNear), in degrees",
                    "",
                    "This is otherwise similar to angleSmall",
                    "",
                    "If this and angleSmall are both 0, entities (other than exceptions) are blind",
                    "",
                    "Cannot be smaller than angleSmall"
            })
    @Config.RangeInt(min = 0, max = 180)
    public int angleLarge = 85;

    @Config.Name("Angle (Small/Thin; Far)")
    @Config.LangKey(DynamicStealth.MODID + ".config.angleFar")
    @Config.Comment(
            {
                    "The angle inside which an entity can see another entity at long range (distance >= distanceFar, in degrees",
                    "",
                    "This is a cone-shaped FOV, and the setting is the angle between the center axis of the cone and the outer surface of the cone",
                    "",
                    "Unless you make it > 90* in which case it's everywhere *except* a cone behind the entity, but w/e",
                    "",
                    "Also, it's technically not a cone shape with other settings accounted for, but let's not get into that",
                    "",
                    "If this and angleLarge are both 0, entities (other than exceptions) are blind",
                    "",
                    "Cannot be larger than angleLarge"
            })
    @Config.RangeInt(min = 0, max = 180)
    public int angleSmall = 30;
}
