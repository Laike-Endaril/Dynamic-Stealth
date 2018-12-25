package com.fantasticsource.dynamicstealth.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class HUD extends Gui
{
    public static String threatSearcher = "";
    public static String threatTarget = "";
    public static int threatLevel = 0;

    public HUD(Minecraft mc)
    {
        ScaledResolution sr = new ScaledResolution(mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        FontRenderer fontRender = mc.fontRenderer;

        if (threatSearcher.equals(""))
        {
            drawString(fontRender, "----------", (int) (width * 0.75), height - 30, 0x777777);
            drawString(fontRender, "----------", (int) (width * 0.75), height - 20, 0x777777);
            drawString(fontRender, "----------", (int) (width * 0.75), height - 10, 0x777777);
        }
        else
        {
            if (threatLevel == 0)
            {
                drawString(fontRender, threatSearcher, (int) (width * 0.75), height - 30, 0x4444FF);
                drawString(fontRender, "----------", (int) (width * 0.75), height - 20, 0x4444FF);
                drawString(fontRender, "----------", (int) (width * 0.75), height - 10, 0x4444FF);
            }
            else if (threatTarget.equals(""))
            {
                drawString(fontRender, threatSearcher, (int) (width * 0.75), height - 30, 0xFFAA00);
                drawString(fontRender, "----------", (int) (width * 0.75), height - 20, 0xFFAA00);
                drawString(fontRender, "" + threatLevel, (int) (width * 0.75), height - 10, 0xFFAA00);
            }
            else
            {
                drawString(fontRender, threatSearcher, (int) (width * 0.75), height - 30, 0xFF0000);
                drawString(fontRender, threatTarget, (int) (width * 0.75), height - 20, 0xFF0000);
                drawString(fontRender,"" + threatLevel, (int) (width * 0.75), height - 10, 0xFF0000);
            }
        }
    }
}
