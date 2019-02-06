package com.fantasticsource.dynamicstealth.config.server;

import com.fantasticsource.dynamicstealth.config.server.ai.AIConfig;
import com.fantasticsource.dynamicstealth.config.server.helpersystem.HelperSystemConfig;
import com.fantasticsource.dynamicstealth.config.server.hud.HUDAllowanceConfig;
import com.fantasticsource.dynamicstealth.config.server.interactions.InteractionConfig;
import com.fantasticsource.dynamicstealth.config.server.items.ItemConfig;
import com.fantasticsource.dynamicstealth.config.server.senses.SensesConfig;
import com.fantasticsource.dynamicstealth.config.server.threat.ThreatConfig;
import net.minecraftforge.common.config.Config;

public class ServerConfig
{
    @Config.Name("AI")
    public AIConfig ai = new AIConfig();

    @Config.Name("Client HUD allowances")
    public HUDAllowanceConfig hud = new HUDAllowanceConfig();

    @Config.Name("Senses")
    public SensesConfig senses = new SensesConfig();

    @Config.Name("Threat System")
    @Config.Comment(
            {
                    "The threat system decides when an entity switches from one attack target to another",
                    "",
                    "This is similar to threat systems found in some MMORPGs"
            })
    public ThreatConfig threat = new ThreatConfig();

    @Config.Name("Helper System")
    @Config.Comment("Which entities come to the aid of which other entities")
    public HelperSystemConfig helperSystemSettings = new HelperSystemConfig();

    @Config.Name("Interactions")
    @Config.Comment("Things that happen under special circumstances, eg. stealth attacks")
    public InteractionConfig interactions = new InteractionConfig();

    @Config.Name("Items")
    public ItemConfig itemSettings = new ItemConfig();
}
