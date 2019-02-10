package com.fantasticsource.dynamicstealth.server.senses;

import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntitySenses;

public class EntitySensesEdit extends EntitySenses
{
    EntityLivingBase searcher;

    public EntitySensesEdit(EntityLiving searcher)
    {
        super(null);
        this.searcher = searcher;
    }

    @Override
    public void clearSensingCache()
    {
    }

    @Override
    public boolean canSee(Entity target)
    {
        return Sight.canSee(searcher, target);
    }
}
