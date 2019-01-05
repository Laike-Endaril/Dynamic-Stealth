package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.client.HUD;
import com.fantasticsource.dynamicstealth.server.Threat;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
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

import static com.fantasticsource.dynamicstealth.client.HUD.*;
import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;
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
        int detailThreatLevel;
        int detailColor;

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
            buf.writeBoolean(detailHUD);
            buf.writeInt(onPointHUDMode);

            EntityLivingBase searcher;
            if (detailHUD)
            {
                searcher = queue.poll();
                if (searcher == null)
                {
                    ByteBufUtils.writeUTF8String(buf, EMPTY);
                    ByteBufUtils.writeUTF8String(buf, EMPTY);
                    buf.writeInt(0);
                    buf.writeInt(COLOR_NULL);
                }
                else if (bypassesThreat(searcher))
                {
                    ByteBufUtils.writeUTF8String(buf, searcher.getName());
                    ByteBufUtils.writeUTF8String(buf, UNKNOWN);
                    buf.writeInt(-1);
                    buf.writeInt(COLOR_ALERT);
                }
                else
                {
                    Threat.ThreatData data = Threat.get(searcher);
                    ByteBufUtils.writeUTF8String(buf, searcher.getName());
                    ByteBufUtils.writeUTF8String(buf, data.target == null ? EMPTY : data.target.getName());
                    buf.writeInt(data.threatLevel);
                    buf.writeInt(HUD.getColor(player, searcher, data.target, data.threatLevel));
                }
            }

            if (onPointHUDMode == 2)
            {
                //TODO Send limited data for all entities; use IDs instead of names
            }
            else if (onPointHUDMode == 1 && !detailHUD)
            {
                //TODO Send limited data for one entity; use IDs instead of names
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            detailHUD = buf.readBoolean();
            onPointHUDMode = buf.readInt();

            if (detailHUD)
            {
                detailSearcherName = ByteBufUtils.readUTF8String(buf);
                detailTargetName = ByteBufUtils.readUTF8String(buf);
                detailThreatLevel = buf.readInt();
                detailColor = buf.readInt();
            }

            if (onPointHUDMode == 2)
            {
                //TODO Receive limited data for all entities
            }
            else if (onPointHUDMode == 1 && !detailHUD)
            {
                //TODO Receive limited data for one entity
            }
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
                    detailSearcher = packet.detailSearcherName;
                    detailTarget = packet.detailTargetName;
                    detailThreatLevel = packet.detailThreatLevel;
                    detailColor = packet.detailColor;
                });
            }

            return null;
        }
    }
}
