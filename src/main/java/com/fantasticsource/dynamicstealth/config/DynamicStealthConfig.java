package com.fantasticsource.dynamicstealth.config;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.dynamicstealth.config.client.ClientConfig;
import com.fantasticsource.dynamicstealth.config.server.ServerConfig;
import net.minecraftforge.common.config.Config;

import static com.fantasticsource.dynamicstealth.config.ConfigHandler.CONFIG_NAME;

@Config(modid = DynamicStealth.MODID, name = CONFIG_NAME)
public class DynamicStealthConfig
{
    @Config.Name("Client Settings")
    @Config.LangKey(DynamicStealth.MODID + ".config.clientSettings")
    public static ClientConfig clientSettings = new ClientConfig();

    @Config.Name("Server Settings")
    @Config.LangKey(DynamicStealth.MODID + ".config.serverSettings")
    public static ServerConfig serverSettings = new ServerConfig();
}
