package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.common.Network;
import com.fantasticsource.dynamicstealth.server.configdata.EntityThreatData;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.common.DynamicStealth.TRIG_TABLE;
import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;

public class Threat
{
    private static final int ITERATION_FREQUENCY = 72000; //Server ticks
    private static int timer = ITERATION_FREQUENCY;
    //Searcher, target, threat level
    private static Map<EntityLivingBase, ThreatData> threatMap = new LinkedHashMap<>(200);

    @SubscribeEvent
    public static void update(TickEvent.ServerTickEvent event)
    {
        if (--timer == 0)
        {
            timer = ITERATION_FREQUENCY;
            removeAllUnused();
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == Side.SERVER)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            Network.sendThreatData(player, seenEntities(player));
        }
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

    public static void removeTargetFromAll(EntityLivingBase target)
    {
        for (Map.Entry<EntityLivingBase, ThreatData> entry : threatMap.entrySet())
        {
            ThreatData threatData = entry.getValue();
            if (threatData.target == target) threatData.target = null;
        }
    }


    public static void remove(EntityLivingBase searcher)
    {
        threatMap.remove(searcher);
    }


    @Nonnull
    public static ThreatData get(EntityLivingBase searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData;
        return new ThreatData(searcher, null, 0);
    }

    public static EntityLivingBase getTarget(EntityLivingBase searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData.searcher;
        return null;
    }

    public static Integer getThreat(EntityLivingBase searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData.threatLevel;
        return 0;
    }


    public static void set(EntityLivingBase searcher, EntityLivingBase target, int threat)
    {
        if (bypassesThreat(searcher)) return;

        if (threat <= 0) remove(searcher);
        else
        {
            if (threat > serverSettings.threat.maxThreat) threat = serverSettings.threat.maxThreat;

            ThreatData threatData = threatMap.get(searcher);
            if (threatData != null)
            {
                threatData.target = target;
                threatData.threatLevel = threat;
            }
            else threatMap.put(searcher, new ThreatData(searcher, target, threat));
        }
    }

    public static void setTarget(EntityLivingBase searcher, EntityLivingBase target)
    {
        if (bypassesThreat(searcher)) return;

        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) threatData.target = target;
        else threatMap.put(searcher, new ThreatData(searcher, target, 0));
    }

    public static void setThreat(EntityLivingBase searcher, int threat)
    {
        if (bypassesThreat(searcher)) return;

        if (threat <= 0) remove(searcher);
        else
        {
            if (threat > serverSettings.threat.maxThreat) threat = serverSettings.threat.maxThreat;

            ThreatData threatData = threatMap.get(searcher);
            if (threatData != null) threatData.threatLevel = threat;
            else threatMap.put(searcher, new ThreatData(searcher, null, threat));
        }
    }


    public static ExplicitPriorityQueue<EntityLivingBase> seenEntities(EntityPlayerMP player)
    {
        ExplicitPriorityQueue<EntityLivingBase> queue = new ExplicitPriorityQueue<>(10);
        double stealthLevel;
        Entity[] loadedEntities = player.world.loadedEntityList.toArray(new Entity[player.world.loadedEntityList.size()]);

        if (serverSettings.senses.usePlayerSenses)
        {
            for (Entity entity : loadedEntities)
            {
                if (entity instanceof EntityLivingBase && entity != player)
                {
                    stealthLevel = EntitySensesEdit.stealthLevel(player, entity);
                    if (stealthLevel <= 1) queue.add((EntityLivingBase) entity, stealthLevel);
                }
            }
        }
        else
        {
            for (Entity entity : loadedEntities)
            {
                if (entity instanceof EntityLivingBase && entity != player)
                {
                    double distSquared = player.getDistanceSq(entity);
                    if (distSquared <= 2500 && EntitySensesEdit.los(player, entity))
                    {
                        double angleDif = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYawHead).normalize().dotProduct(new Vec3d(entity.posX - player.posX, entity.posY - player.posY, entity.posZ - player.posZ).normalize());

                        //And because Vec3d.fromPitchYaw occasionally returns values barely out of the range of (-1, 1)...
                        if (angleDif < -1) angleDif = -1;
                        else if (angleDif > 1) angleDif = 1;

                        angleDif = TRIG_TABLE.arccos(angleDif); //0 in front, pi in back

                        if (angleDif / Math.PI * 180 <= 70) queue.add((EntityLivingBase) entity, Math.pow(angleDif, 2) * distSquared);
                    }
                }
            }
        }

        return queue;
    }


    public static boolean bypassesThreat(EntityLivingBase livingBase)
    {
        if (serverSettings.threat.bypassThreatSystem || livingBase == null) return true;

        for (Class<? extends Entity> clss : EntityThreatData.threatBypass)
        {
            if (clss.isAssignableFrom(livingBase.getClass())) return true;
        }

        return false;
    }

    public static boolean isPassive(EntityLivingBase livingBase)
    {
        if (livingBase == null || bypassesThreat(livingBase)) return false;

        for (Class<? extends Entity> clss : EntityThreatData.isPassive)
        {
            if (clss.isAssignableFrom(livingBase.getClass())) return true;
        }

        for (Class<? extends Entity> clss : EntityThreatData.isNonPassive)
        {
            if (clss.isAssignableFrom(livingBase.getClass())) return false;
        }

        return MCTools.isPassive(livingBase);
    }


    public static class ThreatData
    {
        public EntityLivingBase searcher;
        public EntityLivingBase target;
        public int threatLevel;
        public String searcherName;

        public ThreatData(EntityLivingBase searcherIn, EntityLivingBase targetIn, int threatLevelIn)
        {
            searcher = searcherIn;
            target = targetIn;
            threatLevel = threatLevelIn;

            searcherName = searcher.getName();
        }

        public ThreatData copy()
        {
            return new ThreatData(searcher, target, threatLevel);
        }

        public boolean equals(ThreatData threatData)
        {
            return threatData != null && threatData.searcher == searcher && threatData.target == target && threatData.threatLevel == threatLevel && threatData.searcherName.equals(searcherName);
        }

        public String toString()
        {
            return searcherName + ", " + target.getName() + ", " + threatLevel;
        }
    }
}
