package com.fantasticsource.dynamicstealth.server.event;

import com.fantasticsource.dynamicstealth.server.threat.Threat.THREAT_TYPE;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ApplyThreatEvent extends Event
{
    public EntityLivingBase searcher, target;
    public double threatPercentage;
    public THREAT_TYPE type;
    public boolean searcherSeesTarget;
    public DamageSource damageSource;

    public ApplyThreatEvent(EntityLivingBase searcher, EntityLivingBase target, double threatPercentage, THREAT_TYPE type, boolean searcherSeesTarget)
    {
        this(searcher, target, threatPercentage, type, searcherSeesTarget, null);
    }

    public ApplyThreatEvent(EntityLivingBase searcher, EntityLivingBase target, double threatPercentage, THREAT_TYPE type, boolean searcherSeesTarget, DamageSource damageSource)
    {
        this.searcher = searcher;
        this.target = target;
        this.threatPercentage = threatPercentage;
        this.type = type;
        this.searcherSeesTarget = searcherSeesTarget;
        this.damageSource = damageSource;
    }
}
