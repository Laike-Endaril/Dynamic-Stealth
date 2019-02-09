package com.fantasticsource.dynamicstealth.server.event.attacks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;

public class AssassinationEvent extends LivingEvent
{
    public AssassinationEvent(EntityLivingBase entity, EntityLivingBase target)
    {
        super(entity);
    }
}
