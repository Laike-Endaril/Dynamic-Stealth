package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.server.Threat.Threat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.server.Threat.Threat.bypassesThreat;

public class ClientData
{
    public static final int COLOR_NULL = 0x777777;
    public static final int COLOR_ATTACKING_YOU = 0xFF0000;
    public static final int COLOR_ALERT = 0xFF8800;
    public static final int COLOR_ATTACKING_OTHER = 0xFFFF00;
    public static final int COLOR_IDLE = 0x4444FF;
    public static final int COLOR_PASSIVE = 0x00CC00;

    public static final String EMPTY = "----------";
    public static final String UNKNOWN = "???";

    public static String detailSearcher = EMPTY;
    public static String detailTarget = EMPTY;
    public static int detailPercent = 0;
    public static int detailColor = COLOR_NULL;

    public static LinkedHashMap<Integer, OnPointData> onPointDataMap = new LinkedHashMap<>();

    public static LinkedHashMap<Integer, Float> visibilityMap = new LinkedHashMap<>();


    @SubscribeEvent
    public static void clearHUD(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        detailColor = COLOR_NULL;
        detailSearcher = EMPTY;
        detailTarget = EMPTY;
        detailPercent = 0;

        onPointDataMap.clear();
        visibilityMap.clear();
    }


    public static int getColor(EntityPlayer player, EntityLivingBase searcher, EntityLivingBase target, int threatLevel)
    {
        if (searcher == null) return COLOR_NULL;
        if (bypassesThreat(searcher)) return COLOR_ALERT;
        if (serverSettings.threat.recognizePassive && Threat.isPassive(searcher)) return COLOR_PASSIVE;
        if (threatLevel <= 0) return COLOR_IDLE;
        if (target == null) return COLOR_ALERT;
        if (target == player) return COLOR_ATTACKING_YOU;
        return COLOR_ATTACKING_OTHER;
    }

    public static class OnPointData
    {
        public int color, percent, priority;

        public OnPointData(int color, int percent, int priority)
        {
            this.color = color;
            this.percent = percent;
            this.priority = priority;
        }
    }
}
