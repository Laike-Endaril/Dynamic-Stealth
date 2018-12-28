package com.fantasticsource.mctools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;

public class Speedometer
{
    //This thing has a lot of caveats eg...
    //...player motion is smoothed over several updates, but other entities are not
    //...when teleporting long distances, players return an inaccurate, extremely high value until it's out of the smoothing system (currently 5 updates; about 1/4 second)

    public static Map<EntityPlayerMP, Entry> playerMap = new HashMap<>(1000);

    @SubscribeEvent
    public static void playerUpdate(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        if (event.phase == TickEvent.Phase.END && player instanceof EntityPlayerMP) update((EntityPlayerMP) player);
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP) playerMap.remove(event.player);
    }

    public static double getSpeed(Entity entity)
    {
        if (entity instanceof EntityPlayerMP)
        {
            Entry entry = playerMap.get(entity);
            return entry == null ? 0 : entry.calculatedSpeed;
        }

        return new Vec3d(entity.prevPosX, entity.prevPosY, entity.prevPosZ).distanceTo(entity.getPositionVector()) * 20;
    }

    public static double update(EntityPlayerMP player)
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
