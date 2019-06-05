package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightDefaults;
import net.minecraftforge.common.config.Config;

public class SpecificSightConfig
{
    @Config.Name("Angle")
    @Config.LangKey(DynamicStealth.MODID + ".config.entitySpecificAngle")
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
    public String[] angle = EntitySightDefaults.angleDefaults.toArray(new String[0]);

    @Config.Name("Distance")
    @Config.LangKey(DynamicStealth.MODID + ".config.entitySpecificDistance")
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
    public String[] distance = EntitySightDefaults.distanceDefaults.toArray(new String[0]);

    @Config.Name("Lighting")
    @Config.LangKey(DynamicStealth.MODID + ".config.entitySpecificLighting")
    @Config.Comment(
            {
                    "How well an entity sees in the dark",
                    "",
                    "entityID, lightLevelHigh, lightMultHigh, lightLevelLow, lightMultLow",
                    "",
                    "eg...",
                    "",
                    "minecraft:skeleton, 15, 1, 0, 0"
            })
    public String[] lighting = new String[]{};

    @Config.Name("Naturally Bright")
    @Config.LangKey(DynamicStealth.MODID + ".config.naturallyBright")
    @Config.Comment({"Entities in this list are always treated as if they are standing in max light level"})
    public String[] naturallyBrightEntities = EntitySightDefaults.naturallyBrightDefaults.toArray(new String[0]);

    @Config.Name("Natural Nightvision")
    @Config.LangKey(DynamicStealth.MODID + ".config.naturalNightvision")
    @Config.Comment({"Entities in this list ALWAYS get the nightvision bonus"})
    public String[] naturalNightvisionMobs = EntitySightDefaults.naturalNightvisionDefaults.toArray(new String[0]);

    @Config.Name("Natural Soul Sight")
    @Config.LangKey(DynamicStealth.MODID + ".config.naturalSoulSight")
    @Config.Comment(
            {
                    "Entities in this list pretty much see everything",
                    "",
                    "Adding the player keyword to this list makes all players see all entities as if they had glowing (visible through walls)"
            })
    public String[] naturalSoulSightMobs = EntitySightDefaults.naturalSoulSightDefaults.toArray(new String[0]);
}
