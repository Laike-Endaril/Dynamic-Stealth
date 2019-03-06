package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.client.HUD;
import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightData;
import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.mctools.MCTools.isOP;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(DynamicStealth.MODID);

    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(HUDPacketHandler.class, HUDPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(ClientInitPacketHandler.class, ClientInitPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(VisibilityPacketHandler.class, VisibilityPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(SoulSightPacketHandler.class, SoulSightPacket.class, discriminator++, Side.CLIENT);
    }


    @SubscribeEvent
    public static void sendClientData(TickEvent.PlayerTickEvent event)
    {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            if (player != null && player.world.loadedEntityList.contains(player) && player.isEntityAlive())
            {
                if (EntitySightData.hasSoulSight(player))
                {
                    if (!EntitySightData.soulSightCache.contains(player))
                    {
                        EntitySightData.soulSightCache.add(player);
                        WRAPPER.sendTo(new SoulSightPacket(true), player);
                    }
                }
                else
                {
                    if (EntitySightData.soulSightCache.contains(player))
                    {
                        EntitySightData.soulSightCache.remove(player);
                        WRAPPER.sendTo(new SoulSightPacket(false), player);
                    }
                }

                if (serverSettings.senses.usePlayerSenses) WRAPPER.sendTo(new VisibilityPacket(player), player);

                boolean opHUD, detailedOPHUD, stealthGauge;
                if (isOP(player))
                {
                    opHUD = serverSettings.hud.allowOPHUD > 0;
                    detailedOPHUD = serverSettings.hud.allowDetailedOPHUD > 0;
                    stealthGauge = serverSettings.hud.allowStealthGauge > 0;
                }
                else
                {
                    opHUD = serverSettings.hud.allowOPHUD > 1;
                    detailedOPHUD = serverSettings.hud.allowDetailedOPHUD > 1;
                    stealthGauge = serverSettings.hud.allowStealthGauge > 1;
                }

                if (opHUD || stealthGauge) WRAPPER.sendTo(new HUDPacket(player, opHUD, detailedOPHUD, stealthGauge ? (int) (Sight.totalStealthLevel(player) * 100d) : Byte.MIN_VALUE), player);
            }
        }
    }


    @SubscribeEvent
    public static void sendClientInitData(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            WRAPPER.sendTo(new ClientInitPacket(player), player);
        }
    }


    public static class VisibilityPacket implements IMessage
    {
        ExplicitPriorityQueue<EntityLivingBase> queue;
        LinkedHashMap<Integer, Float> visibilityMap;

        public VisibilityPacket() //Required; probably for when the packet is received
        {
        }

        public VisibilityPacket(EntityPlayerMP player)
        {
            queue = Sight.seenEntities(player, false);
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            int i = queue.size();
            buf.writeInt(i);

            if (serverSettings.senses.usePlayerSenses)
            {
                for (; i > 0; i--)
                {
                    buf.writeFloat((float) (1d - queue.peekPriority()));
                    buf.writeInt(queue.poll().getEntityId());
                }
            }
            else
            {
                for (; i > 0; i--)
                {
                    buf.writeFloat(1);
                    buf.writeInt(queue.poll().getEntityId());
                }
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            visibilityMap = new LinkedHashMap<>();
            float visibility;

            for (int i = buf.readInt(); i > 0; i--)
            {
                visibility = buf.readFloat();
                visibilityMap.put(buf.readInt(), visibility);
            }
        }
    }

    public static class VisibilityPacketHandler implements IMessageHandler<VisibilityPacket, IMessage>
    {
        @Override
        public IMessage onMessage(VisibilityPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    ClientData.visibilityMap = packet.visibilityMap;
                });
            }

            return null;
        }
    }


    public static class SoulSightPacket implements IMessage
    {
        boolean soulSight;

        public SoulSightPacket() //Required; probably for when the packet is received
        {
        }

        public SoulSightPacket(boolean soulSight)
        {
            this.soulSight = soulSight;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(soulSight);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            soulSight = buf.readBoolean();
        }
    }

    public static class SoulSightPacketHandler implements IMessageHandler<SoulSightPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SoulSightPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    ClientData.soulSight = packet.soulSight;
                });
            }

            return null;
        }
    }


    public static class ClientInitPacket implements IMessage
    {
        boolean soulSight;
        boolean usePlayerSenses;

        public ClientInitPacket() //Required; probably for when the packet is received
        {
        }

        public ClientInitPacket(EntityPlayerMP player)
        {
            soulSight = EntitySightData.hasSoulSight(player);
            if (soulSight) EntitySightData.soulSightCache.add(player);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(soulSight);
            buf.writeBoolean(serverSettings.senses.usePlayerSenses);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            soulSight = buf.readBoolean();
            usePlayerSenses = buf.readBoolean();
        }
    }

    public static class ClientInitPacketHandler implements IMessageHandler<ClientInitPacket, IMessage>
    {
        @Override
        public IMessage onMessage(ClientInitPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    ClientData.soulSight = packet.soulSight;
                    ClientData.usePlayerSenses = packet.usePlayerSenses;
                });
            }

            return null;
        }
    }


    public static class HUDPacket implements IMessage
    {
        EntityPlayerMP player;
        ExplicitPriorityQueue<EntityLivingBase> queue;

        boolean detailHUD;
        int stealthLevel;

        ArrayList<ClientData.OnPointData> list = new ArrayList<>();

        public HUDPacket() //Required; probably for when the packet is received
        {
        }

        public HUDPacket(EntityPlayerMP player, boolean opHUD, boolean detailHUD, int stealthLevel)
        {
            this.player = player;
            this.detailHUD = detailHUD;
            this.stealthLevel = stealthLevel;

            queue = opHUD ? Sight.seenEntities(player, true) : new ExplicitPriorityQueue<>();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            EntityLivingBase searcher;
            int maxThreat = serverSettings.threat.maxThreat;

            buf.writeByte(stealthLevel);

            buf.writeBoolean(detailHUD);
            buf.writeInt(queue.size());

            if (detailHUD)
            {
                while (queue.size() > 0)
                {
                    searcher = queue.poll();
                    if (EntityThreatData.bypassesThreat(searcher))
                    {
                        //Color
                        buf.writeByte(ClientData.CID_BYPASS);
                        //Searcher ID
                        buf.writeInt(searcher.getEntityId());
                        //Target ID
                        Entity target = (searcher instanceof EntityLiving) ? ((EntityLiving) searcher).getAttackTarget() : null;
                        buf.writeInt(target == null ? -1 : target.getEntityId());
                        //Threat level
                        buf.writeByte(-1);
                    }
                    else
                    {
                        Threat.ThreatData data = Threat.get(searcher);

                        //Color
                        buf.writeByte(ClientData.getCID(player, searcher, data.target, data.threatLevel)); //else this is a normal entry
                        //Searcher ID
                        buf.writeInt(searcher.getEntityId());
                        //Target ID
                        buf.writeInt(data.target == null ? -1 : data.target.getEntityId());
                        //Threat level
                        buf.writeByte((int) (100D * data.threatLevel / maxThreat));
                    }
                }
            }
            else
            {
                while (queue.size() > 0)
                {
                    searcher = queue.poll();
                    if (EntityThreatData.bypassesThreat(searcher))
                    {
                        //Color
                        buf.writeByte(ClientData.CID_BYPASS);
                        //Searcher ID
                        buf.writeInt(searcher.getEntityId());
                        //Threat level
                        buf.writeByte(-1);
                    }
                    else
                    {
                        Threat.ThreatData data = Threat.get(searcher);

                        //Color
                        buf.writeByte(ClientData.getCID(player, searcher, data.target, data.threatLevel)); //else this is a normal entry
                        //Searcher ID
                        buf.writeInt(searcher.getEntityId());
                        //Threat level
                        buf.writeByte((int) (100D * data.threatLevel / maxThreat));
                    }
                }
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            list.clear();

            stealthLevel = buf.readByte();

            detailHUD = buf.readBoolean();
            int remaining = buf.readInt();

            if (detailHUD)
            {
                for (; remaining > 0; remaining--)
                {
                    list.add(new ClientData.OnPointData(ClientData.getColor(buf.readByte()), buf.readInt(), buf.readInt(), buf.readByte()));
                }
            }
            else
            {
                for (; remaining > 0; remaining--)
                {
                    list.add(new ClientData.OnPointData(ClientData.getColor(buf.readByte()), buf.readInt(), -2, buf.readByte()));
                }
            }
        }
    }

    public static class HUDPacketHandler implements IMessageHandler<HUDPacket, IMessage>
    {
        @Override
        public IMessage onMessage(HUDPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    ClientData.stealthLevel = packet.stealthLevel;
                    System.out.println(ClientData.stealthLevel);

                    ClientData.detailData = null;
                    for (ClientData.OnPointData data : packet.list)
                    {
                        if (HUD.detailFilter(data.color))
                        {
                            ClientData.detailData = data;
                            break;
                        }
                    }

                    ClientData.opList = packet.list;
                    if (ClientData.detailData != null) ClientData.opList.remove(ClientData.detailData);

                    ClientData.opMap.clear();
                    for (ClientData.OnPointData data : ClientData.opList)
                    {
                        ClientData.opMap.put(data.searcherID, data);
                    }
                });
            }

            return null;
        }
    }
}
