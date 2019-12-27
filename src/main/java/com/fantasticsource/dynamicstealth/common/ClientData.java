package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class ClientData
{
    public static final byte
            CID_ATTACKING_YOU = 0,
            CID_SEARCHING = 1,
            CID_ATTACKING_OTHER = 2,
            CID_IDLE_NON_PASSIVE = 3,
            CID_IDLE_PASSIVE = 4,
            CID_FLEEING_NON_PASSIVE = 5,
            CID_FLEEING_PASSIVE = 6,
            CID_BYPASS = 7,
            CID_CONFUSED = 8;

    public static final int
            COLOR_ATTACKING_YOU = 0xFF0000,         //Target: yes, Threat: yes, Color: Red
            COLOR_ATTACKING_OTHER = 0xFFFF00,       //Target: yes, Threat: yes, Color: Yellow
            COLOR_SEARCHING = 0xFF8800,             //Target: only on server, Threat: yes, Color: Orange
            COLOR_IDLE_NON_PASSIVE = 0x4444FF,      //Target: no, Threat: no, Color: Blue
            COLOR_IDLE_PASSIVE = 0x00CC00,          //Target: no, Threat: no, Color: Dark Green
            COLOR_FLEEING_N0N_PASSIVE = 0xFF55FF,   //Target: maybe, Threat: yes, Color: Light Purple
            COLOR_FLEEING_PASSIVE = 0xAA00AA,       //Target: maybe, Threat: yes, Color: Dark Purple
            COLOR_BYPASS = 0x555555,                //Target: maybe, Threat: no, Color: Dark Gray
            COLOR_DAZED = 0x55FF55;                 //Target: maybe, Threat: maybe, Color: Light Green

    public static int stealthLevel = Byte.MIN_VALUE, prevStealthLevel = Byte.MIN_VALUE, lightLevel = 0;

    public static boolean soulSight = false;
    public static boolean usePlayerSenses = false;
    public static boolean allowTargetingName = true, allowTargetingHP = true, allowTargetingThreat = true, allowTargetingDistance = true;

    public static LinkedHashMap<Integer, OnPointData> opMap = new LinkedHashMap<>();
    public static OnPointData targetData = null;
    public static double targetPriority = Integer.MAX_VALUE;

    public static LinkedHashMap<Integer, Float> visibilityMap = new LinkedHashMap<>(), previousVisibilityMap1 = new LinkedHashMap<>(), previousVisibilityMap2 = new LinkedHashMap<>();


    @SubscribeEvent
    public static void clearClientData(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        stealthLevel = Byte.MIN_VALUE;
        prevStealthLevel = Byte.MIN_VALUE;

        soulSight = false;
        usePlayerSenses = false;
        allowTargetingName = true;
        allowTargetingHP = true;
        allowTargetingThreat = true;
        allowTargetingDistance = true;

        opMap.clear();
        targetData = null;
        targetPriority = Integer.MAX_VALUE;

        visibilityMap.clear();
        previousVisibilityMap1.clear();
        previousVisibilityMap2.clear();
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
            case COLOR_IDLE_NON_PASSIVE:
                return CID_IDLE_NON_PASSIVE;
            case COLOR_IDLE_PASSIVE:
                return CID_IDLE_PASSIVE;
            case COLOR_FLEEING_N0N_PASSIVE:
                return CID_FLEEING_NON_PASSIVE;
            case COLOR_FLEEING_PASSIVE:
                return CID_FLEEING_PASSIVE;
            case COLOR_BYPASS:
                return CID_BYPASS;
            case COLOR_DAZED:
                return CID_CONFUSED;
        }
        throw new IllegalArgumentException("Unregistered color: " + color);
    }

    public static byte getCID(EntityPlayer player, EntityLivingBase searcher, EntityLivingBase target, float threatPercentage)
    {
        return getCID(getColor(player, searcher, target, threatPercentage));
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
            case CID_IDLE_NON_PASSIVE:
                return COLOR_IDLE_NON_PASSIVE;
            case CID_IDLE_PASSIVE:
                return COLOR_IDLE_PASSIVE;
            case CID_FLEEING_NON_PASSIVE:
                return COLOR_FLEEING_N0N_PASSIVE;
            case CID_FLEEING_PASSIVE:
                return COLOR_FLEEING_PASSIVE;
            case CID_BYPASS:
                return COLOR_BYPASS;
            case CID_CONFUSED:
                return COLOR_DAZED;
        }
        throw new IllegalArgumentException("Unregistered cid: " + cid);
    }

    public static int getColor(EntityPlayer player, EntityLivingBase searcher, EntityLivingBase target, float threatPercentage)
    {
        if (EntityThreatData.bypassesThreat(searcher)) return COLOR_BYPASS;

        Potion mindTrickPotion = ForgeRegistries.POTIONS.getValue(new ResourceLocation("ebwizardry", "mind_trick"));
        if (mindTrickPotion != null && searcher.getActivePotionEffect(mindTrickPotion) != null) return COLOR_DAZED;

        AIDynamicStealth stealthAI = searcher instanceof EntityLiving ? AIDynamicStealth.getStealthAI((EntityLiving) searcher) : null;
        if (stealthAI != null && stealthAI.isFleeing())
        {
            return (serverSettings.hud.recognizePassive && EntityThreatData.isPassive(searcher)) ? COLOR_FLEEING_PASSIVE : COLOR_FLEEING_N0N_PASSIVE;
        }
        if (serverSettings.hud.recognizePassive && EntityThreatData.isPassive(searcher)) return COLOR_IDLE_PASSIVE;
        if (threatPercentage <= 0) return COLOR_IDLE_NON_PASSIVE;
        if (target == null || !Sight.canSee(searcher, target, true)) return COLOR_SEARCHING;
        if (target == player) return COLOR_ATTACKING_YOU;
        return COLOR_ATTACKING_OTHER;
    }

    public static boolean canHaveClientTarget(byte cid)
    {
        return cid != CID_IDLE_PASSIVE && cid != CID_IDLE_NON_PASSIVE && cid != CID_SEARCHING;
    }

    public static boolean canHaveClientTarget(int color)
    {
        return color != COLOR_IDLE_PASSIVE && color != COLOR_IDLE_NON_PASSIVE && color != COLOR_SEARCHING;
    }

    public static boolean canHaveThreat(byte cid)
    {
        return cid != CID_IDLE_PASSIVE && cid != CID_IDLE_NON_PASSIVE && cid != CID_BYPASS;
    }

    public static boolean canHaveThreat(int color)
    {
        return color != COLOR_IDLE_PASSIVE && color != COLOR_IDLE_NON_PASSIVE && color != COLOR_BYPASS;
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

        public OnPointData clone()
        {
            return new OnPointData(color, searcherID, targetID, percent);
        }

        public Entity getEntity()
        {
            return Minecraft.getMinecraft().world.getEntityByID(searcherID);
        }

        public Entity getTarget()
        {
            return Minecraft.getMinecraft().world.getEntityByID(targetID);
        }
    }
}
