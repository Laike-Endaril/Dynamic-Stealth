package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.DynamicStealthConfig;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

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

    public static int color = COLOR_NULL;
    public static String threatSearcher = EMPTY;
    public static String threatTarget = EMPTY;
    public static int threatLevel = 0;

    public HUD(Minecraft mc)
    {
        ScaledResolution sr = new ScaledResolution(mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        FontRenderer fontRender = mc.fontRenderer;

        if (DynamicStealthConfig.clientSettings.threat.displayHUD)
        {
            if (threatSearcher.equals(EMPTY))
            {
                drawString(fontRender, EMPTY, (int) (width * 0.75), height - 30, color);
                drawString(fontRender, EMPTY, (int) (width * 0.75), height - 20, color);
                drawString(fontRender, EMPTY, (int) (width * 0.75), height - 10, color);
            }
            else
            {
                if (threatLevel == 0)
                {
                    drawString(fontRender, threatSearcher, (int) (width * 0.75), height - 30, color);
                    drawString(fontRender, EMPTY, (int) (width * 0.75), height - 20, color);
                    drawString(fontRender, EMPTY, (int) (width * 0.75), height - 10, color);
                }
                else if (threatTarget.equals(EMPTY))
                {
                    drawString(fontRender, threatSearcher, (int) (width * 0.75), height - 30, color);
                    drawString(fontRender, EMPTY, (int) (width * 0.75), height - 20, color);
                    drawString(fontRender, "" + threatLevel, (int) (width * 0.75), height - 10, color);
                }
                else if (threatLevel == -1)
                {
                    drawString(fontRender, threatSearcher, (int) (width * 0.75), height - 30, COLOR_ALERT);
                    drawString(fontRender, UNKNOWN, (int) (width * 0.75), height - 20, COLOR_ALERT);
                    drawString(fontRender, UNKNOWN, (int) (width * 0.75), height - 10, COLOR_ALERT);
                }
                else
                {
                    drawString(fontRender, threatSearcher, (int) (width * 0.75), height - 30, color);
                    drawString(fontRender, threatTarget, (int) (width * 0.75), height - 20, color);
                    drawString(fontRender, "" + threatLevel, (int) (width * 0.75), height - 10, color);
                }
            }

            GL11.glColor4f(1, 1, 1, 1);
        }
    }


    public static int getColor(EntityPlayer player, EntityLiving searcher, EntityLivingBase target, int threatLevel)
    {
        if (searcher == null) return COLOR_NULL;
        if (DynamicStealthConfig.serverSettings.threat.recognizePassive && MCTools.isPassive(searcher)) return COLOR_PASSIVE;
        if (threatLevel <= 0) return COLOR_IDLE;
        if (target == null) return COLOR_ALERT;
        if (target == player) return COLOR_ATTACKING_YOU;
        return COLOR_ATTACKING_OTHER;
    }
}
