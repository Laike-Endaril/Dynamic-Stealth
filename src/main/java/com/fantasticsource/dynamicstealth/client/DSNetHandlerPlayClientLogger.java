package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.core.Logger;

import java.lang.reflect.Field;

@SideOnly(Side.CLIENT)
public class DSNetHandlerPlayClientLogger extends Logger
{
    protected DSNetHandlerPlayClientLogger(Logger oldLogger)
    {
        super(oldLogger.getContext(), oldLogger.getName(), oldLogger.getMessageFactory());
    }

    public static void init()
    {
        Field netHandlerPlayClientLOGGERField = ReflectionTool.getField(NetHandlerPlayClient.class, "field_147301_d", "LOGGER");
        ReflectionTool.set(netHandlerPlayClientLOGGERField, null, new DSNetHandlerPlayClientLogger((Logger) ReflectionTool.get(netHandlerPlayClientLOGGERField, null)));
    }

    @Override
    public void warn(String message)
    {
        if (!message.contains("Received p")) super.warn(message); //Short match for "Received passengers for unknown entity"
    }
}
