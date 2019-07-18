package com.fantasticsource.dynamicstealth.config.server.threat;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatDefaults;
import net.minecraftforge.common.config.Config;

public class SpecificThreatConfig
{
    @Config.Name("Threat Bypass")
    @Config.LangKey(DynamicStealth.MODID + ".config.entitySpecificBypass")
    @Config.Comment(
            {
                    "Entities in this bypass the threat system",
                    "",
                    "This means they will not use the search AI, will appear with ???? as their target and threat level in the targeting HUD, and always appear as full alert in the on-point HUD",
                    "",
                    "For some entities, this option is necessary for them to work right, such as slimes.  For others, like the ender dragon and other players, it has no effect besides how they appear in the HUD",
                    "",
                    "You can also specify entities with a certain name, like so:",
                    "modid:entity:name"
            })
    public String[] threatBypass = EntityThreatDefaults.threatBypassDefaults.toArray(new String[0]);

    @Config.Name("Passiveness")
    @Config.LangKey(DynamicStealth.MODID + ".config.entityPassiveness")
    @Config.Comment(
            {
                    "Sets whether the threat system detects and entity as passive or not",
                    "",
                    "entityID, passivity",
                    "",
                    "eg...",
                    "",
                    "minecraft:skeleton, true",
                    "",
                    "You can also specify entities with a certain name, like so:",
                    "modid:entity:name"
            })
    public String[] isPassive = EntityThreatDefaults.passiveDefaults.toArray(new String[0]);
}
