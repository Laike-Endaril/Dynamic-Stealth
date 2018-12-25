package com.fantasticsource.mctools;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.HashMap;
import java.util.Map;

public class Speedometer
{
    //This thing has a lot of caveats eg...
    //...player motion is smoothed over several updates, but other entities are not
    //...when teleporting long distances, players return an inaccurate, extremely high value until it's out of the smoothing system (currently 5 updates; about 1/4 second)

    public static Map<EntityPlayer, Entry> playerMap = new HashMap<>(1000);

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        Channel channel = ((EntityPlayerMP) event.player).connection.netManager.channel();
        channel.pipeline().addFirst(new PlayerPackets(event.player));
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        playerMap.remove(event.player);
    }

    public static double getSpeed(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            Entry entry = playerMap.get(entity);
            return entry == null ? 0 : entry.calculatedSpeed;
        }

        double res = new Vec3d(entity.prevPosX, entity.prevPosY, entity.prevPosZ).distanceTo(entity.getPositionVector()) * 20;
        return res;
    }

    public static double update(EntityPlayer player, Vec3d newPosition)
    {
        long time = System.nanoTime();
        Entry entry = playerMap.get(player);

        if (entry != null)
        {
            double speed = entry.position.distanceTo(newPosition) / (double) (time - entry.time) * 1e9;
            entry.set(newPosition, time, speed);
            return entry.calculatedSpeed;
        }

        playerMap.put(player, new Entry(newPosition, time));
        return 0;
    }

    public static class Entry
    {
        public Vec3d position;
        public long time;
        public double[] speeds = {0, 0, 0, 0, 0};
        public double calculatedSpeed = 0;

        public Entry(Vec3d position, long time)
        {
            this.position = position;
            this.time = time;
        }

        public void set(Vec3d position, long time, double speed)
        {
            this.position = position;
            this.time = time;
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

    public static class PlayerPackets extends SimpleChannelInboundHandler
    {
        EntityPlayer player;

        public PlayerPackets(EntityPlayer player)
        {
            super(false);
            this.player = player;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg)
        {
            Class packetClass = msg.getClass();
            if (packetClass == CPacketPlayer.Position.class)
            {
                CPacketPlayer.Position data = (CPacketPlayer.Position) msg;
                Speedometer.update(player, new Vec3d(data.getX(Double.NaN), data.getY(Double.NaN), data.getZ(Double.NaN)));
            }
            else if (packetClass == CPacketPlayer.PositionRotation.class)
            {
                CPacketPlayer.PositionRotation data = (CPacketPlayer.PositionRotation) msg;
                Speedometer.update(player, new Vec3d(data.getX(Double.NaN), data.getY(Double.NaN), data.getZ(Double.NaN)));
            }

            ctx.fireChannelRead(msg);
        }
    }
}
