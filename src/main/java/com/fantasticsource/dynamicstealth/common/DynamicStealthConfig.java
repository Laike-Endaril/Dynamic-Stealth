package com.fantasticsource.dynamicstealth.common;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = DynamicStealth.MODID)
public class DynamicStealthConfig
{
    @Config.Name("Client Settings")
    public static ClientSettings clientSettings = new ClientSettings();

    public static class ClientSettings
    {
        @Config.Name("Threat System")
        @Comment({
                "The threat system decides when an entity switches from one attack target to another",
                "",
                "This is similar to threat systems found in some MMORPGs"
        })
        public ThreatSystem threat = new ThreatSystem();

        public class ThreatSystem
        {
            @Config.Name("Display HUD")
            @Comment({"If set to true AND threat HUD is allowed by server, displays a HUD containing threat system information"})
            public boolean displayHUD = true;
        }
    }

    @Config.Name("Server Settings")
    public static ServerSettings serverSettings = new ServerSettings();

    public static class ServerSettings
    {
        @Config.Name("AI")
        public AI ai = new AI();

        public class AI
        {
            @Config.Name("Head Turn Speed")
            @Comment({"How quickly entities' heads spin during eg. a search sequence"})
            @Config.RangeInt(min = 1, max = 180)
            public int headTurnSpeed = 3;

            @Config.Name("Entity-Specific Settings (Advanced)")
            public EntityAISettings y_entityOverrides = new EntityAISettings();

            public class EntityAISettings
            {
                @Config.Name("Head Turn Speed")
                @Comment({
                        "How quickly entities' heads spin during eg. a search sequence",
                        "",
                        "entityID, headTurnSpeed",
                        "",
                        "eg...",
                        "",
                        "minecraft:skeleton, 5"
                })
                @Config.RequiresMcRestart
                public String[] headTurnSpeed = new String[]{"ghast, 10"};
            }
        }


        @Config.Name("Senses")
        public Senses senses = new Senses();

        public class Senses
        {
            @Config.Name("Vision")
            public Vision vision = new Vision();

            public class Vision
            {
                @Config.Name("Stealth Multipliers")
                @Comment({
                        "Contains multipliers that increase stealth / decrease awareness",
                        "",
                        "Whichever of these multipliers is currently giving the best (lowest) multiplier is used"
                })
                public StealthMultipliers a_stealthMultipliers = new StealthMultipliers();

                public class StealthMultipliers
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
                public VisibilityMultipliers b_visibilityMultipliers = new VisibilityMultipliers();

                public class VisibilityMultipliers
                {
                    @Config.Name("Armor Multiplier (Cumulative)")
                    @Comment({
                            "An entity's visibility is multiplied by 1 + (this setting * armor)",
                            "",
                            "If set to 0, there is no effect",
                            "",
                            "If set to 0.25, an entity with 20 armor (full diamond) is 5x as likely to be seen"
                    })
                    @Config.RangeDouble(min = 0)
                    public double armorMultiplierCumulative = 0.25;

                    @Config.Name("'Alert' Multiplier")
                    @Comment({
                            "If an entity is alert, their visual perception is multiplied by this",
                            "",
                            "If set to 1, there is no effect",
                            "",
                            "If set to 2, an alerted entity can generally see targets twice as easily (but still not beyond Distance (Far))"
                    })
                    @Config.RangeDouble(min = 1)
                    public double alertMultiplier = 1.25;

                    @Config.Name("'On Fire' Multiplier")
                    @Comment({
                            "If an entity is on fire, their visibility is multiplied by this",
                            "",
                            "If set to 1, there is no effect",
                            "",
                            "If set to 2, they are twice as easy to see when on fire"
                    })
                    @Config.RangeDouble(min = 1)
                    public double onFireMultiplier = 1.5;
                }


                @Config.Name("Lighting")
                @Comment({"How much of an effect lighting has on stealth.  Nightvision is in here as well"})
                public Lighting c_lighting = new Lighting();

