package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.server.configdata.EntityThreatDefaults;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = DynamicStealth.MODID)
public class DynamicStealthConfig
{
    @Config.Name("Client Settings")
    public static ClientSettings clientSettings = new ClientSettings();
    @Config.Name("Server Settings")
    public static ServerSettings serverSettings = new ServerSettings();

    public static class ClientSettings
    {
        @Config.Name("Threat System")
        @Comment(
                {
                        "The threat system decides when an entity switches from one attack target to another",
                        "",
                        "This is similar to threat systems found in some MMORPGs"
                })
        public ThreatSystem threat = new ThreatSystem();

        @Config.Name("Other Settings")
        @Comment({"Stuff that doesn't fit in other categories"})
        public OtherClientSettings z_otherSettings = new OtherClientSettings();

        public class OtherClientSettings
        {
            @Config.Name("Minimum Entity Opacity")
            @Comment(
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
            @Comment(
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

        public class ThreatSystem
        {
            @Config.Name("Display Detail HUD")
            @Comment({"If set to true AND threat detail HUD is allowed by server, displays a HUD containing threat system information"})
            public boolean displayDetailHUD = true;

            @Config.Name("Max On-Point HUD Count")
            @Comment(
                    {
                            "The maximum number of on-point HUDs to display",
                            "",
                            "The actual number of HUDs displayed will vary depending on targets in range and server settings"
                    })
            @Config.RangeInt(min = 0)
            public int onPointHUDMax = 9999;

            @Config.Name("On-point HUD Filter")
            public OnPointHUDFilter filter = new OnPointHUDFilter();
            @Config.Name("On-point HUD Style")
            public OnPointHUDStyle onPointHUDStyle = new OnPointHUDStyle();

            public class OnPointHUDFilter
            {
                @Config.Name("Passive")
                @Comment({"If true, on-point indicators appear for passive mobs"})
                public boolean showPassive = true;

                @Config.Name("Bypass")
                @Comment({"If true, on-point indicators appear for mobs that bypass the threat system"})
                public boolean showBypass = true;

                @Config.Name("Idle")
                @Comment({"If true, on-point indicators appear for idle mobs"})
                public boolean showIdle = true;

                @Config.Name("Attacking Other")
                @Comment({"If true, on-point indicators appear for mobs which are attacking something besides you"})
                public boolean showAttackingOther = true;

                @Config.Name("Alert")
                @Comment({"If true, on-point indicators appear for alerted mobs who are actively searching for a target"})
                public boolean showAlert = true;

                @Config.Name("Attacking You")
                @Comment({"If true, on-point indicators appear for mobs that are attacking YOU"})
                public boolean showAttackingYou = true;

                @Config.Name("Flee")
                @Comment({"If true, on-point indicators appear for mobs that are fleeing from combat"})
                public boolean showFleeing = true;
            }

            public class OnPointHUDStyle
            {
                @Config.Name("0: Use Depth")
                @Comment(
                        {
                                "If false, on-point HUDs will display on top of blocks and models, but below shadows (bit glitchy when overlapping shadows)",
                                "",
                                "If true, on-point HUDs will display on top of shadows correctly, but at their position in-world, ie. they can be hidden behind blocks/models"
                        })
                public boolean depth = true;

                @Config.Name("1: 3D Vertical Percentage")
                @Comment(
                        {
                                "3D position height is <this setting * entity height + vertical offset>",
                                "",
                                "Basically, if you want to use the top of the head as the base position (and then add offsets), set this to 1",
                                "",
                                "At the feet would be 0, and 0.5 would make the base position the 3D center of the entity"
                        })
                public double verticalPercent = 1;

                @Config.Name("2: Account For Sneaking")
                @Comment({"If set to true, vertical position is shifted down a bit when the entity is sneaking, similar to default nameplate behavior"})
                public boolean accountForSneak = true;

                @Config.Name("3: 3D Vertical Offset")
                @Comment(
                        {
                                "3D position height is <vertical percentage * entity height + this setting>",
                                "",
                                "So if you want the 3D position to be half a  block above the head (synced with nameplate), set vertical percentage to 1, and this setting to 0.5"
                        })
                public double verticalOffset = 0.5;

                @Config.Name("4: 3D Horizontal Percentage")
                @Comment(
                        {
                                "This setting alters the horizontal 3D position *after* rotation happens",
                                "",
                                "If you set this to 0.5, it will be centered on the left side of the entity, and -0.5 will be centered on the right"
                        })
                public double horizontalPercent = 0;

                @Config.Name("5: 2D Horizontal Offset")
                @Comment({"Slides the indicator left and right in relation to your screen"})
                public double horizontalOffset2D = 0;

                @Config.Name("5: 2D Vertical Offset")
                @Comment({"Slides the indicator up and down in relation to your screen"})
                public double verticalOffset2D = -10;

                @Config.Name("7: Scale")
                @Comment({"The scale of the indicator itself; how big the indicator is"})
                public double scale = 0.5;
            }
        }
    }

    public static class ServerSettings
    {
        @Config.Name("AI")
        public AI ai = new AI();

        @Config.Name("Senses")
        public Senses senses = new Senses();

        @Config.Name("Threat System")
        @Comment(
                {
                        "The threat system decides when an entity switches from one attack target to another",
                        "",
                        "This is similar to threat systems found in some MMORPGs"
                })
        public ThreatSystem threat = new ThreatSystem();

        @Config.Name("Helper System")
        @Comment("Which entities come to the aid of which other entities")
        public HelperSystemSettings helperSystemSettings = new HelperSystemSettings();

        @Config.Name("Other Settings")
        @Comment({"Stuff that doesn't fit in other categories"})
        public OtherSettings z_otherSettings = new OtherSettings();

        @Config.Name("Items")
        public ItemSettings itemSettings = new ItemSettings();
        @Config.Name("Flee Mechanic")
        public FleeSettings flee = new FleeSettings();

        public class ItemSettings
        {
            @Config.Name("Potions")
            public PotionSettings potionSettings = new PotionSettings();

            public class PotionSettings
            {
                @Config.Name("Soul Sight Potions")
                @Comment("If set to true, the game will load soul sight potions (accessible from the brewing tab of the creative menu)")
                @Config.RequiresMcRestart
                public boolean soulSightPotion = true;

                @Config.Name("Soul Sight Potion Recipes")
                @Comment("If set to true, players can brew soul sight potions with ender eyes + thick potions")
                @Config.RequiresMcRestart
                public boolean soulSightPotionRecipe = true;
            }
        }

        public class HelperSystemSettings
        {
            @Config.Name("Ownership")
            public Ownership ownership = new Ownership();

            @Config.Name("Teams")
            public Teams teams = new Teams();

            @Config.Name("Custom NPCs Factions")
            @Comment("These settings only matter if Custom NPCs is installed")
            public CNPCFactions cnpcFactions = new CNPCFactions();

            @Config.Name("Help Same Entity Type")
            @Comment(
                    {
                            "Whether to help entities of the same type",
                            "",
                            "Eg. if set to true, skeletons will help other skeletons (but not zombies)"
                    })
            public boolean helpSameType = true;

            public class Ownership
            {
                @Config.Name("Help Owner")
                @Comment("Help our owner (if we have one)")
                public boolean helpOwner = true;

                @Config.Name("Help If Same Owner")
                @Comment("Help them if we both have the same owner (if we have one)")
                public boolean helpOtherWithSameOwner = true;

                @Config.Name("Dedicated")
                @Comment("DON'T help them if they don't have the same owner (if we have one)")
                public boolean dedicated = true;

                @Config.Name("Help Owned")
                @Comment("Help them if we own them")
                public boolean helpOwned = true;
            }

            public class Teams
            {
                @Config.Name("Help Same Team")
                @Comment("Help other entities on our team")
                public boolean helpSame = true;

                @Config.Name("Don't Help Other Teams")
                @Comment("Make sure NOT to help entities on other teams")
                public boolean dontHelpOther = true;
            }

            public class CNPCFactions
            {
                @Config.Name("Help If Good Rep")
                @Comment("Help npcs in the same faction and players with good faction rep")
                public boolean helpGoodRep = true;

                @Config.Name("Don't Help If Bad Rep")
                @Comment("Make sure NOT to help those we're hostile to")
                public boolean dontHelpBadRep = true;
            }
        }

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
                @Comment(
                        {
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

        public class Senses
        {
            @Config.Name("Use player senses")
            @Comment(
                    {
                            "If enabled, stealth mechanics work on players",
                            "",
                            "Basically if this is turned on and that skeleton is holding still in the dark, you might not be able to see him until you get close"
                    })
            @Config.RequiresMcRestart
            public boolean usePlayerSenses = true;

            @Config.Name("Touch")
            public Touch touch = new Touch();
            @Config.Name("Vision")
            public Vision vision = new Vision();

            public class Touch
            {
                @Config.Name("Enable Touch")
                @Comment({"If true, entities can feel each other if they bump into one another"})
                @Config.RequiresMcRestart
                public boolean touchEnabled = true;

                @Config.Name("Unfeeling")
                @Comment({"Entities in this list don't notice when something bumps them"})
                @Config.RequiresMcRestart
                public String[] unfeelingEntities = new String[]
                        {
                                "zombie",
                                "husk"
                        };
            }

            public class Vision
            {
                @Config.Name("Stealth Multipliers")
                @Comment(
                        {
                                "Contains multipliers that increase stealth / decrease awareness",
                                "",
                                "Whichever of these multipliers is currently giving the best (lowest) multiplier is used"
                        })
                public StealthMultipliers a_stealthMultipliers = new StealthMultipliers();
                @Config.Name("Visibility Multipliers")
                @Comment(
                        {
                                "Contains multipliers that decrease stealth / increase awareness",
                                "",
                                "Whichever of these multipliers is currently giving the worst (highest) multiplier is used"
                        })
                public VisibilityMultipliers b_visibilityMultipliers = new VisibilityMultipliers();
                @Config.Name("Lighting")
                @Comment({"How much of an effect lighting has on stealth.  Nightvision is in here as well"})
                public Lighting c_lighting = new Lighting();
                @Config.Name("Speed")
                @Comment({"How much of an effect an entity's speed has on stealth"})
                public Speeds d_speeds = new Speeds();
                @Config.Name("Angle")
                @Comment({"FOV angles"})
                public Angles e_angles = new Angles();
                @Config.Name("Distance")
                @Comment({"FOV distances"})
                public Distance f_distances = new Distance();
                @Config.Name("Absolute Cases")
                @Comment({"Special cases, eg. glowing"})
                public Absolutes g_absolutes = new Absolutes();
                @Config.Name("Entity-Specific Settings (Advanced)")
                public EntityVisionSettings y_entityOverrides = new EntityVisionSettings();

                public class StealthMultipliers
                {
                    @Config.Name("Crouching Multiplier")
                    @Comment(
                            {
                                    "Multiplies an entity's visibility by this decimal when crouching",
                                    "",
                                    "If set to 1, there is no effect",
                                    "",
                                    "If set to 0, crouching entities are invisible (except in special cases)"
                            })
                    @Config.RangeDouble(min = 0, max = 1)
                    public double crouchingMultiplier = 0.75;

                    @Config.Name("Mob Head Multiplier")
                    @Comment(
                            {
                                    "When an entity (including a player) is wearing a mob head, mobs of that type have reduced chance to realize they're a target",
                                    "",
                                    "If set to 1, there is no effect",
                                    "",
                                    "If set to 0, mobs of the mob head type cannot notice entities wearing their heads"
                            })
                    @Config.RangeDouble(min = 0, max = 1)
                    public double mobHeadMultiplier = 0.5;

                    @Config.Name("Invisibility Multiplier")
                    @Comment(
                            {
                                    "Invisible entities' visibility is multiplied by this",
                                    "",
                                    "If set to 1, there is no effect",
                                    "",
                                    "If set to 0, invisible entities are, uh...invisible"
                            })
                    @Config.RangeDouble(min = 0, max = 1)
                    public double invisibilityMultiplier = 0.1;

                    @Config.Name("Blindness Multiplier")
                    @Comment(
                            {
                                    "Blinded entities' detection range is multiplied by this",
                                    "",
                                    "If set to 1, there is no effect",
                                    "",
                                    "If set to 0, blind entities can't see"
                            })
                    @Config.RangeDouble(min = 0, max = 1)
                    public double blindnessMultiplier = 0.5;
                }

                public class VisibilityMultipliers
                {
                    @Config.Name("Armor Multiplier (Cumulative)")
                    @Comment(
                            {
                                    "An entity's visibility is multiplied by 1 + (this setting * armor)",
                                    "",
                                    "If set to 0, there is no effect",
                                    "",
                                    "If set to 0.25, an entity with 20 armor (full diamond) is 5x as likely to be seen"
                            })
                    @Config.RangeDouble(min = 0)
                    public double armorMultiplierCumulative = 0.25;

                    @Config.Name("'Alert' Multiplier")
                    @Comment(
                            {
                                    "If an entity is alert, their visual perception is multiplied by this",
                                    "",
                                    "If set to 1, there is no effect",
                                    "",
                                    "If set to 2, an alerted entity can generally see targets twice as easily (but still not beyond Distance (Far))"
                            })
                    @Config.RangeDouble(min = 1)
                    public double alertMultiplier = 1.25;

                    @Config.Name("'Seen' Multiplier")
                    @Comment(
                            {
                                    "If an entity has recently seen its target, their visual perception is multiplied by this",
                                    "",
                                    "If set to 1, there is no effect",
                                    "",
                                    "If set to 2, the searcher can generally see targets twice as easily (but still not beyond Distance (Far))"
                            })
                    @Config.RangeDouble(min = 1)
                    public double seenMultiplier = 2;

                    @Config.Name("'On Fire' Multiplier")
                    @Comment(
                            {
                                    "If an entity is on fire, their visibility is multiplied by this",
                                    "",
                                    "If set to 1, there is no effect",
                                    "",
                                    "If set to 2, they are twice as easy to see when on fire"
                            })
                    @Config.RangeDouble(min = 1)
                    public double onFireMultiplier = 1.5;
                }

                public class Lighting
                {
                    @Config.Name("Light (High/Bright)")
                    @Comment(
                            {
                                    "The lowest light level at which entities take no sight penalty",
                                    "",
                                    "Entities are harder to see in light levels lower than this"
                            })
                    @Config.RangeInt(min = 0, max = 15)
                    public int lightHigh = 8;

                    @Config.Name("Light (Low/Dark)")
                    @Comment(
                            {
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

                public class Speeds
                {
                    @Config.Name("Speed (High/Fast)")
                    @Comment({"If moving at this speed or above, an entity has the maximum speed penalty to their stealth rating"})
                    public double speedHigh = 5.6;

                    @Config.Name("Speed (Low/Slow)")
                    @Comment({"At or below this speed, an entity has no speed penalty to their stealth rating"})
                    public double speedLow = 0;
                }

                public class Angles
                {
                    @Config.Name("Angle (Large/Wide; Near)")
                    @Comment(
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
                    @Comment(
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

                public class Distance
                {
                    @Config.Name("Distance (Far)")
                    @Comment(
                            {
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
                    @Comment(
                            {
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

                public class Absolutes
                {
                    @Config.Name("See Glowing?")
                    @Comment(
                            {
                                    "If set to true, glowing entities will be seen when inside another entity's FOV, ignoring all other factors",
                                    "",
                                    "Allows entities to see invisible players who are glowing, but does not remove invisibility; if glowing runs out before invisibility, you're hard to see again"
                            })
                    public boolean seeGlowing = true;
                }

                public class EntityVisionSettings
                {
                    @Config.Name("Angle")
                    @Comment(
                            {
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
                                    "ender_dragon, 90, 0",
                                    "player, 70, 0"
                            };

                    @Config.Name("Distance")
                    @Comment(
                            {
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
                                    "ghast, 50, 20",
                                    "wither, 100, 30",
                                    "ender_dragon, 100, 60",
                                    "player, 50, 5"
                            };

                    @Config.Name("Lighting")
                    @Comment(
                            {
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
                    public String[] naturalNightVisionMobs = new String[]
                            {
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

                    @Config.Name("Natural Soul Sight")
                    @Comment(
                            {
                                    "Entities in this list pretty much see everything",
                                    "",
                                    "Adding the player keyword to this list makes all players see all entities as if they had glowing (visible through walls)"
                            })
                    @Config.RequiresMcRestart
                    public String[] naturalSoulSightMobs = new String[]
                            {
                                    "endermite",
                                    "enderman",
                                    "ender_dragon",
                                    "vex"
                            };

                    @Config.Name("Speed")
                    @Comment(
                            {
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

        public class FleeSettings
        {
            @Config.Name("HP Threshold")
            @Comment("The % of health at which entities start to flee")
            @Config.RangeInt(min = 0, max = 100)
            public int threshold = 25;

            @Config.Name("Degredation Rate")
            @Comment("How fast the flee gauge decreases; lower number means they flee for a longer time")
            @Config.RangeInt(min = 1)
            public int degredationRate = 3;

            @Config.Name("Damage Increases Flee Duration")
            @Comment("If set to true, then when an entity is *already* in flee mode, damage will increase its flee duration")
            public boolean increaseOnDamage = true;

            @Config.Name("CNPCs flee To Home Position")
            @Comment("If enabled, Custom NPCs flee to their home position instead of away from what hit them")
            public boolean cnpcsRunHome = false;

            @Config.Name("Fearless")
            @Comment(
                    {
                            "These entities will not use the flee mechanic when low on health",
                            "",
                            "Entities that bypass threat are automatically fearless"
                    })
            public String[] fearless = new String[]
                    {
                            "player",
                            "zombie",
                            "zombie_villager",
                            "husk",
                            "skeleton",
                            "stray",
                            "wither_skeleton",
                            "creeper",
                            "ghast",
                            "slime",
                            "enderman",
                            "ender_dragon",
                            "wither"
                    };
        }

        public class ThreatSystem
        {
            @Config.Name("Client HUD allowances")
            public HUD hud = new HUD();

            @Config.Name("'Attacked By Same' Multiplier")
            @Comment({"When an in-combat entity is attacked by its current target, its threat is increased by the damage taken times this, divided by its max HP"})
            @Config.RangeDouble(min = 1)
            public double attackedThreatMultiplierTarget = 6000;

            @Config.Name("'Attacked By Other' Multiplier")
            @Comment({"When an in-combat entity is attacked by something that is *not* its current target, its threat is decreased by damage taken times this, divided by its max HP"})
            @Config.RangeDouble(min = 1)
            public double attackedThreatMultiplierOther = 6000;

            @Config.Name("Bypass Threat System (Global)")
            @Comment({"If enabled, all entities should bypass the threat system"})
            public boolean bypassThreatSystem = false;

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

            @Config.Name("Entity-Specific Settings (Advanced)")
            public EntityThreatSettings y_entityOverrides = new EntityThreatSettings();

            public class EntityThreatSettings
            {
                @Config.Name("Threat Bypass")
                @Comment(
                        {
                                "Entities in this bypass the threat system",
                                "",
                                "This means they will not use the search AI, will appear with ???? as their target and threat level in the detail HUD, and always appear as full alert in the on-point HUD",
                                "",
                                "For some entities, this option is necessary for them to work right, such as slimes.  For others, like the ender dragon and other players, it has no effect besides how they appear in the HUD"
                        })
                @Config.RequiresMcRestart
                public String[] threatBypass = EntityThreatDefaults.threatBypassDefaults.toArray(new String[EntityThreatDefaults.threatBypassDefaults.size()]);

                @Config.Name("Passiveness")
                @Comment(
                        {
                                "Sets whether the threat system detects and entity as passive or not",
                                "",
                                "entityID, passivity",
                                "",
                                "eg...",
                                "",
                                "minecraft:skeleton, true"
                        })
                @Config.RequiresMcRestart
                public String[] isPassive = EntityThreatDefaults.passiveDefaults.toArray(new String[EntityThreatDefaults.passiveDefaults.size()]);
            }

            public class HUD
            {
                @Config.Name("Allow detailed HUD on clients")
                @Comment(
                        {
                                "If enabled, clients are allowed to turn on a HUD for displaying detailed threat information for a single target",
                                "",
                                "0 means disabled for all players",
                                "1 means enabled for OP players ONLY",
                                "2 means enabled for all players"
                        })
                @Config.RangeInt(min = 0, max = 2)
                public int allowClientDetailHUD = 2;

                @Config.Name("On-Point HUD for normal players")
                @Comment(
                        {
                                "Controls how the on-point, per-entity threat HUD can be used on clients (for normal/non-OP players)",
                                "",
                                "0 means disabled",
                                "1 means enabled for targeted entity ONLY",
                                "2 means enabled for all seen entities"
                        })
                @Config.RangeInt(min = 0, max = 2)
                public int normalOnPointHUD = 2;

                @Config.Name("On-Point HUD for OP players")
                @Comment(
                        {
                                "Controls how the on-point, per-entity threat HUD can be used on clients (for OP players)",
                                "",
                                "0 means disabled",
                                "1 means enabled for targeted entity ONLY",
                                "2 means enabled for all seen entities"
                        })
                @Config.RangeInt(min = 0, max = 2)
                public int opOnPointHUD = 2;

            }
        }

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
