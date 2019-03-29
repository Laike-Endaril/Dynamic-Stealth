package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightData;
import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.mctools.ServerTickTimer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
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
import java.util.Map;

import static com.fantasticsource.dynamicstealth.common.ClientData.*;
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
        event.player.world.profiler.startSection("DStealth: Send client data");
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            if (player.world.loadedEntityList.contains(player) && player.isEntityAlive())
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

                boolean opHUD, targetElement, stealthGauge;
                if (isOP(player))
                {
                    opHUD = serverSettings.hud.allowOPHUD > 0;
                    targetElement = serverSettings.hud.allowTargetElement > 0;
                    stealthGauge = serverSettings.hud.allowStealthGauge > 0;
                }
                else
                {
                    opHUD = serverSettings.hud.allowOPHUD > 1;
                    targetElement = serverSettings.hud.allowTargetElement > 1;
                    stealthGauge = serverSettings.hud.allowStealthGauge > 1;
                }

                if (opHUD || stealthGauge)
                {
                    double totalStealth = Sight.totalStealthLevel(player);
                    player.world.profiler.startSection("DStealth: Create HUDPacket");
                    IMessage packet = new HUDPacket(player, opHUD, targetElement, !stealthGauge ? Byte.MIN_VALUE : totalStealth == Double.MAX_VALUE ? Byte.MIN_VALUE + 1 : (int) (totalStealth * 100)); //Byte.MIN_VALUE means disabled
                    player.world.profiler.endStartSection("DStealth: Send HUDPacket");
                    WRAPPER.sendTo(packet, player);
                    player.world.profiler.endSection();
                }
            }
        }
        event.player.world.profiler.endSection();
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
        LinkedHashMap<EntityLivingBase, Double> inputMap;
        LinkedHashMap<Integer, Float> visibilityMap;

        public VisibilityPacket() //Required; probably for when the packet is received
        {
        }

        public VisibilityPacket(EntityPlayerMP player)
        {
            inputMap = Sight.seenEntities(player);
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            int i = inputMap.size();
            buf.writeInt(i);

            for (Map.Entry<EntityLivingBase, Double> entry : inputMap.entrySet())
            {
                buf.writeFloat((float) (1d - entry.getValue()));
                buf.writeInt(entry.getKey().getEntityId());
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
        boolean targetElement, update;
        int stealthLevel;
        ArrayList<EntityLivingBase> inputList = new ArrayList<>();

        ArrayList<ClientData.OnPointData> outputList = new ArrayList<>();


        public HUDPacket() //Required; probably for when the packet is received
        {
        }

        public HUDPacket(EntityPlayerMP player, boolean opHUD, boolean targetElement, int stealthLevel)
        {
            this.player = player;
            this.targetElement = targetElement;

            if (ServerTickTimer.currentTick() % Sight.maxAITickrate == 0) this.stealthLevel = stealthLevel;
            else this.stealthLevel = Byte.MIN_VALUE + 1;

            BlockPos playerPos = player.getPosition();
            int rangeSq = serverSettings.hud.opHUDRange << 1;
            int delay = serverSettings.hud.opHUDDelay;

            update = opHUD && ServerTickTimer.currentTick() % delay == player.getEntityId() % delay;

            if (update)
            {
                for (EntityLivingBase searcher : Sight.seenEntities(player).keySet())
                {
                    if (searcher.getDistanceSq(playerPos) <= rangeSq) inputList.add(searcher);
                }
            }
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            int maxThreat = serverSettings.threat.maxThreat;

            buf.writeByte(stealthLevel);

            buf.writeBoolean(update);
            if (update)
            {
                buf.writeBoolean(targetElement);
                buf.writeInt(inputList.size());

                if (targetElement)
                {
                    for (EntityLivingBase searcher : inputList)
                    {
                        if (EntityThreatData.bypassesThreat(searcher))
                        {
                            //Color
                            buf.writeByte(ClientData.CID_BYPASS);
                            //Searcher ID
                            buf.writeInt(searcher.getEntityId());
                            //Target ID
                            Entity target = (searcher instanceof EntityLiving) ? ((EntityLiving) searcher).getAttackTarget() : null;
                            buf.writeInt(target == null ? -1 : target.getEntityId());
                        }
                        else
                        {
                            Threat.ThreatData data = Threat.get(searcher);
                            byte cid = ClientData.getCID(player, searcher, data.target, data.threatLevel);

                            //Color
                            buf.writeByte(cid);
                            //Searcher ID
                            buf.writeInt(searcher.getEntityId());

                            //Target ID
                            if (canHaveClientTarget(cid)) buf.writeInt(data.target == null ? -1 : data.target.getEntityId());
                            //Threat level
                            if (canHaveThreat(cid)) buf.writeByte((int) (100D * data.threatLevel / maxThreat));
                        }
                    }
                }
                else
                {
                    for (EntityLivingBase searcher : inputList)
                    {
                        if (EntityThreatData.bypassesThreat(searcher))
                        {
                            //Color
                            buf.writeByte(ClientData.CID_BYPASS);
                            //Searcher ID
                            buf.writeInt(searcher.getEntityId());
                        }
                        else
                        {
                            Threat.ThreatData data = Threat.get(searcher);
                            byte cid = ClientData.getCID(player, searcher, data.target, data.threatLevel);

                            //Color
                            buf.writeByte(ClientData.getCID(player, searcher, data.target, data.threatLevel));
                            //Searcher ID
                            buf.writeInt(searcher.getEntityId());
                            //Threat level
                            if (canHaveThreat(cid)) buf.writeByte((int) (100D * data.threatLevel / maxThreat));
                        }
                    }
                }
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            stealthLevel = buf.readByte();
            update = buf.readBoolean();
            if (update)
            {
                targetElement = buf.readBoolean();
                int remaining = buf.readInt();

                if (targetElement)
                {
                    for (; remaining > 0; remaining--)
                    {
                        int color = ClientData.getColor(buf.readByte());
                        outputList.add(new OnPointData(color, buf.readInt(), canHaveClientTarget(color) ? buf.readInt() : -1, canHaveThreat(color) ? buf.readByte() : 0));
                    }
                }
                else
                {
                    for (; remaining > 0; remaining--)
                    {
                        int color = ClientData.getColor(buf.readByte());
                        outputList.add(new OnPointData(color, buf.readInt(), -2, canHaveThreat(color) ? buf.readByte() : 0));
                    }
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
                    int stealth = packet.stealthLevel;
                    if (stealth != Byte.MIN_VALUE + 1) ClientData.stealthLevel = stealth;

                    if (packet.update)
                    {
                        ClientData.opMap.clear();
                        int target = ClientData.targetData == null ? -1 : ClientData.targetData.targetID;
                        for (ClientData.OnPointData data : packet.outputList)
                        {
                            ClientData.opMap.put(data.searcherID, data);
                            if (data.searcherID == target) ClientData.targetData = data;
                        }
                    }
                });
            }

            return null;
        }
    }
}
