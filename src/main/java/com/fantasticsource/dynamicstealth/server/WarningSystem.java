package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.common.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.server.ai.AIStealthTargetingAndSearch;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
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
        if (helper != warner && helper instanceof EntityLiving)
        {
            EntityLiving livingHelper = (EntityLiving) helper;

            if (!Threat.bypassesThreat(livingHelper) && !Threat.isPassive(livingHelper))
            {
                Threat.ThreatData data = Threat.get(livingHelper);
                if (data.target == null && HelperSystem.helpPriority(livingHelper, warner, true, Math.pow(30d * livingHelper.getEntityAttribute(Attributes.HEARING).getAttributeValue() / 100, 2)) > 0)
                {
                    int distance = (int) warner.getDistance(helper);
                    int xz = 4 + distance / 2;
                    int y = 2 + distance / 4;
                    for (EntityAITasks.EntityAITaskEntry task : livingHelper.tasks.taskEntries)
                    {
                        if (task.action instanceof AIStealthTargetingAndSearch) ((AIStealthTargetingAndSearch) task.action).restart(MCTools.randomPos(warnPos, xz, y));
                    }

                    if (data.threatLevel < DynamicStealthConfig.serverSettings.threat.targetSpottedThreat) Threat.setThreat(livingHelper, DynamicStealthConfig.serverSettings.threat.targetSpottedThreat); //TODO add config for "warned" threat level
                }
            }
        }
    }
}
