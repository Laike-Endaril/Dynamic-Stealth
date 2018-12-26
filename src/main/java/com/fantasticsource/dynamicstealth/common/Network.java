package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.client.HUD;
import com.fantasticsource.dynamicstealth.server.Threat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static com.fantasticsource.dynamicstealth.client.HUD.EMPTY;
import static com.fantasticsource.mctools.MCTools.*;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(DynamicStealth.MODID);

    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(ThreatPacketHandler.class, ThreatPacket.class, discriminator++, Side.CLIENT);
    }


    public static void sendThreatData(EntityPlayerMP player, Threat.ThreatData threatData)
    {
        if (threatData == null) sendThreatData(player, null, null, 0);
        else sendThreatData(player, threatData.searcher, threatData.target, threatData.threatLevel, false);
    }

    public static void sendThreatData(EntityPlayerMP player, Threat.ThreatData threatData, boolean permissionOverride)
    {
        sendThreatData(player, threatData.searcher, threatData.target, threatData.threatLevel, permissionOverride);
    }

    public static void sendThreatData(EntityPlayerMP player, EntityLiving searcher, EntityLivingBase target, int threatLevel)
    {
        sendThreatData(player, searcher, target, threatLevel, false);
    }

    public static void sendThreatData(EntityPlayerMP player, EntityLiving searcher, EntityLivingBase target, int threatLevel, boolean permissionOverride)
    {
        int mode = DynamicStealthConfig.serverSettings.threat.allowClientHUD;
        if (permissionOverride || mode == 2 || (mode == 1 && isOP(player)))
        {
            WRAPPER.sendTo(new ThreatPacket(searcher == null ? EMPTY : searcher.getName(), target == null ? EMPTY : target.getName(), threatLevel), player);
        }
    }

    public static class ThreatPacket implements IMessage
    {
        String searcher, target;
        int threatLevel;

        public ThreatPacket() //This seems to be required, even if unused
        {
        }

        public ThreatPacket(String searcherIn, String targetIn, int threatLevelIn)
        {
            searcher = searcherIn;
            target = targetIn;
            threatLevel = threatLevelIn;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, searcher);
            ByteBufUtils.writeUTF8String(buf, target);
            buf.writeInt(threatLevel);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            searcher = ByteBufUtils.readUTF8String(buf);
            target = ByteBufUtils.readUTF8String(buf);
            threatLevel = buf.readInt();
        }
    }

    public static class ThreatPacketHandler implements IMessageHandler<ThreatPacket, IMessage>
    {
        @Override
        public IMessage onMessage(ThreatPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    HUD.threatSearcher = packet.searcher;
                    HUD.threatTarget = packet.target;
                    HUD.threatLevel = packet.threatLevel;
                });
            }

            return null;
        }
    }


    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
            Channel channel = playerMP.connection.netManager.channel();
            channel.pipeline().addFirst(new PlayerPackets(playerMP));
        }
    }

    public static class PlayerPackets extends SimpleChannelInboundHandler
    {
        EntityPlayerMP player;

        public PlayerPackets(EntityPlayerMP playerIn)
        {
            super(false);
            player = playerIn;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg)
        {
            Class packetClass = msg.getClass();
            if (packetClass == CPacketPlayer.Position.class)
            {
                CPacketPlayer.Position data = (CPacketPlayer.Position) msg;

                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
                {
                    EntityLiving searcher = Threat.focusedEntity(player, data.getX(player.posX), data.getY(player.posY), data.getZ(player.posZ), player.rotationYawHead, player.rotationPitch);
                    Threat.watchers.set(player, searcher);
                });
            }
            else if (packetClass == CPacketPlayer.PositionRotation.class)
            {
                CPacketPlayer.PositionRotation data = (CPacketPlayer.PositionRotation) msg;
                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
                {
                    EntityLiving searcher = Threat.focusedEntity(player, data.getX(player.posX), data.getY(player.posY), data.getZ(player.posZ), data.getYaw(player.rotationYawHead), data.getPitch(player.rotationPitch));
                    Threat.watchers.set(player, searcher);
                });
            }
            else if (packetClass == CPacketPlayer.Rotation.class)
            {
                CPacketPlayer.Rotation data = (CPacketPlayer.Rotation) msg;
                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
                {
                    EntityLiving searcher = Threat.focusedEntity(player, player.posX, player.posY, player.posZ, data.getYaw(player.rotationYawHead), data.getPitch(player.rotationPitch));
                    Threat.watchers.set(player, searcher);
                });
            }

            ctx.fireChannelRead(msg);
        }
    }
}
