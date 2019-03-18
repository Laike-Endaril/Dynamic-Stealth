package com.fantasticsource.dynamicstealth.config.server;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
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
    @Config.LangKey(DynamicStealth.MODID + ".config.ai")
    public AIConfig ai = new AIConfig();

    @Config.Name("Client HUD allowances")
    @Config.LangKey(DynamicStealth.MODID + ".config.HUDAllowances")
    public HUDAllowanceConfig hud = new HUDAllowanceConfig();

    @Config.Name("Senses")
    @Config.LangKey(DynamicStealth.MODID + ".config.senses")
    public SensesConfig senses = new SensesConfig();

    @Config.Name("Threat System")
    @Config.LangKey(DynamicStealth.MODID + ".config.threat")
    @Config.Comment(
            {
                    "The threat system decides when an entity switches from one attack target to another",
                    "",
                    "This is similar to threat systems found in some MMORPGs"
            })
    public ThreatConfig threat = new ThreatConfig();

    @Config.Name("Helper System")
    @Config.LangKey(DynamicStealth.MODID + ".config.helperSys")
    @Config.Comment("Which entities come to the aid of which other entities")
    public HelperSystemConfig helperSystemSettings = new HelperSystemConfig();

    @Config.Name("Interactions")
    @Config.LangKey(DynamicStealth.MODID + ".config.interact")
    @Config.Comment("Things that happen under special circumstances, eg. stealth attacks")
    public InteractionConfig interactions = new InteractionConfig();

    @Config.Name("Items")
    @Config.LangKey(DynamicStealth.MODID + ".config.items")
    public ItemConfig itemSettings = new ItemConfig();
}
