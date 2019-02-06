package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import com.fantasticsource.dynamicstealth.server.senses.EntitySightDefaults;
import net.minecraftforge.common.config.Config;

public class SpecificSightConfig
{
    @Config.Name("Angle")
    @Config.Comment(
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
    @Config.Comment(
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
    @Config.Comment(
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

    @Config.Name("Naturally Bright")
    @Config.Comment({"Entities in this list are always treated as if they are standing in max light level"})
    @Config.RequiresMcRestart
    public String[] naturallyBrightEntities = EntitySightDefaults.naturallyBrightDefaults.toArray(new String[EntitySightDefaults.naturallyBrightDefaults.size()]);

    @Config.Name("Natural Nightvision")
    @Config.Comment({"Entities in this list ALWAYS get the nightvision bonus"})
    @Config.RequiresMcRestart
    public String[] naturalNightvisionMobs = new String[]
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
    @Config.Comment(
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
    @Config.Comment(
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
