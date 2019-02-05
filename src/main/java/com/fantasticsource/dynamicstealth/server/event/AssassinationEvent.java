package com.fantasticsource.dynamicstealth.server.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;

public class AssassinationEvent extends LivingEvent
{
    public AssassinationEvent(EntityLivingBase entity, EntityLivingBase target)
    {
        super(entity);

        //TODO Add a weapon/held item filter with NBT support
        //TODO Reference issue #9
    }
}
