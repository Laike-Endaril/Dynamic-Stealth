package com.fantasticsource.dynamicstealth.server.Senses;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntitySenses;

import java.util.ArrayList;
import java.util.List;

public class EntitySensesEdit extends EntitySenses
{
    List<Entity> seenEntities = new ArrayList<>();
    List<Entity> unseenEntities = new ArrayList<>();

    EntityLivingBase searcher;

    public EntitySensesEdit(EntityLiving searcher)
    {
        super(null);
        this.searcher = searcher;
    }

    @Override
    public void clearSensingCache()
    {
        seenEntities.clear();
        unseenEntities.clear();
    }

    @Override
    public boolean canSee(Entity target)
    {
        if (seenEntities.contains(target)) return true;
        if (unseenEntities.contains(target)) return false;

        boolean result = Sight.visualStealthLevel(searcher, target, true, true) <= 1;

        if (result) seenEntities.add(target);
        else unseenEntities.add(target);

        return result;
    }
}
