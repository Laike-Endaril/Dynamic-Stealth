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

    @Config.Name("Test")
    @Config.LangKey(DynamicStealth.MODID + ".config.testcat")
    public static TestConfig testConfig = new TestConfig();

    public static class TestConfig
    {
        @Config.Name("Test")
        @Config.LangKey(DynamicStealth.MODID + ".config.testbool")
        public boolean test = false;

        @Config.Name("Test 2")
        @Config.LangKey(DynamicStealth.MODID + ".config.testcat2")
        public TestConfig2 testConfig2 = new TestConfig2();

        public static class TestConfig2
        {
            @Config.Name("Test 2")
            @Config.LangKey(DynamicStealth.MODID + ".config.testbool2")
            public boolean test = true;
        }
    }
}
