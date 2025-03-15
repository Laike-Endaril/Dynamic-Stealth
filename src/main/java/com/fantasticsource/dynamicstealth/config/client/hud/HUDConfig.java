package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class HUDConfig
{
    @Config.Name("010 Show Stealth Gauge")
    @Config.LangKey(DynamicStealth.MODID + ".config.showStealthGauge")
    @Config.Comment(
            {
                    "If true, the stealth gauge may be displayed when applicable",
                    "You may need to have something around to see or nearly see you",
                    "See other settings below for detailed config"
            })
    public boolean showStealthGauge = true;

    @Config.Name("020 Show On-Point HUD")
    @Config.LangKey(DynamicStealth.MODID + ".config.showOpHUD")
    @Config.Comment(
            {
                    "If true, there may be a small HUD above all applicable entities in the world",
                    "This may include a health bar and small threat gauge",
                    "See other settings below for detailed config"
            })
    public boolean showOpHUD = true;

    @Config.Name("030 Show Targeting HUD")
    @Config.LangKey(DynamicStealth.MODID + ".config.showTargetingHUD")
    @Config.Comment(
            {
                    "If true, the entity you're closest to looking at may have a targeting cursor on it, and detailed information in a panel",
                    "See other settings below for detailed config"
            })
    public boolean showTargetingHUD = true;

    @Config.Name("040 Show Light Gauge")
    @Config.LangKey(DynamicStealth.MODID + ".config.showLightGauge")
    @Config.Comment(
            {
                    "If true, you may see a light gauge, which shows how bright DS currently considers you to be",
                    "See other settings below for detailed config"
            })
    public boolean showLightGauge = true;

    @Config.Name("Stealth Gauge Style")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthGaugeStyle")
    public StealthGaugeStyleConfig stealthGaugeStyle = new StealthGaugeStyleConfig();

    @Config.Name("On-point HUD Filter")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDFilter")
    public OPHUDFilterConfig ophudFilter = new OPHUDFilterConfig();

    @Config.Name("On-point HUD Style")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDStyle")
    public OPHUDStyleConfig ophudStyle = new OPHUDStyleConfig();

    @Config.Name("Targeting Filter")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingFilter")
    public TargetingFilterConfig targetingFilter = new TargetingFilterConfig();

    @Config.Name("Targeting HUD Style")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingStyle")
    public TargetingHUDStyleConfig targetingStyle = new TargetingHUDStyleConfig();

    @Config.Name("Light Gauge")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightGauge")
    public LightGaugeConfig lightGauge = new LightGaugeConfig();
}
