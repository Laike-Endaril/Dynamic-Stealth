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
}
