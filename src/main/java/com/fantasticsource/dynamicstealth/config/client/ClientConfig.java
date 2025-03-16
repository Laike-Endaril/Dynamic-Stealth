package com.fantasticsource.dynamicstealth.config.client;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.config.client.hud.HUDConfig;
import net.minecraftforge.common.config.Config;

public class ClientConfig
{
    @Config.Name("HUD")
    @Config.LangKey(DynamicStealth.MODID + ".config.hud")
    @Config.Comment("What information you want to have displayed on the screen")
    public HUDConfig hudSettings = new HUDConfig();

    @Config.Name("Entity Fading")
    @Config.LangKey(DynamicStealth.MODID + ".config.entityFading")
    @Config.Comment("How entities fade in and out, if at all")
    public EntityFadeConfig entityFading = new EntityFadeConfig();

    @Config.Name("Tooltips")
    @Config.LangKey(DynamicStealth.MODID + ".config.tooltips")
    @Config.Comment("How item tooltips are altered")
    public TooltipConfig tooltips = new TooltipConfig();

    @Config.Name("010 Spider and Enderman Render Fix")
    @Config.LangKey(DynamicStealth.MODID + ".config.spiderAndEndermanRenderFix")
    @Config.RequiresMcRestart
    @Config.Comment(
            {
                    "If true, replaces the normal rendering of spider and endermen eyes to fix a couple vanilla rendering bugs",
                    "If you notice that spiders and endermen are rendering badly, but other things are fine, try turning this off",
                    "More likely to need this turned to false when running shaders"
            })
    public boolean spiderAndEndermanRenderFix = true;
}
