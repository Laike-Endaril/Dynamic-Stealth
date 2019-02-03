package com.fantasticsource.dynamicstealth.event;

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

    @Cancelable
    public static class SearchEvent extends BasicEvent
    {
        public SearchEvent(EntityLivingBase livingBase)
        {
            super(livingBase);
        }
    }

    @Cancelable
    public static class TargetSeenEvent extends BasicEvent
    {
        public TargetSeenEvent(EntityLivingBase livingBase)
        {
            super(livingBase);
        }
    }

    @Cancelable
    public static class FleeEvent extends BasicEvent
    {
        public FleeEvent(EntityLivingBase livingBase)
        {
            super(livingBase);
        }
    }

    @Cancelable
    public static class RallyEvent extends BasicEvent
    {
        public RallyEvent(EntityLivingBase livingBase)
        {
            super(livingBase);
        }
    }

    public static class DesperationEvent extends BasicEvent
    {
        public DesperationEvent(EntityLivingBase livingBase)
        {
            super(livingBase);
        }
    }

}
