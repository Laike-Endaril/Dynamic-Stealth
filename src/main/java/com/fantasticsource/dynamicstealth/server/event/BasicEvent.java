package com.fantasticsource.dynamicstealth.server.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BasicEvent extends Event
{
    private EntityLivingBase livingBase;

    public BasicEvent(EntityLivingBase livingBase)
    {
        this.livingBase = livingBase;
    }

    public EntityLivingBase getLivingBase()
    {
        return livingBase;
    }


    public static class SearchEvent extends BasicEvent
    {
        public SearchEvent(EntityLivingBase livingBase)
        {
            super(livingBase);
        }
    }

    public static class TargetSeenEvent extends BasicEvent
    {
        public TargetSeenEvent(EntityLivingBase livingBase)
        {
            super(livingBase);
        }
    }


    public static class FleeEvent extends BasicEvent
    {
        public FleeEvent(EntityLivingBase livingBase, int fleeReason)
        {
            super(livingBase);
        }
    }

    /**
     * Cancelling this event only prevents any special effects; it does not make the entity continue to flee
     */
    @Cancelable
    public static class CalmDownEvent extends BasicEvent
    {
        public CalmDownEvent(EntityLivingBase livingBase, int fleeReason)
        {
            super(livingBase);
        }
    }

    /**
     * Cancelling this event only prevents any special effects; it does not make the entity continue to flee
     */
    @Cancelable
    public static class RallyEvent extends BasicEvent
    {
        public RallyEvent(EntityLivingBase livingBase, int fleeReason)
        {
            super(livingBase);
        }
    }

    @Cancelable
    public static class DesperationEvent extends BasicEvent
    {
        public DesperationEvent(EntityLivingBase livingBase)
        {
            super(livingBase);
        }
    }

    @Cancelable
    public static class CantReachEvent extends BasicEvent
    {
        public CantReachEvent(EntityLivingBase livingBase)
        {
            super(livingBase);
        }
    }
}