                public class Lighting
                {
                    @Config.Name("Light (High/Bright)")
                    @Comment({
                            "The lowest light level at which entities take no sight penalty",
                            "",
                            "Entities are harder to see in light levels lower than this"
                    })
                    @Config.RangeInt(min = 0, max = 15)
                    public int lightHigh = 8;

                    @Config.Name("Light (Low/Dark)")
                    @Comment({
                            "At or below this light level, entities cannot be seen at all",
                            "",
                            "Inclusive, so if set to 0, then in 0 lighting, entities cannot be seen by other entities"
                    })
                    @Config.RangeInt(min = -1, max = 15)
                    public int lightLow = -1;

                    @Config.Name("Night Vision Awareness Bonus")
                    @Comment({"When an entity has the nightvision effect, this value is added to their perceived light levels (and then set to 15 if larger than 15)"})
                    @Config.RangeInt(min = 0, max = 15)
                    public int nightVisionAddition = 15;
                }


                @Config.Name("Speed")
                @Comment({"How much of an effect an entity's speed has on stealth"})
                public Speeds d_speeds = new Speeds();

                public class Speeds
                {
                    @Config.Name("Speed (High/Fast)")
                    @Comment({"If moving at this speed or above, an entity has the maximum speed penalty to their stealth rating"})
                    public double speedHigh = 5.6;

                    @Config.Name("Speed (Low/Slow)")
                    @Comment({"At or below this speed, an entity has no speed penalty to their stealth rating"})
                    public double speedLow = 0;
                }


                @Config.Name("Angle")
                @Comment({"FOV angles"})
                public Angles e_angles = new Angles();

                public class Angles
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
                    public int angleLarge = 85;

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
                    public int angleSmall = 30;
                }


                @Config.Name("Distance")
                @Comment({"FOV distances"})
                public Distance f_distances = new Distance();

                public class Distance
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
                    public int distanceNear = 5;
                }


                @Config.Name("Absolute Cases")
                @Comment({"Special cases, eg. glowing"})
                public Absolutes g_absolutes = new Absolutes();

                public class Absolutes
                {
                    @Config.Name("See Glowing?")
                    @Comment({
                            "If set to true, glowing entities will be seen when inside another entity's FOV, ignoring all other factors",
                            "",
                            "Allows entities to see invisible players who are glowing, but does not remove invisibility; if glowing runs out before invisibility, you're hard to see again"
                    })
                    public boolean seeGlowing = true;
                }


                @Config.Name("Entity-Specific Settings (Advanced)")
                public EntityVisionSettings y_entityOverrides = new EntityVisionSettings();

                public class EntityVisionSettings
                {
                    @Config.Name("Angle")
                    @Comment({
                            "How wide an entity's vision is, near and far away",
                            "",
                            "entityID, angleLarge, angleSmall",
                            "",
                            "eg...",
                            "",
                            "minecraft:skeleton, 90, 45"
                    })
                    @Config.RequiresMcRestart
                    public String[] angle = new String[]
                            {
                                    "ghast, 90, 0",
                                    "wither, 90, 0",
                                    "ender_dragon, 90, 0"
                            };

                    @Config.Name("Distance")
                    @Comment({
                            "How far an entity can see, at the edge of its vision and at its focal point",
                            "",
                            "entityID, distanceFar, distanceNear",
                            "",
                            "eg...",
                            "",
                            "minecraft:skeleton, 40, 3"
                    })
                    @Config.RequiresMcRestart
                    public String[] distance = new String[]
                            {
                                    "ghast, 80, 20",
                                    "wither, 100, 30",
                                    "ender_dragon, 100, 60"
                            };

                    @Config.Name("Lighting")
                    @Comment({
                            "How well an entity sees in the dark",
                            "",
                            "entityID, lightHigh, lightLow",
                            "",
                            "eg...",
                            "",
                            "minecraft:skeleton, 15, -1"
                    })
                    @Config.RequiresMcRestart
                    public String[] lighting = new String[]
                            {
                                    "ghast, 0, -1",
                                    "wither, 0, -1",
                                    "ender_dragon, 0, -1"
                            };

