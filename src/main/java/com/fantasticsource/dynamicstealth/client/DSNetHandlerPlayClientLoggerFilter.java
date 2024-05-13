package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

@SideOnly(Side.CLIENT)
public class DSNetHandlerPlayClientLoggerFilter implements Filter
{
    public static void init()
    {
        ((Logger) ReflectionTool.get(NetHandlerPlayClient.class, new String[]{"field_147301_d", "LOGGER"}, null)).addFilter(new DSNetHandlerPlayClientLoggerFilter());
    }

    @Override
    public Result getOnMismatch()
    {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch()
    {
        return Result.DENY;
    }


    protected Result filter(String message)
    {
        return shouldBlock(message) ? getOnMatch() : getOnMismatch();
    }

    protected boolean shouldBlock(String message)
    {
        return message.contains("Received p");
    }


    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params)
    {
        return filter(msg);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0)
    {
        return filter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1)
    {
        return filter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2)
    {
        return filter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3)
    {
        return filter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4)
    {
        return filter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5)
    {
        return filter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6)
    {
        return filter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7)
    {
        return filter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8)
    {
        return filter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9)
    {
        return filter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t)
    {
        if (msg instanceof String) return filter((String) msg);
        if (msg instanceof Message) return filter(((Message) msg).getFormattedMessage());
        return filter("" + msg);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t)
    {
        return filter(msg.getFormattedMessage());
    }

    @Override
    public Result filter(LogEvent event)
    {
        return filter(event.getMessage().getFormattedMessage());
    }

    @Override
    public State getState()
    {
        return State.STARTED;
    }

    @Override
    public void initialize()
    {
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
    }

    @Override
    public boolean isStarted()
    {
        return true;
    }

    @Override
    public boolean isStopped()
    {
        return false;
    }
}
