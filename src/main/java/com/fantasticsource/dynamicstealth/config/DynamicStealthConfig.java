package com.fantasticsource.dynamicstealth.config;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.dynamicstealth.config.client.ClientConfig;
import com.fantasticsource.dynamicstealth.config.server.ServerConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = DynamicStealth.MODID)
public class DynamicStealthConfig
{
    @Config.Name("Client Settings")
    public static ClientConfig clientSettings = new ClientConfig();
    @Config.Name("Server Settings")
    public static ServerConfig serverSettings = new ServerConfig();
}
