package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class ClientData
{
    public static final byte
            CID_NULL = 0,
            CID_ATTACKING_YOU = 1,
            CID_ALERT = 2,
            CID_ATTACKING_OTHER = 3,
            CID_IDLE = 4,
            CID_PASSIVE = 5,
            CID_FLEEING = 6,
            CID_BYPASS = 7;

    public static final int
            COLOR_NULL = 0x777777,
            COLOR_ATTACKING_YOU = 0xFF0000,
            COLOR_ALERT = 0xFF8800,
            COLOR_ATTACKING_OTHER = 0xFFFF00,
            COLOR_IDLE = 0x4444FF,
            COLOR_PASSIVE = 0x00CC00,
            COLOR_FLEEING = 0x770077,
            COLOR_BYPASS = 0x555555;

    public static final String UNKNOWN = "???";

    public static boolean soulSight = false;
    public static boolean usePlayerSenses = false;

    public static ArrayList<OnPointData> opList = new ArrayList<>();
    public static LinkedHashMap<Integer, OnPointData> opMap = new LinkedHashMap<>();
    public static OnPointData detailData = null;

    public static LinkedHashMap<Integer, Float> visibilityMap = new LinkedHashMap<>();


    @SubscribeEvent
    public static void clearClientData(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        soulSight = false;
        usePlayerSenses = false;

        opList.clear();
        opMap.clear();
        detailData = null;

        visibilityMap.clear();
    }


    public static byte getCID(int color)
    {
        switch (color)
        {
            case COLOR_NULL:
                return CID_NULL;
            case COLOR_ATTACKING_YOU:
                return CID_ATTACKING_YOU;
            case COLOR_ALERT:
                return CID_ALERT;
            case COLOR_ATTACKING_OTHER:
                return CID_ATTACKING_OTHER;
            case COLOR_IDLE:
                return CID_IDLE;
            case COLOR_PASSIVE:
                return CID_PASSIVE;
            case COLOR_FLEEING:
                return CID_FLEEING;
            case COLOR_BYPASS:
                return CID_BYPASS;
        }
        throw new IllegalArgumentException("Unregistered color: " + color);
    }

    public static byte getCID(EntityPlayer player, EntityLivingBase searcher, EntityLivingBase target, int threatLevel)
    {
        return getCID(getColor(player, searcher, target, threatLevel));
    }

    public static int getColor(byte cid)
    {
        switch (cid)
        {
            case CID_NULL:
                return COLOR_NULL;
            case CID_ATTACKING_YOU:
                return COLOR_ATTACKING_YOU;
            case CID_ALERT:
                return COLOR_ALERT;
            case CID_ATTACKING_OTHER:
                return COLOR_ATTACKING_OTHER;
            case CID_IDLE:
                return COLOR_IDLE;
            case CID_PASSIVE:
                return COLOR_PASSIVE;
            case CID_FLEEING:
                return COLOR_FLEEING;
            case CID_BYPASS:
                return COLOR_BYPASS;
        }
        throw new IllegalArgumentException("Unregistered cid: " + cid);
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
        public int color, searcherID, targetID, percent;

        public OnPointData(int color, int searcherID, int targetID, int percent)
        {
            this.color = color;
            this.searcherID = searcherID;
            this.targetID = targetID;
            this.percent = percent;
        }
    }
}
