package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.common.Network;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class HUD extends Gui
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

    public static ArrayList<Network.OnPointData> onPointDataList;

    public HUD(Minecraft mc)
    {
        ScaledResolution sr = new ScaledResolution(mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        FontRenderer fontRender = mc.fontRenderer;

        if (DynamicStealthConfig.clientSettings.threat.displayHUD)
        {
            if (detailSearcher.equals(EMPTY))
            {
                drawString(fontRender, EMPTY, (int) (width * 0.75), height - 30, detailColor);
                drawString(fontRender, EMPTY, (int) (width * 0.75), height - 20, detailColor);
                drawString(fontRender, EMPTY, (int) (width * 0.75), height - 10, detailColor);
            }
            else
            {
                if (detailPercent == 0)
                {
                    drawString(fontRender, detailSearcher, (int) (width * 0.75), height - 30, detailColor);
                    drawString(fontRender, EMPTY, (int) (width * 0.75), height - 20, detailColor);
                    drawString(fontRender, EMPTY, (int) (width * 0.75), height - 10, detailColor);
                }
                else if (detailTarget.equals(EMPTY))
                {
                    drawString(fontRender, detailSearcher, (int) (width * 0.75), height - 30, detailColor);
                    drawString(fontRender, EMPTY, (int) (width * 0.75), height - 20, detailColor);
                    drawString(fontRender, detailPercent + "%", (int) (width * 0.75), height - 10, detailColor);
                }
                else if (detailPercent == -1) //Special code for threat bypass mode
                {
                    drawString(fontRender, detailSearcher, (int) (width * 0.75), height - 30, COLOR_ALERT);
                    drawString(fontRender, UNKNOWN, (int) (width * 0.75), height - 20, COLOR_ALERT);
                    drawString(fontRender, UNKNOWN, (int) (width * 0.75), height - 10, COLOR_ALERT);
                }
                else
                {
                    drawString(fontRender, detailSearcher, (int) (width * 0.75), height - 30, detailColor);
                    drawString(fontRender, detailTarget, (int) (width * 0.75), height - 20, detailColor);
                    drawString(fontRender, detailPercent + "%", (int) (width * 0.75), height - 10, detailColor);
                }
            }

            GL11.glColor4f(1, 1, 1, 1);
        }
    }

    @SubscribeEvent
    public static void clearHUD(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        detailColor = COLOR_NULL;
        detailSearcher = EMPTY;
        detailTarget = EMPTY;
        detailPercent = 0;

        onPointDataList.clear();
    }

    public static int getColor(EntityPlayer player, EntityLivingBase searcher, EntityLivingBase target, int threatLevel)
    {
        if (searcher == null) return COLOR_NULL;
        if (DynamicStealthConfig.serverSettings.threat.recognizePassive && MCTools.isPassive(searcher)) return COLOR_PASSIVE;
        if (threatLevel <= 0) return COLOR_IDLE;
        if (target == null) return COLOR_ALERT;
        if (target == player) return COLOR_ATTACKING_YOU;
        return COLOR_ATTACKING_OTHER;
    }
}
