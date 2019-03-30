package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
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
    public static final byte
            CID_ATTACKING_YOU = 0,
            CID_SEARCHING = 1,
            CID_ATTACKING_OTHER = 2,
            CID_IDLE = 3,
            CID_PASSIVE = 4,
            CID_FLEEING = 5,
            CID_BYPASS = 6;

    public static final int
            COLOR_ATTACKING_YOU = 0xFF0000,         //Target: yes, Threat: yes
            COLOR_ATTACKING_OTHER = 0xFFFF00,       //Target: yes, Threat: yes
            COLOR_SEARCHING = 0xFF8800,             //Target: only on server, Threat: yes
            COLOR_IDLE = 0x4444FF,                  //Target: no, Threat: no
            COLOR_PASSIVE = 0x00CC00,               //Target: no, Threat: no
            COLOR_FLEEING = 0x770077,               //Target: maybe, Threat: yes
            COLOR_BYPASS = 0x555555;                //Target: maybe, Threat: no

    public static int stealthLevel = Byte.MIN_VALUE, prevStealthLevel = Byte.MIN_VALUE;

    public static boolean soulSight = false;
    public static boolean usePlayerSenses = false;

    public static LinkedHashMap<Integer, OnPointData> opMap = new LinkedHashMap<>();
    public static OnPointData targetData = null;
    public static double targetPriority = Integer.MAX_VALUE;

    public static LinkedHashMap<Integer, Float> visibilityMap = new LinkedHashMap<>();


    @SubscribeEvent
    public static void clearClientData(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        stealthLevel = Byte.MIN_VALUE;
        prevStealthLevel = Byte.MIN_VALUE;

        soulSight = false;
        usePlayerSenses = false;

        opMap.clear();
        targetData = null;
        targetPriority = Integer.MAX_VALUE;

        visibilityMap.clear();
    }


    public static byte getCID(int color)
    {
        switch (color)
        {
            case COLOR_ATTACKING_YOU:
                return CID_ATTACKING_YOU;
            case COLOR_SEARCHING:
                return CID_SEARCHING;
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
            case CID_ATTACKING_YOU:
                return COLOR_ATTACKING_YOU;
            case CID_SEARCHING:
                return COLOR_SEARCHING;
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
        if (EntityThreatData.bypassesThreat(searcher)) return COLOR_BYPASS;
        AIDynamicStealth stealthAI = searcher instanceof EntityLiving ? AIDynamicStealth.getStealthAI((EntityLiving) searcher) : null;
        if (stealthAI != null && stealthAI.isFleeing()) return COLOR_FLEEING;
        if (serverSettings.hud.recognizePassive && EntityThreatData.isPassive(searcher)) return COLOR_PASSIVE;
        if (threatLevel <= 0) return COLOR_IDLE;
        if (target == null || !Sight.canSee(searcher, target)) return COLOR_SEARCHING;
        if (target == player) return COLOR_ATTACKING_YOU;
        return COLOR_ATTACKING_OTHER;
    }

    public static boolean canHaveClientTarget(byte cid)
    {
        return cid != CID_PASSIVE && cid != CID_IDLE && cid != CID_SEARCHING;
    }

    public static boolean canHaveClientTarget(int color)
    {
        return color != COLOR_PASSIVE && color != COLOR_IDLE && color != COLOR_SEARCHING;
    }

    public static boolean canHaveThreat(byte cid)
    {
        return cid != CID_PASSIVE && cid != CID_IDLE && cid != CID_BYPASS;
    }

    public static boolean canHaveThreat(int color)
    {
        return color != COLOR_PASSIVE && color != COLOR_IDLE && color != COLOR_BYPASS;
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
