package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class ClientData
{
    public static final int COLOR_NULL = 0x777777;
    public static final int COLOR_ATTACKING_YOU = 0xFF0000;
    public static final int COLOR_ALERT = 0xFF8800;
    public static final int COLOR_ATTACKING_OTHER = 0xFFFF00;
    public static final int COLOR_IDLE = 0x4444FF;
    public static final int COLOR_PASSIVE = 0x00CC00;
    public static final int COLOR_FLEEING = 0x770077;
    public static final int COLOR_BYPASS = 0x333333;

    public static final String EMPTY = "----------";
    public static final String UNKNOWN = "???";

    public static String detailSearcher = EMPTY;
    public static String detailTarget = EMPTY;
    public static int detailPercent = 0;
    public static int detailColor = COLOR_NULL;

    public static boolean soulSight = false;
    public static boolean usePlayerSenses = false;

    public static LinkedHashMap<Integer, OnPointData> onPointDataMap = new LinkedHashMap<>();

    public static LinkedHashMap<Integer, Float> visibilityMap = new LinkedHashMap<>();


    @SubscribeEvent
    public static void clearHUD(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        detailColor = COLOR_NULL;
        detailSearcher = EMPTY;
        detailTarget = EMPTY;
        detailPercent = 0;

        soulSight = false;
        usePlayerSenses = false;

        onPointDataMap.clear();
        visibilityMap.clear();
    }


    public static int getColor(EntityPlayer player, EntityLivingBase searcher, EntityLivingBase target, int threatLevel)
    {
        if (searcher == null) return COLOR_NULL;
        if (EntityThreatData.bypassesThreat(searcher)) return COLOR_BYPASS;
        AIDynamicStealth stealthAI = searcher instanceof EntityLiving ? AIDynamicStealth.getStealthAI((EntityLiving) searcher) : null;
        if (stealthAI != null && stealthAI.isFleeing()) return COLOR_FLEEING;
        if (serverSettings.hud.recognizePassive && EntityThreatData.isPassive(searcher)) return COLOR_PASSIVE;
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
