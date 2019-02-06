package com.fantasticsource.dynamicstealth.config.server.helpersystem;

import net.minecraftforge.common.config.Config;

public class HelperSystemConfig
{
    @Config.Name("Ownership")
    public OwnershipConfig ownership = new OwnershipConfig();

    @Config.Name("Teams")
    public TeamConfig teams = new TeamConfig();

    @Config.Name("Custom NPCs Factions")
    @Config.Comment("These settings only matter if Custom NPCs is installed")
    public CNPCFactionConfig cnpcFactions = new CNPCFactionConfig();


    @Config.Name("Help Same Entity Type")
    @Config.Comment(
            {
                    "Whether to help entities of the same type",
                    "",
                    "Eg. if set to true, skeletons will help other skeletons (but not zombies)"
            })
    public boolean helpSameType = true;
}
