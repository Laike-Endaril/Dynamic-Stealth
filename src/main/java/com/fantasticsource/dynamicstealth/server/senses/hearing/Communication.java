package com.fantasticsource.dynamicstealth.server.senses.hearing;

import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.common.DynamicStealth.TRIG_TABLE;
import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.server.HelperSystem.isAlly;
import static com.fantasticsource.dynamicstealth.server.HelperSystem.rep;
import static com.fantasticsource.dynamicstealth.server.senses.hearing.Hearing.canHear;

public class Communication
{
    private static ArrayList<WarnData> warners = new ArrayList<>();


    //Notify others of target death
    public static void notifyDead(EntityLivingBase notifier, EntityLivingBase dead)
    {
        if (notifier instanceof EntityLiving && notifier.world == dead.world && notifier.isEntityAlive())
        {
            EntityLiving livingNotifier = (EntityLiving) notifier;
            World world = notifier.world;

            for (Entity entity : world.loadedEntityList.toArray(new Entity[world.loadedEntityList.size()]))
            {
                if (entity instanceof EntityLivingBase)
                {
                    EntityLivingBase listener = (EntityLivingBase) entity;

                    if (Threat.getTarget(listener) == dead && isAlly(listener, livingNotifier) && canHear(listener, livingNotifier, serverSettings.senses.hearing.notificationRange))
                    {
                        if (MCTools.isOwned(listener)) Threat.set(listener, null, 0);
                        else Threat.clearTarget(listener);
                        Communication.notifyDead(listener, dead);
                    }
                }
            }
        }
    }


    //Warn others of threat
    public static void warn(EntityLivingBase warner, EntityLivingBase danger, BlockPos dangerPos, boolean sawDanger)
    {
        warners.add(new WarnData(warner, danger, dangerPos, sawDanger));
    }

    @SubscribeEvent
    public static void update(TickEvent.ServerTickEvent event)
    {
        warners.removeIf(Communication::processAndRemove);
    }

    private static boolean processAndRemove(WarnData warnData)
    {
        EntityLivingBase warner = warnData.warner;
        EntityLivingBase danger = warnData.danger;
        World world = warner.world;
        if (warner.isEntityAlive() && (danger == null || danger.world == world))
        {
            for (Entity helper : world.loadedEntityList.toArray(new Entity[world.loadedEntityList.size()]))
            {
                tryWarn(warner, helper, danger, warnData.dangerPos, warnData.sawDanger);
            }
        }

        return true;
    }

    private static void tryWarn(EntityLivingBase warner, Entity helper, EntityLivingBase danger, BlockPos dangerPos, boolean sawDanger)
    {
        if (helper != warner && helper != danger && helper instanceof EntityLiving && helper.isEntityAlive())
        {
            EntityLiving livingHelper = (EntityLiving) helper;

            if (!EntityThreatData.bypassesThreat(livingHelper) && rep(livingHelper, warner) > rep(livingHelper, danger))
            {
                Threat.ThreatData helperThreat = Threat.get(livingHelper);
                if ((helperThreat.target == null || helperThreat.target == danger) && canHear(livingHelper, warner, serverSettings.senses.hearing.warningRange))
                {
                    boolean canSee = sawDanger && Sight.canSee(livingHelper, danger, false, true, MCTools.getYaw(livingHelper, danger, TRIG_TABLE), MCTools.getPitch(livingHelper, danger, TRIG_TABLE));
                    if (canSee) Threat.set(livingHelper, danger, Tools.max(serverSettings.threat.warnedThreat, helperThreat.threatLevel));
                    else Threat.setThreat(livingHelper, Tools.max(serverSettings.threat.warnedThreat, helperThreat.threatLevel));

                    AIDynamicStealth helperAI = AIDynamicStealth.getStealthAI(livingHelper);
                    if (helperAI != null)
                    {
                        helperAI.fleeIfYouShould(0);

                        if (canSee) helperAI.lastKnownPosition = danger.getPosition();
                        else
                        {
                            int distance = (int) helper.getDistance(dangerPos.getX(), dangerPos.getY(), dangerPos.getZ());
                            helperAI.lastKnownPosition = MCTools.randomPos(dangerPos, Tools.min(3 + (distance >> 1), 7), Tools.min(1 + (distance >> 2), 4));
                        }
                    }
                }
            }
        }
    }

    public static class WarnData
    {
        EntityLivingBase warner;
        EntityLivingBase danger;
        BlockPos dangerPos;
        boolean sawDanger;

        public WarnData(EntityLivingBase warner, EntityLivingBase danger, BlockPos dangerPos, boolean sawDanger)
        {
            this.warner = warner;
            this.danger = danger;
            this.dangerPos = dangerPos;
            this.sawDanger = sawDanger;
        }
    }
}