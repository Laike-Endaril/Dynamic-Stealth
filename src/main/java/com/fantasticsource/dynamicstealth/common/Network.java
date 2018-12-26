package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.client.HUD;
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

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(DynamicStealth.MODID);

    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(ThreatPacketHandler.class, ThreatPacket.class, discriminator++, Side.CLIENT);
    }


    public static void sendThreatData(EntityPlayerMP player, EntityLiving searcher, EntityLivingBase target, int threatLevel)
    {
        WRAPPER.sendTo(new ThreatPacket(searcher == null ? EMPTY : searcher.getName(), target == null ? EMPTY : target.getName(), threatLevel), player);
    }

    public static class ThreatPacket implements IMessage
    {
        String searcher, target;
        int threatLevel;

        public ThreatPacket()
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
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    HUD.threatSearcher = packet.searcher;
                    HUD.threatTarget = packet.target;
                    HUD.threatLevel = packet.threatLevel;
                });
            }

            return null;
        }
    }
}
