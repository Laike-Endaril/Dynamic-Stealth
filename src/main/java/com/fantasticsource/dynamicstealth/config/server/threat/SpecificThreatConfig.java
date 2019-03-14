package com.fantasticsource.dynamicstealth.config.server.threat;

import com.fantasticsource.dynamicstealth.server.threat.EntityThreatDefaults;
import net.minecraftforge.common.config.Config;

public class SpecificThreatConfig
{
    @Config.Name("Threat Bypass")
    @Config.Comment(
            {
                    "Entities in this bypass the threat system",
                    "",
                    "This means they will not use the search AI, will appear with ???? as their target and threat level in the targeting HUD, and always appear as full alert in the on-point HUD",
                    "",
                    "For some entities, this option is necessary for them to work right, such as slimes.  For others, like the ender dragon and other players, it has no effect besides how they appear in the HUD"
            })
    @Config.RequiresMcRestart
    public String[] threatBypass = EntityThreatDefaults.threatBypassDefaults.toArray(new String[EntityThreatDefaults.threatBypassDefaults.size()]);

    @Config.Name("Passiveness")
    @Config.Comment(
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
