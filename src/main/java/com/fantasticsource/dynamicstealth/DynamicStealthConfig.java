package com.fantasticsource.dynamicstealth;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = DynamicStealth.MODID)
public class DynamicStealthConfig
{
    @Config.Name("Stealth Multipliers")
    @Comment({
            "Contains multipliers that increase stealth / decrease awareness",
            "",
            "Whichever of these multipliers is currently giving the best (lowest) multiplier is used"
    })
    public static StealthMultipliers a_stealthMultipliers = new StealthMultipliers();
    public static class StealthMultipliers
    {
        @Config.Name("Crouching Multiplier")
        @Comment({
                "Multiplies an entity's visibility by this decimal when crouching",
                "",
                "If set to 1, there is no effect",
                "",
                "If set to 0, crouching entities are invisible (except in special cases)"
        })
        @Config.RangeDouble(min = 0, max = 1)
        public double crouchingMultiplier = 0.75;

        @Config.Name("Mob Head Multiplier")
        @Comment({
                "When an entity (including a player) is wearing a mob head, mobs of that type have reduced chance to realize they're a target",
                "",
                "If set to 1, there is no effect",
                "",
                "If set to 0, mobs of the mob head type cannot notice entities wearing their heads"
        })
        @Config.RangeDouble(min = 0, max = 1)
        public double mobHeadMultiplier = 0.5;

        @Config.Name("Invisibility Multiplier")
        @Comment({
                "Invisible entities' visibility is multiplied by this",
                "",
                "If set to 1, there is no effect",
                "",
                "If set to 0, invisible entities are, uh...invisible"
        })
        @Config.RangeDouble(min = 0, max = 1)
        public double invisibilityMultiplier = 0.1;

        @Config.Name("Blindness Multiplier")
        @Comment({
                "Blinded entities' detection range is multiplied by this",
                "",
                "If set to 1, there is no effect",
                "",
                "If set to 0, blind entities can't see"
        })
        @Config.RangeDouble(min = 0, max = 1)
        public double blindnessMultiplier = 0.5;
    }



    @Config.Name("Visibility Multipliers")
    @Comment({
            "Contains multipliers that decrease stealth / increase awareness",
            "",
            "Whichever of these multipliers is currently giving the worst (highest) multiplier is used"
    })
    public static VisibilityMultipliers b_visibilityMultipliers = new VisibilityMultipliers();
    public static class VisibilityMultipliers
    {
        @Comment({
                "An entity's visibility is multiplied by 1 + (this setting * armor)",
                "",
                "If set to 0, there is no effect",
                "",
                "If set to 0.25, an entity with 20 armor (full diamond) is 5x as likely to be seen"
        })
        @Config.RangeDouble(min = 0)
        @Config.Name("Armor Multiplier (Cumulative)")
        public double armorMultiplierCumulative = 0.25;

        @Config.Name("\"On Fire\" Multiplier")
        @Comment({
                "An entity's visibility is multiplied by 1 + (this setting * armor)",
                "",
                "If set to 0, there is no effect",
                "",
                "If set to 0.5, an entity with 20 armor (full diamond) is 10x as likely to be seen"
        })
        @Config.RangeDouble(min = 1)
        public double onFireMultiplier = 1.5;
    }



    @Config.Name("Lighting")
    @Comment({"How much of an effect lighting has on stealth.  Nightvision is in here as well"})
    public static Lighting c_lighting = new Lighting();
    public static class Lighting
    {
        @Config.Name("Light (High/Bright)")
        @Comment({
                "The lowest light level at which entities take no sight penalty",
                "",
                "Entities are harder to see in light levels lower than this"
        })
        @Config.RangeInt(min = 0, max = 15)
        @Config.RequiresMcRestart
        public int lightHigh = 8;

        @Config.Name("Light (Low/Dark)")
        @Comment({
                "At or below this light level, entities cannot be seen at all",
                "",
                "Inclusive, so if set to 0, then in 0 lighting, entities cannot be seen by other entities"
        })
        @Config.RangeInt(min = -1, max = 15)
        @Config.RequiresMcRestart
        public int lightLow = -1;

        @Config.Name("Night Vision Awareness Bonus")
        @Comment({"When an entity has the nightvision effect, this value is added to their perceived light levels (and then set to 15 if larger than 15)"})
        @Config.RangeInt(min = 0, max = 15)
        @Config.RequiresMcRestart
        public int nightVisionAddition = 15;
    }



