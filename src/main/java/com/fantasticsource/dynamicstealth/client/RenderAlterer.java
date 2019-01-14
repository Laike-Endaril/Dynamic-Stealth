package com.fantasticsource.dynamicstealth.client;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderAlterer
{
    @SubscribeEvent
    public static void preRender(RenderLivingEvent.Pre event)
    {
        event.getEntity().setInvisible(false);
    }
}
