package com.fantasticsource.dynamicstealth.client.event;

import com.fantasticsource.dynamicstealth.common.ClientData;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The OPHUD is not a true HUD, but rather an in-world render, triggering from RenderLivingEvent.Post, on non-outline passes (so that eg. the glow effect won't screw it up)
 * The OPHUD event does not trigger for the entities you are riding or ridden by
 * You can access the parent event (an instance of RenderLivingEvent.Post) from the OPHUD event, which gives access to its RenderLivingBase instance, etc
 * For the "skull" location, use RenderOPHUDEvent.Transformed.  Otherwise, use RenderOPHUDEvent.Untransformed
 * RenderOPHUDEvent.Transformed is cancelable.  Canceling it prevents the render of the normal "skull", so you can eg. catch the event, cancel it, and do your own render instead
 * RenderOPHUDEvent.Untransformed is not cancelable and is fired before `RenderOPHUDEvent.Transformed` (whether you cancel the transformed event or not has no bearing)
 * If the "skull" was not going to render in the first place, due to DS configs, neither of these events fire.  This includes the DS client configs for OPHUD filtering (by angle, distance, etc)
 * Check static fields in OPHUDFilterConfig and OPHUDStyleConfig if you want to link into more related DS settings
 */
public class RenderOPHUDEvent extends Event
{
    private final ClientData.OnPointData data;
    private final RenderLivingEvent.Post parentEvent;

    private RenderOPHUDEvent(RenderLivingEvent.Post parentEvent, ClientData.OnPointData data)
    {
        this.parentEvent = parentEvent;
        this.data = data;
    }


    /**
     * @return The RenderLivingEvent this event was fired from
     */
    public RenderLivingEvent.Post getParentEvent()
    {
        return parentEvent;
    }


    /**
     * @return Data for the entity (see fields and methods in ClientData.OnPointData); may be null
     */
    @Nonnull
    public ClientData.OnPointData getData()
    {
        return data.clone();
    }

    /**
     * @return The entity being rendered
     */
    @Nonnull
    public Entity getEntity()
    {
        return data.getEntity();
    }

    /**
     * @return The target of the rendered entity, or null if unavailable
     */
    @Nullable
    public Entity getMainTargetTarget()
    {
        return data.getTarget();
    }

    /**
     * @return The threat color of the rendered entity.  This is a 0xRRGGBB format integer
     */
    public int getMainTargetColor()
    {
        return data.color;
    }

    /**
     * @return The threat percentage of the entity currently targeted by the DS targeting system, as an integer, or 0 if unavailable.  This is a full-number percentage from 0 -> 100
     */
    public int getMainTargetThreat()
    {
        return data.percent;
    }


    /**
     * See javadoc for RenderOPHUDEvent
     */
    @Cancelable
    public static class Transformed extends RenderOPHUDEvent
    {
        public Transformed(RenderLivingEvent.Post parentEvent, ClientData.OnPointData data)
        {
            super(parentEvent, data);
        }
    }

    /**
     * See javadoc for RenderOPHUDEvent
     */
    public static class Untransformed extends RenderOPHUDEvent
    {
        public Untransformed(RenderLivingEvent.Post parentEvent, ClientData.OnPointData data)
        {
            super(parentEvent, data);
        }
    }
}