    @Config.Name("Speed")
    @Comment({"How much of an effect an entity's speed has on stealth"})
    public static Speeds d_speeds = new Speeds();
    public static class Speeds
    {
        @Config.Name("Speed (High/Fast)")
        @Comment({"If moving at this speed or above, an entity has the maximum speed penalty to their stealth rating"})
        @Config.RequiresMcRestart
        public double speedHigh = 5.6;

        @Config.Name("Speed (Low/Slow)")
        @Comment({"At or below this speed, an entity has no speed penalty to their stealth rating"})
        @Config.RequiresMcRestart
        public double speedLow = 0;
    }



    @Config.Name("Angle")
    @Comment({"FOV angles"})
    public static Angles e_angles = new Angles();
    public static class Angles
    {
        @Config.Name("Angle (Large/Wide; Near)")
        @Comment({
                "The angle inside which an entity can see another entity at close range (distance <= distanceNear), in degrees",
                "",
                "This is otherwise similar to angleSmall",
                "",
                "If this and angleSmall are both 0, entities (other than exceptions) are blind",
                "",
                "Cannot be smaller than angleSmall"
        })
        @Config.RangeInt(min = 0, max = 180)
        @Config.RequiresMcRestart
        public int angleLarge = 107;

        @Config.Name("Angle (Small/Thin; Far)")
        @Comment({
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
        @Config.RequiresMcRestart
        public int angleSmall = 30;
    }



    @Config.Name("Distance")
    @Comment({"FOV distances"})
    public static Distance f_distances = new Distance();
    public static class Distance
    {
        @Config.Name("Distance (Far)")
        @Comment({
                "The absolute maximum distance that an entity can see another entity from, in blocks",
                "",
                "Exclusive, so if set to 0 mobs NEVER see anything, with a few exceptions, eg...",
                "",
                "Zombies will still attack villagers as normal; this uses completely different logic than what I'm accessing atm so I'm not trying to change it for now",
                "",
                "Some mobs are not affected at all, for the same reason as stated above, including endermen and the ender dragon"
        })
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int distanceFar = 40;

        @Config.Name("Distance (Near)")
        @Comment({
                "The distance before an entity's sight starts degrading naturally (even in good conditions),  in blocks",
                "",
                "Exclusive, so if set to 0 mobs NEVER see anything, with a few exceptions, eg...",
                "",
                "...zombies will still attack villagers as normal; this uses completely different logic than what I'm accessing atm so I'm not trying to change it for now",
                "",
                "...some mobs are not affected at all, for the same reason as stated above, including endermen and the ender dragon"
        })
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int distanceNear = 5;
    }



    @Config.Name("Absolute Cases")
    @Comment({"Special cases, eg. glowing"})
    public static Absolutes g_absolutes = new Absolutes();
    public static class Absolutes
    {
        @Config.Name("See Glowing?")
        @Comment({
                "If set to true, glowing entities will be seen when inside another entity's FOV, ignoring all other factors",
                "",
                "Allows entities to see invisible players who are glowing, but does not remove invisibility; if glowing runs out before invisibility, you're hard to see again"
        })
        public boolean seeGlowing = true;
    }



    @Config.Name("Mob-Specific Settings")
    public static MobSettings y_mobOverrides = new MobSettings();
    public static class MobSettings
    {
        @Config.Name("Natural Night Vision")
        @Comment({"Entities in this list ALWAYS get the night vision bonus"})
        @Config.RequiresMcRestart
        public String[] naturalNightVisionMobs = new String[]{
                "minecraft:squid",
                "minecraft:guardian",
                "minecraft:elder_guardian",
                "minecraft:sheep",
                "minecraft:cow",
                "minecraft:mooshroom",
                "minecraft:ocelot",
                "minecraft:wolf",
                "minecraft:polar_bear",
                "minecraft:silverfish",
                "minecraft:endermite",
                "minecraft:enderman",
                "minecraft:ender_dragon",
                "minecraft:wither",
                "minecraft:vex"
        };
    }



    @Config.Name("Other Settings")
    @Comment({"Stuff that doesn't fit in other categories"})
    public static OtherSettings z_otherSettings = new OtherSettings();
    public static class OtherSettings
    {
        @Config.Name("Remove Invisibility On Hit")
        @Comment({"If set to true, when one living entity hits another living entity, they both lose invisibility"})
        public boolean removeInvisibilityOnHit = true;

        @Config.Name("Remove Blindness On Hit")
        @Comment({"If set to true, when one living entity hits another living entity, they both lose blindness"})
        public boolean removeBlindnessOnHit = true;
    }
}
