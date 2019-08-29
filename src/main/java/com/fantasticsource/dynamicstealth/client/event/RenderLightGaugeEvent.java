package com.fantasticsource.dynamicstealth.client.event;

import com.fantasticsource.dynamicstealth.common.DSTools;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * The light gauge is a true HUD, triggering from RenderGameOverlayEvent.Pre, type RenderGameOverlayEvent.ElementType.HOTBAR (ie. just before the hotbar is rendered)
 * You can access the parent event (an instance of RenderGameOverlayEvent.Pre) from the targeting HUD event
 * Check static fields in LightGaugeConfig if you want to link into more related DS settings
 */
@Cancelable
public class RenderLightGaugeEvent extends Event
{
    private RenderGameOverlayEvent.Pre parentEvent;

    public RenderLightGaugeEvent(RenderGameOverlayEvent.Pre parentEvent)
    {
        this.parentEvent = parentEvent;
    }

    /**
     * @return The RenderGameOverlayEvent this event was fired from
     */
    public RenderGameOverlayEvent.Pre getParentEvent()
    {
        return parentEvent;
    }

    /**
     * @return The highest light level DS may use for stealth checks against the player (depends on what parts of the player a given entity has LOS to; if they can't see the "brightest" part of the player, they will check stealth using a lower light level)
     */
    public int getPlayerLightLevel()
    {
        return DSTools.maxLightLevelTotal(Minecraft.getMinecraft().player);
    }
}
