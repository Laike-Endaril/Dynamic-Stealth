package com.fantasticsource.mctools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class Speedometer
{
    //Player motion is smoothed over several updates to help account for inconsistent packet reception rates, but other entities are not

    public static Map<EntityPlayerMP, Entry> playerMap = new LinkedHashMap<>(1000);
    public static Map<EntityLiving, Integer> recentTeleports = new LinkedHashMap<>(10);

    @SubscribeEvent
    public static void playerUpdateEvent(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        if (event.phase == TickEvent.Phase.END && player instanceof EntityPlayerMP) updatePlayer((EntityPlayerMP) player);
    }

    @SubscribeEvent
    public static void teleport(EnderTeleportEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        if (livingBase instanceof EntityPlayerMP) remove((EntityPlayerMP) livingBase);
        else if (livingBase instanceof EntityLiving) recentTeleports.put((EntityLiving) livingBase, 3);
    }

    @SubscribeEvent
    public static void dimChange(EntityTravelToDimensionEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP) remove((EntityPlayerMP) entity);
        else if (entity instanceof EntityLiving) recentTeleports.put((EntityLiving) entity, 3);
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP) remove((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event)
    {
        recentTeleports.entrySet().removeIf(Speedometer::checkRemoveTeleporting);
    }

    private static boolean checkRemoveTeleporting(Map.Entry<EntityLiving, Integer> entry)
    {
        int time = entry.getValue();
        if (--time >= 0) return true;

        entry.setValue(time);
        return false;
    }


    private static void remove(EntityPlayerMP player)
    {
        playerMap.remove(player);
    }

    public static double getSpeed(Entity entity)
    {
        if (entity instanceof EntityPlayerMP)
        {
            Entry entry = playerMap.get(entity);
            return entry == null ? 0 : entry.calculatedSpeed;
        }

        if (recentTeleports.containsKey(entity)) return 0;

        return new Vec3d(entity.prevPosX, entity.prevPosY, entity.prevPosZ).distanceTo(entity.getPositionVector()) * 20;
    }

    public static double updatePlayer(EntityPlayerMP player)
    {
        Vec3d newVec = player.getPositionVector();

        Entry entry = playerMap.get(player);
        if (entry == null)
        {
            playerMap.put(player, new Entry(newVec));
            return 0;
        }

        entry.set(newVec, entry.position.distanceTo(newVec) * 20);
        return entry.calculatedSpeed;
    }

    public static class Entry
    {
        public Vec3d position;
        private double[] speeds = {0, 0, 0, 0, 0};
        public double calculatedSpeed = 0;

        public Entry(Vec3d position)
        {
            this.position = position;
        }

        public void set(Vec3d position, double speed)
        {
            this.position = position;
            calculatedSpeed = 0;
            for (int i = speeds.length - 1; i > 0; i--)
            {
                speeds[i] = speeds[i - 1];
                calculatedSpeed += speeds[i];
            }
            speeds[0] = speed;
            calculatedSpeed = (calculatedSpeed + speed) / speeds.length;
        }
    }
}
