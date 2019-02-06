package com.fantasticsource.dynamicstealth.config.server.senses;

import com.fantasticsource.dynamicstealth.config.server.senses.sight.SightConfig;
import net.minecraftforge.common.config.Config;

public class SensesConfig
{
    @Config.Name("Use player senses")
    @Config.Comment(
            {
                    "If enabled, stealth mechanics work on players",
                    "",
                    "Basically if this is turned on and that skeleton is holding still in the dark, you might not be able to see him until you get close"
            })
    @Config.RequiresMcRestart
    public boolean usePlayerSenses = true;

    @Config.Name("Touch")
    public TouchConfig touch = new TouchConfig();

    @Config.Name("Sight")
    public SightConfig sight = new SightConfig();

    @Config.Name("Hearing")
    public HearingConfig hearing = new HearingConfig();
}
