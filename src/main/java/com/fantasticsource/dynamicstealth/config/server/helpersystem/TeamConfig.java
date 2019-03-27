package com.fantasticsource.dynamicstealth.config.server.helpersystem;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class TeamConfig
{
    @Config.Name("Help Same Team")
    @Config.LangKey(DynamicStealth.MODID + ".config.helpTeam")
    @Config.Comment("Help other entities on our team")
    public boolean helpSame = true;

    @Config.Name("Don't Help Other Teams")
    @Config.LangKey(DynamicStealth.MODID + ".config.dontHelpOtherTeam")
    @Config.Comment("Make sure NOT to help entities on other teams")
    public boolean dontHelpOther = true;
}
