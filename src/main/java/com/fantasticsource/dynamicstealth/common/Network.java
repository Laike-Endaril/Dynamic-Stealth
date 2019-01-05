package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.client.HUD;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;
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
                if (detailHUD || onPointHUDMode > 0) WRAPPER.sendTo(new HUDPacket(queue.toArray(), detailHUD, onPointHUDMode), player);
            }
            else
            {
                boolean detailHUD = serverSettings.threat.hud.allowClientDetailHUD > 1;
                int onPointHUDMode = serverSettings.threat.hud.normalOnPointHUD;
                if (detailHUD || onPointHUDMode > 0) WRAPPER.sendTo(new HUDPacket(queue.toArray(), detailHUD, onPointHUDMode), player);
            }
        }
    }

    public static class HUDPacket implements IMessage
    {
        EntityLivingBase[] entities;
        boolean detailHUD;
        int onPointHUDMode;

        public HUDPacket() //This seems to be required, even if unused
        {
        }

        public HUDPacket(EntityLivingBase[] entitiesIn, boolean detailHUDIn, int onPointHUDModeIn)
        {
            entities = entitiesIn;
            detailHUD = detailHUDIn;
            onPointHUDMode = onPointHUDModeIn;

//            if (searcher == null) WRAPPER.sendTo(new HUDPacket(EMPTY, EMPTY, 0, COLOR_NULL), player);
//            else if (Threat.bypassesThreat(searcher)) WRAPPER.sendTo(new HUDPacket(searcher.getName(), "", -1, 0), player);
//            else WRAPPER.sendTo(new HUDPacket(searcher.getName(), target == null ? EMPTY : target.getName(), threatLevel, HUD.getColor(player, searcher, target, threatLevel)), player);
//
//
//
//            searcher = searcherIn;
//            target = targetIn;
//            threatLevel = threatLevelIn;
//            color = colorIn;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(detailHUD);
            buf.writeInt(onPointHUDMode);

            if (detailHUD)
            {
                //TODO Send detailed data for one entry
            }

            if (onPointHUDMode == 2)
            {
                //TODO Send limited data for all entries
            }
            else if (onPointHUDMode == 1 && !detailHUD)
            {
                //TODO Send limited data for one entry
            }
//            ByteBufUtils.writeUTF8String(buf, searcher);
//            ByteBufUtils.writeUTF8String(buf, target);
//            buf.writeInt(threatLevel);
//            buf.writeInt(color);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
//            searcher = ByteBufUtils.readUTF8String(buf);
//            target = ByteBufUtils.readUTF8String(buf);
//            threatLevel = buf.readInt();
//            color = buf.readInt();
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
                    //TODO
                });
            }

            return null;
        }
    }
}
