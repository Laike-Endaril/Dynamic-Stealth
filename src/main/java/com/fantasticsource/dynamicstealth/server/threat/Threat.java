package com.fantasticsource.dynamicstealth.server.threat;

import com.fantasticsource.dynamicstealth.server.Attributes;
import com.fantasticsource.dynamicstealth.server.CombatTracker;
import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.server.threat.Threat.THREAT_TYPE.GEN_ATTACKED_DURING_FLEE;
import static com.fantasticsource.mctools.ServerTickTimer.currentTick;

public class Threat
{
    private static final int ITERATION_FREQUENCY = 72000;
    //Searcher, target, threat level
    private static Map<EntityLivingBase, ThreatData> threatMap = new LinkedHashMap<>(200);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void update(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && currentTick() % ITERATION_FREQUENCY == 0) removeAllUnused();
    }

    private static void removeAllUnused()
    {
        threatMap.entrySet().removeIf(Threat::checkRemoveSearcher);
    }

    private static boolean checkRemoveSearcher(Map.Entry<EntityLivingBase, ThreatData> entry)
    {
        EntityLivingBase searcher = entry.getKey();
        return !searcher.world.loadedEntityList.contains(searcher);
    }

    public static void remove(EntityLivingBase searcher)
    {
        CombatTracker.setNoTargetTime(searcher);
        CombatTracker.setIdleTime(searcher);
        threatMap.remove(searcher);
    }

    @Nonnull
    public static ThreatData get(EntityLivingBase searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData.copy();
        return new ThreatData(searcher, null, 0);
    }

    public static EntityLivingBase getTarget(EntityLivingBase searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData.target;
        return null;
    }

    public static float getThreat(EntityLivingBase searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData.threatPercentage;
        return 0;
    }

    public static void set(EntityLivingBase searcher, EntityLivingBase target, float threatPercentage)
    {
        if (EntityThreatData.bypassesThreat(searcher)) return;

        if (threatPercentage <= 0) remove(searcher);
        else
        {
            if (threatPercentage > 100) threatPercentage = 100;

            ThreatData threatData = threatMap.get(searcher);
            if (threatData != null)
            {
                if (target == null) CombatTracker.setNoTargetTime(searcher);
                else if (threatData.target == null) CombatTracker.setNoTargetTime(searcher, currentTick() - 1);

                if (threatData.threatPercentage <= 0) CombatTracker.setIdleTime(searcher, currentTick() - 1);
                threatData.target = target;
                threatData.threatPercentage = threatPercentage;
            }
            else
            {
                if (target == null) CombatTracker.setNoTargetTime(searcher);
                else CombatTracker.setNoTargetTime(searcher, currentTick() - 1);

                CombatTracker.setIdleTime(searcher, currentTick() - 1);
                threatMap.put(searcher, new ThreatData(searcher, target, threatPercentage));
            }
        }
    }

    public static void clearTarget(EntityLivingBase searcher)
    {
        if (EntityThreatData.bypassesThreat(searcher)) return;

        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) threatData.target = null;
        CombatTracker.setNoTargetTime(searcher);
    }

    public static void setThreat(EntityLivingBase searcher, float threatPercentage)
    {
        if (EntityThreatData.bypassesThreat(searcher)) return;

        if (threatPercentage <= 0) remove(searcher);
        else
        {
            if (threatPercentage > 100) threatPercentage = 100;

            ThreatData threatData = threatMap.get(searcher);
            if (threatData != null)
            {
                if (threatData.threatPercentage <= 0) CombatTracker.setIdleTime(searcher, currentTick() - 1);
                threatData.threatPercentage = threatPercentage;
            }
            else
            {
                CombatTracker.setIdleTime(searcher, currentTick() - 1);
                threatMap.put(searcher, new ThreatData(searcher, null, threatPercentage));
            }
        }
    }

    public static void apply(EntityLivingBase searcher, EntityLivingBase target, double threatPercentage, THREAT_TYPE type, boolean searcherSeesTarget)
    {
        if (EntityThreatData.bypassesThreat(searcher)) return;

        ThreatData data = Threat.get(searcher);
        EntityLivingBase oldTarget = data.target;
        float oldPercentage = data.threatPercentage;

        switch (type)
        {
            case GEN_TARGET_SPOTTED:
                if (searcherSeesTarget && (oldTarget == null || oldTarget == target))
                {
                    threatPercentage *= MCTools.getAttribute(target, Attributes.THREATGEN_SPOTTED, 1);
                    Threat.set(searcher, target, (float) threatPercentage);
                }
                break;


            case GEN_ATTACKED:
                if (searcher instanceof EntityLiving)
                {
                    AIDynamicStealth ai = AIDynamicStealth.getStealthAI((EntityLiving) searcher);
                    if (ai != null && ai.isFleeing())
                    {
                        //Redirect to fleeing type
                        apply(searcher, target, threatPercentage, GEN_ATTACKED_DURING_FLEE, searcherSeesTarget);
                        return;
                    }
                }

                threatPercentage *= MCTools.getAttribute(target, Attributes.THREATGEN_ATTACK, 1);
                threatPercentage /= searcher.getMaxHealth();

                if (oldPercentage <= 0)
                {
                    //Not in combat
                    threatPercentage *= serverSettings.threat.attackedInitialMultiplier;
                    Threat.set(searcher, searcherSeesTarget ? target : null, (float) threatPercentage);
                }
                else if (searcherSeesTarget && target != oldTarget)
                {
                    //In combat (not fleeing), and hit by an entity besides our threat target, which we see
                    //Subtract from existing threat level; if it would be <= 0, then instead set new target and use initial attack multiplier as opposed to attacked by other multiplier
                    double threatTest = threatPercentage * serverSettings.threat.attackedByOtherMultiplier;
                    if (threatTest < oldPercentage) Threat.setThreat(searcher, (float) (oldPercentage - threatTest));
                    else Threat.set(searcher, target, (float) (threatPercentage * serverSettings.threat.attackedInitialMultiplier));
                }
                else
                {
                    //In combat (not fleeing), and hit by threat target or what is presumed to be threat target (if unseen)
                    threatPercentage *= serverSettings.threat.attackedBySameMultiplier;
                    Threat.setThreat(searcher, (float) threatPercentage + oldPercentage);
                }
                break;


            case GEN_ATTACKED_DURING_FLEE:
                threatPercentage *= MCTools.getAttribute(target, Attributes.THREATGEN_ATTACK, 1);
                threatPercentage *= serverSettings.threat.attackedBySameMultiplier;
                threatPercentage /= searcher.getMaxHealth();

                //Always cumulative when fleeing
                if (searcherSeesTarget) Threat.set(searcher, target, (float) threatPercentage + oldPercentage);
                else Threat.setThreat(searcher, (float) threatPercentage + oldPercentage);
                break;


            case GEN_DAMAGE_DEALT:
                if (target == oldTarget && searcherSeesTarget)
                {
                    threatPercentage *= MCTools.getAttribute(target, Attributes.THREATGEN_DAMAGE_TAKEN, 1);
                    threatPercentage *= serverSettings.threat.damageDealtMultiplier;
                    threatPercentage /= target.getMaxHealth();

                    Threat.setThreat(searcher, (float) threatPercentage + oldPercentage);
                }
                break;


            case GEN_WARNED:
                if (searcherSeesTarget) threatPercentage *= MCTools.getAttribute(target, Attributes.THREATGEN_WARNED_AGAINST, 1);
                threatPercentage = Tools.max(threatPercentage, oldPercentage);

                if (searcherSeesTarget && target != null && oldTarget == null) Threat.set(searcher, target, (float) threatPercentage);
                else Threat.setThreat(searcher, (float) threatPercentage);
                break;


            case GEN_ALLY_KILLED:
                if (searcherSeesTarget) threatPercentage *= MCTools.getAttribute(target, Attributes.THREATGEN_KILL, 1);

                if (!searcherSeesTarget || target == oldTarget) Threat.setThreat(searcher, (float) threatPercentage + oldPercentage);
                else if (oldTarget == null) Threat.set(searcher, target, (float) threatPercentage + oldPercentage);
                else
                {
                    //We have a current, valid threat target, it is not the killer, and the killer is seen

                    //If the new amount is >= the current amount, switch targets and use the greater amount
                    //Otherwise, enrage at the current target (add the new amount to the current amount)
                    if (threatPercentage >= oldPercentage) Threat.set(searcher, target, (float) threatPercentage);
                    else Threat.setThreat(searcher, (float) threatPercentage + oldPercentage);
                }
                break;


            case GEN_TARGET_VISIBLE:
                if (searcherSeesTarget && oldTarget != null)
                {
                    threatPercentage *= MCTools.getAttribute(oldTarget, Attributes.THREATGEN_VISIBLE, 1);
                    Threat.setThreat(searcher, (float) threatPercentage + oldPercentage);
                }
                break;


            case DEG_TARGET_NOT_VISIBLE:
                if (!searcherSeesTarget)
                {
                    threatPercentage *= MCTools.getAttribute(target, Attributes.THREATDEG_NOT_VISIBLE, 1);
                    Threat.setThreat(searcher, oldPercentage - (float) threatPercentage);
                }
                break;


            case DEG_FLEE:
                threatPercentage *= MCTools.getAttribute(oldTarget, Attributes.THREATDEG_FLEE_FROM, 1);
                Threat.setThreat(searcher, oldPercentage - (float) threatPercentage);
                break;


            case DEG_OWNED_CANT_REACH:
                Threat.setThreat(searcher, oldPercentage - (float) threatPercentage);
                break;
        }
    }


    public enum THREAT_TYPE
    {
        GEN_TARGET_SPOTTED,
        GEN_ATTACKED,
        GEN_ATTACKED_DURING_FLEE,
        GEN_DAMAGE_DEALT,
        GEN_WARNED,
        GEN_ALLY_KILLED,
        GEN_TARGET_VISIBLE,

        DEG_TARGET_NOT_VISIBLE,
        DEG_FLEE,
        DEG_OWNED_CANT_REACH
    }

    public static class ThreatData
    {
        public EntityLivingBase searcher;
        public EntityLivingBase target;
        public float threatPercentage;
        public String searcherName;

        private ThreatData(EntityLivingBase searcher, EntityLivingBase target, float threatPercentage)
        {
            this.searcher = searcher;
            this.target = target;
            this.threatPercentage = threatPercentage;

            searcherName = searcher.getName();
        }

        public ThreatData copy()
        {
            return new ThreatData(searcher, target, threatPercentage);
        }

        public boolean equals(ThreatData threatData)
        {
            return threatData != null && threatData.searcher == searcher && threatData.target == target && threatData.threatPercentage == threatPercentage && threatData.searcherName.equals(searcherName);
        }

        public String toString()
        {
            return searcherName + ", " + target.getName() + ", " + threatPercentage;
        }
    }
}
