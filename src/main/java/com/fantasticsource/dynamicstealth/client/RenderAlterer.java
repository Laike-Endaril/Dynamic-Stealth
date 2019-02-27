package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class RenderAlterer
{
    private static ArrayList<EntityLivingBase> soulSightCache = new ArrayList<>();
    private static Scoreboard scoreboard = Minecraft.getMinecraft().world.getScoreboard();

    static
    {
        scoreboard.createTeam("green").setPrefix(TextFormatting.GREEN.toString());
        scoreboard.createTeam("blue").setPrefix(TextFormatting.BLUE.toString());
        scoreboard.createTeam("yellow").setPrefix(TextFormatting.YELLOW.toString());
        scoreboard.createTeam("orange").setPrefix(TextFormatting.GOLD.toString());
        scoreboard.createTeam("red").setPrefix(TextFormatting.RED.toString());
        scoreboard.createTeam("black").setPrefix(TextFormatting.BLACK.toString());
        scoreboard.createTeam("purple").setPrefix(TextFormatting.DARK_PURPLE.toString());
    }


    private static String getTeam(int color)
    {
        switch (color)
        {
            case ClientData.COLOR_PASSIVE:
                return "green";
            case ClientData.COLOR_IDLE:
                return "blue";
            case ClientData.COLOR_ATTACKING_OTHER:
                return "yellow";
            case ClientData.COLOR_ALERT:
                return "orange";
            case ClientData.COLOR_ATTACKING_YOU:
                return "red";
            case ClientData.COLOR_BYPASS:
                return "black";
            case ClientData.COLOR_FLEEING:
                return "purple";
        }
        return null;
    }


    @SubscribeEvent
    public static void preRender(RenderLivingEvent.Pre event)
    {
        EntityLivingBase livingBase = event.getEntity();


        //Don't draw seen entities as invisible, because they've been SEEN
        if (Compat.statues && livingBase.getClass().getName().contains("party.lemons.statue")) return;
        livingBase.setInvisible(false);


        //Soul sight glow effect
        if (soulSightCache.contains(livingBase))
        {
            soulSightCache.remove(livingBase);
            livingBase.setGlowing(false);
        }

        //Focused target glow effect
        ClientData.OnPointData data = ClientData.detailData;
        if (data != null && data.searcherID == livingBase.getEntityId())
        {
            scoreboard.addPlayerToTeam(livingBase.getUniqueID().toString(), getTeam(data.color));
        }


        //Entity opacity based on visibility
        if (ClientData.usePlayerSenses && livingBase != Minecraft.getMinecraft().player)
        {
            int id = livingBase.getEntityId();
            double min = DynamicStealthConfig.clientSettings.entityFading.mobOpacityMin;
            double visibility = ClientData.visibilityMap.containsKey(id) ? ClientData.visibilityMap.get(id) : 1;
            double maxOpacityAt = DynamicStealthConfig.clientSettings.entityFading.fullOpacityAt;
            if (visibility != 0)
            {
                if (maxOpacityAt == 0) visibility = 1;
                else visibility /= maxOpacityAt;
            }

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            GlStateManager.enableCull();
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);

            GlStateManager.color(1, 1, 1, 1);
            GL11.glColor4f(1, 1, 1, (float) (min + (1d - min) * visibility));
        }
    }

    @SubscribeEvent
    public static void postRender(RenderLivingEvent.Post event)
    {
        EntityLivingBase livingBase = event.getEntity();


        //Focused target glowing effect
        Team team = livingBase.getTeam();
        if (team != null)
        {
            scoreboard.removePlayerFromTeam(livingBase.getCachedUniqueIdString(), (ScorePlayerTeam) team);
            livingBase.setGlowing(false);
        }

        //Focused target and soul sight glowing effects
        ClientData.OnPointData data = ClientData.detailData;
        if (data != null && data.searcherID == livingBase.getEntityId())
        {
            livingBase.setGlowing(true);
            soulSightCache.add(livingBase);
        }
        else if (ClientData.soulSight && !livingBase.isGlowing())
        {
            livingBase.setGlowing(true);
            soulSightCache.add(livingBase);
        }


        if (Compat.statues && livingBase.getClass().getName().contains("party.lemons.statue")) return;
        GlStateManager.color(1, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.disableBlend();
    }
}
