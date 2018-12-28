package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.client.HUD;
import com.fantasticsource.dynamicstealth.server.Threat;
import com.fantasticsource.mctools.MCTools;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static com.fantasticsource.dynamicstealth.client.HUD.EMPTY;
import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.*;
import static com.fantasticsource.mctools.MCTools.isOP;

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
        int mode = serverSettings.threat.allowClientHUD;
        if (permissionOverride || mode == 2 || (mode == 1 && isOP(player)))
        {
            WRAPPER.sendTo(new ThreatPacket(searcher == null ? EMPTY : searcher.getName(), target == null ? EMPTY : target.getName(), threatLevel, searcher != null && serverSettings.threat.recognizePassive && MCTools.isPassive(searcher)), player);
        }
    }

    public static class ThreatPacket implements IMessage
    {
        String searcher, target;
        int threatLevel;
        boolean isPassive = false;

        public ThreatPacket() //This seems to be required, even if unused
        {
        }

        public ThreatPacket(String searcherIn, String targetIn, int threatLevelIn, boolean isPassiveIn)
        {
            searcher = searcherIn;
            target = targetIn;
            threatLevel = threatLevelIn;
            isPassive = isPassiveIn;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, searcher);
            ByteBufUtils.writeUTF8String(buf, target);
            buf.writeInt(threatLevel);
            buf.writeBoolean(isPassive);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            searcher = ByteBufUtils.readUTF8String(buf);
            target = ByteBufUtils.readUTF8String(buf);
            threatLevel = buf.readInt();
            isPassive = buf.readBoolean();
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
                    HUD.searcherIsPassive = packet.isPassive;
                });
            }

            return null;
        }
    }
}
