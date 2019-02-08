package com.fantasticsource.dynamicstealth.config;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraftforge.fml.client.DefaultGuiFactory;

public class DynamicStealthConfigFactory extends DefaultGuiFactory
{
    public DynamicStealthConfigFactory()
    {
        super(DynamicStealth.MODID, DynamicStealth.NAME);
    }
}
