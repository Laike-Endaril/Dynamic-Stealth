package com.fantasticsource.dynamicstealth.config.server.helpersystem;

import net.minecraftforge.common.config.Config;

public class TeamConfig
{
    @Config.Name("Help Same Team")
    @Config.Comment("Help other entities on our team")
    public boolean helpSame = true;

    @Config.Name("Don't Help Other Teams")
    @Config.Comment("Make sure NOT to help entities on other teams")
    public boolean dontHelpOther = true;
}
