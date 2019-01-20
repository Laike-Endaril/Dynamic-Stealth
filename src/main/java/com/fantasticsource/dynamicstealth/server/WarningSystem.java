package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.common.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.server.ai.AIStealthTargetingAndSearch;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class WarningSystem
{
    private static LinkedHashMap<EntityLivingBase, Pair<World, BlockPos>> warners = new LinkedHashMap<>();


    public static void warn(EntityLivingBase livingBase, BlockPos blockPos)
    {
        warners.put(livingBase, new Pair<>(livingBase.world, blockPos));
    }

    @SubscribeEvent
    public static void update(TickEvent.ServerTickEvent event)
    {
        warners.entrySet().removeIf(WarningSystem::checkRemove);
    }

    private static boolean checkRemove(Map.Entry<EntityLivingBase, Pair<World, BlockPos>> entry)
    {
        EntityLivingBase warner = entry.getKey();
        Pair<World, BlockPos> data = entry.getValue();
        World world = data.getKey();
        if (warner.isEntityAlive() && warner.world == world)
        {
            for (Entity entity : world.loadedEntityList)
            {
                tryWarn(warner, entity, data.getValue());
            }
        }

        return true;
    }

    private static void tryWarn(EntityLivingBase warner, Entity helper, BlockPos warnPos)
    {
        if (helper instanceof EntityLiving)
        {
            EntityLiving livingHelper = (EntityLiving) helper;

            if (!Threat.bypassesThreat(livingHelper) && !Threat.isPassive(livingHelper))
            {
                Threat.ThreatData data = Threat.get(livingHelper);
                if (data.target == null && filter(warner, livingHelper))
                {
                    for (EntityAITasks.EntityAITaskEntry task : livingHelper.tasks.taskEntries)
                    {
                        if (task.action instanceof AIStealthTargetingAndSearch)
                        {
                            AIStealthTargetingAndSearch ai = (AIStealthTargetingAndSearch) task.action;
                            ai.restart(warnPos);
                        }
                    }

                    if (data.threatLevel < DynamicStealthConfig.serverSettings.threat.targetSpottedThreat) Threat.setThreat(livingHelper, DynamicStealthConfig.serverSettings.threat.targetSpottedThreat); //TODO add config for "warned" threat level
                }
            }
        }
    }

    private static boolean filter(EntityLivingBase warner, EntityLiving helper)
    {
        //Distance
        if (Math.sqrt(warner.getDistanceSq(helper)) <= 30d * helper.getEntityAttribute(Attributes.HEARING).getAttributeValue() / 100)
        {
            //Ownership //TODO add t/f config for this
            if (helper instanceof IEntityOwnable)
            {
                Entity owner = ((IEntityOwnable) helper).getOwner();
                if (owner != null)
                {
                    //Owner is warning us
                    if (owner == warner) return true;

                    //Something with same owner is warning us
                    if (warner instanceof IEntityOwnable && ((IEntityOwnable) warner).getOwner() == owner) return true;

                    //Something which is not our owner and does not have the same owner; we don't care about them
                    return false;
                }
            }

            //Teams //TODO add t/f config option for this
            Team helperTeam = helper.getTeam();
            if (helperTeam != null)
            {
                if (helperTeam.isSameTeam(warner.getTeam())) return true; //Force true if on same team
                if (warner.getTeam() != null) return false; // force false if on different teams
            }

            //By type
            if (!Compat.customnpcs || !helper.getClass().getName().equals("noppes.npcs.entity.EntityCustomNpc"))
            {
                //TODO add config for types to accept warnings from
                if (helper.getClass() == warner.getClass()) return true;
            }
        }

        return false;
    }
}
