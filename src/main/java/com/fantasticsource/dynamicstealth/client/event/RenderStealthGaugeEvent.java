package com.fantasticsource.dynamicstealth.client.event;

import com.fantasticsource.dynamicstealth.common.ClientData;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * The stealth gauge is a true HUD, triggering from RenderGameOverlayEvent.Pre, type RenderGameOverlayEvent.ElementType.HOTBAR (ie. just before the hotbar is rendered)
 * You can access the parent event (an instance of RenderGameOverlayEvent.Pre) from the targeting HUD event
 * Check static fields in MainHUDStyleConfig if you want to link into more related DS settings
 */
@Cancelable
public class RenderStealthGaugeEvent extends Event
{
    private RenderGameOverlayEvent.Pre parentEvent;

    public RenderStealthGaugeEvent(RenderGameOverlayEvent.Pre parentEvent)
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
     * @return The *global* stealth level of the player (ie. best attempt at minimum stealth level among all hostiles), or Float.NaN if unavailable (eg. if this data is disabled on the server); the returned value already accounts for partialtick and can be used directly in is suitable for using directly in a render method.  Normal range is 0 -> 100
     */
    public float getPlayerStealthLevel()
    {
        if (ClientData.stealthLevel == Byte.MIN_VALUE) return Float.NaN;
        return Minecraft.getMinecraft().getRenderPartialTicks() * (ClientData.stealthLevel - ClientData.prevStealthLevel) + ClientData.prevStealthLevel;
    }
}
