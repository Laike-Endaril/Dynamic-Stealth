package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.server.Threat;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.fantasticsource.tools.datastructures.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.common.HUDData.*;
import static com.fantasticsource.dynamicstealth.server.Threat.bypassesThreat;
import static com.fantasticsource.mctools.MCTools.isOP;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(DynamicStealth.MODID);

    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(ThreatPacketHandler.class, HUDPacket.class, discriminator++, Side.CLIENT);
    }


    public static void sendThreatData(EntityPlayerMP player, ExplicitPriorityQueue<EntityLivingBase> queue)
    {
        if (player != null && player.world.loadedEntityList.contains(player))
        {
            if (isOP(player))
            {
                boolean detailHUD = serverSettings.threat.hud.allowClientDetailHUD > 0;
                int onPointHUDMode = serverSettings.threat.hud.opOnPointHUD;
                if (detailHUD || onPointHUDMode > 0) WRAPPER.sendTo(new HUDPacket(player, queue, detailHUD, onPointHUDMode), player);
            }
            else
            {
                boolean detailHUD = serverSettings.threat.hud.allowClientDetailHUD > 1;
                int onPointHUDMode = serverSettings.threat.hud.normalOnPointHUD;
                if (detailHUD || onPointHUDMode > 0) WRAPPER.sendTo(new HUDPacket(player, queue, detailHUD, onPointHUDMode), player);
            }
        }
    }

    public static class HUDPacket implements IMessage
    {
        EntityPlayerMP player;
        ExplicitPriorityQueue<EntityLivingBase> queue;
        boolean detailHUD;
        int onPointHUDMode;

        String detailSearcherName;
        String detailTargetName;
        int detailPercent;
        int detailColor;

        //Output / client side only
        Map<Integer, Pair<Integer, Integer>> onPointMap = new LinkedHashMap<>(10);

        public HUDPacket() //This seems to be required, even if unused
        {
        }

        public HUDPacket(EntityPlayerMP playerIn, ExplicitPriorityQueue<EntityLivingBase> queueIn, boolean detailHUDIn, int onPointHUDModeIn)
        {
            player = playerIn;
            queue = queueIn;
            detailHUD = detailHUDIn;
            onPointHUDMode = onPointHUDModeIn;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            EntityLivingBase searcher;
            int maxThreat = serverSettings.threat.maxThreat;

            buf.writeBoolean(detailHUD);
            buf.writeInt(onPointHUDMode);

            if (detailHUD)
            {
                searcher = queue.poll();
                if (searcher == null) buf.writeInt(COLOR_NULL); //if color == COLOR_NULL then searcher is null
                else if (bypassesThreat(searcher))
                {
                    buf.writeInt(COLOR_ALERT);
                    buf.writeInt(-1); //else if threatLevel == -1 then searcher bypasses threat
                    ByteBufUtils.writeUTF8String(buf, searcher.getName());

                    if (onPointHUDMode > 0) buf.writeInt(searcher.getEntityId());
                }
                else
                {
                    Threat.ThreatData data = Threat.get(searcher);

                    buf.writeInt(getColor(player, searcher, data.target, data.threatLevel)); //else this is a normal entry
                    buf.writeInt((int) (100D * data.threatLevel / maxThreat));

                    ByteBufUtils.writeUTF8String(buf, searcher.getName());
                    ByteBufUtils.writeUTF8String(buf, data.target == null ? EMPTY : data.target.getName());

                    if (onPointHUDMode > 0) buf.writeInt(searcher.getEntityId());
                }
            }
            else //!detailHUD
            {
                if (onPointHUDMode == 1)
                {
                    searcher = queue.poll();
                    if (searcher == null) buf.writeInt(COLOR_NULL);
                    else
                    {
                        Threat.ThreatData data = Threat.get(searcher);

                        buf.writeInt(getColor(player, searcher, data.target, data.threatLevel));
                        buf.writeInt(searcher.getEntityId());
                        buf.writeInt((int) (100D * data.threatLevel / maxThreat));
                    }
                }
            }

            if (onPointHUDMode == 2)
            {
                buf.writeInt(queue.size());

                while (queue.size() > 0)
                {
                    searcher = queue.poll();
                    if (searcher == null) buf.writeInt(COLOR_NULL); //if color == COLOR_NULL then searcher is null
                    else if (bypassesThreat(searcher))
                    {
                        buf.writeInt(COLOR_ALERT);
                        buf.writeInt(searcher.getEntityId());
                        buf.writeInt(-1); //else if threatLevel == -1 then searcher bypasses threat
                    }
                    else
                    {
                        Threat.ThreatData data = Threat.get(searcher);

                        buf.writeInt(getColor(player, searcher, data.target, data.threatLevel)); //else this is a normal entry
                        buf.writeInt(searcher.getEntityId());
                        buf.writeInt((int) (100D * data.threatLevel / maxThreat));
                    }
                }
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            onPointMap.clear();
            int color;

            detailHUD = buf.readBoolean();
            onPointHUDMode = buf.readInt();

            if (detailHUD)
            {
                detailColor = buf.readInt();

                if (detailColor != COLOR_NULL)
                {
                    detailPercent = buf.readInt();

                    detailSearcherName = ByteBufUtils.readUTF8String(buf);
                    detailTargetName = detailPercent == -1 ? UNKNOWN : ByteBufUtils.readUTF8String(buf);

                    if (onPointHUDMode > 0) onPointMap.put(buf.readInt(), new Pair<>(detailColor, detailPercent));
                }
            }
            else
            {
                if (onPointHUDMode == 1)
                {
                    color = buf.readInt();
                    if (color != COLOR_NULL) onPointMap.put(buf.readInt(), new Pair<>(color, buf.readInt()));
                }
            }

            if (onPointHUDMode == 2)
            {
                for (int i = buf.readInt(); i > 0; i--)
                {
                    color = buf.readInt();
                    if (color != COLOR_NULL) onPointMap.put(buf.readInt(), new Pair<>(color, buf.readInt()));
                }
            }
        }
    }

    public static class OnPointData
    {
        public int entityID;
        public int color;
        public int percent;

        public OnPointData(int idIn, int colorIn, int percentIn)
        {
            entityID = idIn;
            color = colorIn;
            percent = percentIn;
        }
    }

    public static class ThreatPacketHandler implements IMessageHandler<HUDPacket, IMessage>
    {
        @Override
        public IMessage onMessage(HUDPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    detailColor = packet.detailColor;

                    if (detailColor == COLOR_NULL)
                    {
                        detailPercent = -1;
                        detailSearcher = EMPTY;
                        detailTarget = EMPTY;
                    }
                    else
                    {
                        detailSearcher = packet.detailSearcherName;
                        detailPercent = packet.detailPercent;

                        detailTarget = detailPercent == -1 ? EMPTY : packet.detailTargetName;
                    }

                    onPointDataMap = packet.onPointMap;
                });
            }

            return null;
        }
    }
}
