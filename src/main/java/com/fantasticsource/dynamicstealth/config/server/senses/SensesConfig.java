package com.fantasticsource.dynamicstealth.config.server.senses;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.config.server.senses.sight.SightConfig;
import net.minecraftforge.common.config.Config;

public class SensesConfig
{
    @Config.Name("Use player senses")
    @Config.RequiresMcRestart
    @Config.LangKey(DynamicStealth.MODID + ".config.playerSenses")
    @Config.Comment(
            {
                    "If enabled, stealth mechanics work on players",
                    "",
                    "Basically if this is turned on and that skeleton is holding still in the dark, you might not be able to see him until you get close"
            })
    public boolean usePlayerSenses = true;

    @Config.Name("Touch")
    @Config.LangKey(DynamicStealth.MODID + ".config.touch")
    public TouchConfig touch = new TouchConfig();

    @Config.Name("Sight")
    @Config.LangKey(DynamicStealth.MODID + ".config.sight")
    public SightConfig sight = new SightConfig();

    @Config.Name("Hearing")
    @Config.LangKey(DynamicStealth.MODID + ".config.hearing")
    public HearingConfig hearing = new HearingConfig();
}