                    @Config.Name("Natural Night Vision")
                    @Comment({"Entities in this list ALWAYS get the night vision bonus"})
                    @Config.RequiresMcRestart
                    public String[] naturalNightVisionMobs = new String[]{
                            "squid",
                            "guardian",
                            "elder_guardian",
                            "sheep",
                            "cow",
                            "mooshroom",
                            "ocelot",
                            "wolf",
                            "polar_bear",
                            "silverfish",
                            "endermite",
                            "enderman",
                            "ender_dragon",
                            "wither",
                            "vex",
                            "ghast"
                    };

                    @Config.Name("Speed")
                    @Comment({
                            "How sensitive an entity's sight is to movement",
                            "",
                            "entityID, speedHigh, speedLow",
                            "",
                            "eg...",
                            "",
                            "minecraft:skeleton, 5.6, 0"
                    })
                    @Config.RequiresMcRestart
                    public String[] speed = new String[]{"ghast, 3, 3"};
                }
            }
        }


        @Config.Name("Threat System")
        @Comment({
                "The threat system decides when an entity switches from one attack target to another",
                "",
                "This is similar to threat systems found in some MMORPGs"
        })
        public ThreatSystem threat = new ThreatSystem();

        public class ThreatSystem
        {
            @Config.Name("Allow HUD display on clients")
            @Comment({
                    "If enabled, clients are allowed to turn on a HUD for displaying threat information",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
            @Config.RangeInt(min = 0, max = 2)
            public int allowClientHUD = 2;

            @Config.Name("'Attacked By Same' Multiplier")
            @Comment({"When an in-combat entity is attacked by its current target, its threat is increased by the damage taken times this, divided by its max HP"})
            @Config.RangeDouble(min = 1)
            public double attackedThreatMultiplierTarget = 6000;

            @Config.Name("'Attacked By Other' Multiplier")
            @Comment({"When an in-combat entity is attacked by something that is *not* its current target, its threat is decreased by damage taken times this, divided by its max HP"})
            @Config.RangeDouble(min = 1)
            public double attackedThreatMultiplierOther = 6000;

            @Config.Name("Initial Attack Multiplier")
            @Comment({"When an out-of-combat entity is attacked, its threat is set to the damage taken times this, divided by its max HP"})
            @Config.RangeDouble(min = 1)
            public double attackedThreatMultiplierInitial = 6000;

            @Config.Name("Initial 'Target Spotted' Threat")
            @Comment({"When an out-of-combat entity spots a valid target, its threat is set to this"})
            @Config.RangeInt(min = 1)
            public int targetSpottedThreat = 300;

            @Config.Name("Maximum Threat")
            @Comment({"The maximum threat level an entity can reach"})
            @Config.RangeInt(min = 1)
            public int maxThreat = 1000;

            @Config.Name("Recognize Passives Automatically")
            @Comment({"If enabled, clients' threat HUDs will display green for passive mobs"})
            public boolean recognizePassive = true;

            @Config.Name("Unseen Target Minimum Threat Level")
            @Comment({"If an entity's threat level falls below this and they don't see their target, they go out-of-combat / stop searching"})
            @Config.RangeInt(min = 0)
            public int unseenMinimumThreat = 0;

            @Config.Name("Unseen Target Degredation Rate")
            @Comment({"Every time an entity updates and their target's position is unknown, this is subtracted from their threat"})
            @Config.RangeInt(min = 1)
            public int unseenTargetDegredationRate = 1;
        }


        @Config.Name("Other Settings")
        @Comment({"Stuff that doesn't fit in other categories"})
        public OtherSettings z_otherSettings = new OtherSettings();

        public class OtherSettings
        {
            @Config.Name("Remove Invisibility On Hit")
            @Comment({"If set to true, when one living entity hits another living entity, they both lose invisibility"})
            public boolean removeInvisibilityOnHit = true;

            @Config.Name("Remove Blindness On Hit")
            @Comment({"If set to true, when one living entity hits another living entity, they both lose blindness"})
            public boolean removeBlindnessOnHit = true;
        }
    }
}
