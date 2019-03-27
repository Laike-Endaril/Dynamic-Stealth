package com.fantasticsource.dynamicstealth.config.server.helpersystem;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class HelperSystemConfig
{
    @Config.Name("Ownership")
    @Config.LangKey(DynamicStealth.MODID + ".config.helperOwnership")
    public OwnershipConfig ownership = new OwnershipConfig();

    @Config.Name("Teams")
    @Config.LangKey(DynamicStealth.MODID + ".config.helperTeams")
    public TeamConfig teams = new TeamConfig();

    @Config.Name("Custom NPCs Factions")
    @Config.LangKey(DynamicStealth.MODID + ".config.helperCNPCFactions")
    @Config.Comment("These settings only matter if Custom NPCs is installed")
    public CNPCFactionConfig cnpcFactions = new CNPCFactionConfig();


    @Config.Name("Help Same Entity Type")
    @Config.LangKey(DynamicStealth.MODID + ".config.helperType")
    @Config.Comment(
            {
                    "Whether to help entities of the same type",
                    "",
                    "Eg. if set to true, skeletons will help other skeletons (but not zombies)"
            })
    public boolean helpSameType = true;
}
