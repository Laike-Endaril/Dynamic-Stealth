package com.fantasticsource.dynamicstealth.client.event;

import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The targeting HUD is a true HUD, triggering from RenderGameOverlayEvent.Pre, type RenderGameOverlayEvent.ElementType.HOTBAR (ie. just before the hotbar is rendered)
 * The targeting HUD event fires for a targeted entity based on DS config settings, or not at all if not applicable
 * You can access the parent event (an instance of RenderGameOverlayEvent.Pre) from the targeting HUD event
 * RenderTargetingHUDEvent.Onscreen fires if the center of the currently targeted entity is on the screen, otherwise RenderTargetingHUDEvent.Offscreen fires instead
 * Both events are cancelable, and canceling one will prevent the normal DS render of that type
 * For RenderTargetingHUDEvent.Onscreen, x and y are the 2D position on-screen of the center of the entity, and can be used for a direct openGL transform to reach said point
 * For RenderTargetingHUDEvent.Offscreen, x and y are the 2D position on the EDGE of the screen which points toward the center of the offscreen entity, and can be used for a direct openGL transform to reach said point
 * Check static fields in TargetingFilterConfig and TargetingHUDStyleConfig if you want to link into more related DS settings
 */
@Cancelable
public class RenderTargetingHUDEvent extends Event
{
    private final double x, y;
    private final ClientData.OnPointData data;
    private final RenderGameOverlayEvent.Pre parentEvent;

    private RenderTargetingHUDEvent(RenderGameOverlayEvent.Pre parentEvent, double x, double y, ClientData.OnPointData data)
    {
        this.parentEvent = parentEvent;
        this.x = x;
        this.y = y;
        this.data = data;
    }


    /**
     * @return The RenderGameOverlayEvent this event was fired from
     */
    public RenderGameOverlayEvent.Pre getParentEvent()
    {
        return parentEvent;
    }

    /**
     * See javadoc for RenderTargetingHUDEvent
     */
    public double getX()
    {
        return x;
    }

    /**
     * See javadoc for RenderTargetingHUDEvent
     */
    public double getY()
    {
        return y;
    }


    public int getViewportWidth() throws IllegalAccessException
    {
        return Render.getStoredViewportWidth();
    }

    public int getViewportHeight() throws IllegalAccessException
    {
        return Render.getStoredViewportHeight();
    }


    /**
     * @return Data for the entity currently targeted by the DS targeting system (see fields and methods in ClientData.OnPointData)
     */
    @Nonnull
    public ClientData.OnPointData getData()
    {
        return data.clone();
    }

    /**
     * @return The entity currently targeted by the DS targeting system
     */
    @Nonnull
    public Entity getEntity()
    {
        return data.getEntity();
    }

    /**
     * @return The target *of* the entity currently targeted by the DS targeting system, or null if unavailable
     */
    @Nullable
    public Entity getEntityTarget()
    {
        return data.getTarget();
    }

    /**
     * @return The threat color of the entity currently targeted by the DS targeting system, as an integer.  This is a 0xRRGGBB format integer
     */
    public int getEntityThreatColor()
    {
        return data.color;
    }

    /**
     * @return The threat percentage of the entity currently targeted by the DS targeting system, as an integer, or 0 if unavailable.  This is a full-number percentage from 0 -> 100
     */
    public int getEntityThreat()
    {
        return data.percent;
    }


    /**
     * See javadoc for RenderTargetingHUDEvent
     */
    public static class Onscreen extends RenderTargetingHUDEvent
    {
        public Onscreen(RenderGameOverlayEvent.Pre parentEvent, double x, double y, ClientData.OnPointData data)
        {
            super(parentEvent, x, y, data);
        }
    }

    /**
     * See javadoc for RenderTargetingHUDEvent
     */
    public static class Offscreen extends RenderTargetingHUDEvent
    {
        private final double angleRad, dsX, dsY;

        public Offscreen(RenderGameOverlayEvent.Pre parentEvent, double x, double y, double dsX, double dsY, double angleRad, ClientData.OnPointData data)
        {
            super(parentEvent, x, y, data);
            this.angleRad = angleRad;
            this.dsX = dsX;
            this.dsY = dsY;
        }

        /**
         * @return The angle from the center of the screen to the offscreen entity, in radians.  0 means directly to the right.  Keep in mind that openGL uses degrees
         */
        public double getAngleRad()
        {
            return angleRad;
        }

        /**
         * @return The angle from the center of the screen to the offscreen entity, in degrees.  0 means directly to the right.  Directly usable for an openGL rotation (if eg. your base texture "points to the right" normally)
         */
        public double getAngleDeg()
        {
            return Tools.radtodeg(angleRad);
        }

        /**
         * @return The x position DS normally uses to draw an offscreen indicator (lies on a circle centered on the middle of the screen)
         */
        public double getDSX()
        {
            return dsX;
        }

        /**
         * @return The y position DS normally uses to draw an offscreen indicator (lies on a circle centered on the middle of the screen)
         */
        public double getDSY()
        {
            return dsY;
        }
    }
}
