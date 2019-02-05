package com.fantasticsource.dynamicstealth.server.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class StealthAttackEvent extends LivingEvent
{
    private final DamageSource source;
    private float amount;

    public StealthAttackEvent(EntityLivingBase entity, DamageSource source, float amount)
    {
        super(entity);
        this.source = source;
        this.amount = amount;

        //TODO Add a weapon/held item filter with NBT support
        //TODO Add armor penetration as an option
        //TODO Reference issue #9
    }

    public DamageSource getSource()
    {
        return source;
    }

    public float getAmount()
    {
        return amount;
    }

    public void setAmount(float amount)
    {
        this.amount = amount;
    }
}
