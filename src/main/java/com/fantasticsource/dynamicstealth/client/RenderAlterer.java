package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.client.layeredits.LayerEndermanEyesEdit;
import com.fantasticsource.dynamicstealth.client.layeredits.LayerSpiderEyesEdit;
import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.client.renderer.entity.layers.LayerEndermanEyes;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.clientSettings;

@SideOnly(Side.CLIENT)
public class RenderAlterer
{
    private static Field renderLivingBaseLayerRenderersField;

    private static Scoreboard scoreboard;
    private static ArrayList<EntityLivingBase> glowCache = new ArrayList<>();

    private static boolean ready = false;

    @SubscribeEvent
    public static void init(EntityJoinWorldEvent event)
    {
        if (!ready && event.getEntity() == Minecraft.getMinecraft().player)
        {
            scoreboard = Minecraft.getMinecraft().world.getScoreboard();

            scoreboard.createTeam("green").setPrefix(TextFormatting.GREEN.toString());
            scoreboard.createTeam("blue").setPrefix(TextFormatting.BLUE.toString());
            scoreboard.createTeam("yellow").setPrefix(TextFormatting.YELLOW.toString());
            scoreboard.createTeam("orange").setPrefix(TextFormatting.GOLD.toString());
            scoreboard.createTeam("red").setPrefix(TextFormatting.RED.toString());
            scoreboard.createTeam("black").setPrefix(TextFormatting.BLACK.toString());
            scoreboard.createTeam("purple").setPrefix(TextFormatting.DARK_PURPLE.toString());

            try
            {
                renderLivingBaseLayerRenderersField = ReflectionTool.getField(RenderLivingBase.class, "field_177097_h", "layerRenderers");
            }
            catch (NoSuchFieldException | IllegalAccessException e)
            {
                MCTools.crash(e, 156, true);
            }

            ready = true;
        }
    }

    @SubscribeEvent
    public static void reset(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        ready = false;
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
    public static void tick(TickEvent.WorldTickEvent event)
    {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END)
        {
            for (EntityLivingBase livingBase : (ArrayList<EntityLivingBase>) glowCache.clone())
            {
                livingBase.setGlowing(false);
                glowCache.remove(livingBase);
            }
        }
    }

    @SubscribeEvent
    public static void preRender(RenderLivingEvent.Pre event)
    {
        if (ready)
        {
            EntityLivingBase livingBase = event.getEntity();
            //Hard stops
            if (Compat.statues && livingBase.getClass().getName().contains("party.lemons.statue")) return;


            //Don't draw seen entities as invisible, because they've been SEEN
            livingBase.setInvisible(false);


            //Remove glow effect if cached
            if (glowCache.contains(livingBase))
            {
                glowCache.remove(livingBase);
                livingBase.setGlowing(false);
            }


            //Focused target glow effect
            if (clientSettings.hudSettings.targetingStyle.glow && clientSettings.hudSettings.targetingStyle.colorGlow)
            {
                ClientData.OnPointData data = ClientData.detailData;
                if (data != null && data.searcherID == livingBase.getEntityId())
                {
                    scoreboard.addPlayerToTeam(livingBase.getUniqueID().toString(), getTeam(data.color));
                }
            }


            //Entity opacity based on visibility
            if (ClientData.usePlayerSenses && livingBase != Minecraft.getMinecraft().player)
            {
                int id = livingBase.getEntityId();
                double min = clientSettings.entityFading.mobOpacityMin;
                double visibility = ClientData.visibilityMap.containsKey(id) ? ClientData.visibilityMap.get(id) : 1;
                double maxOpacityAt = clientSettings.entityFading.fullOpacityAt;
                if (visibility != 0)
                {
                    if (maxOpacityAt == 0) visibility = 1;
                    else visibility /= maxOpacityAt;
                }

                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                GlStateManager.enableCull();
                GlStateManager.cullFace(GlStateManager.CullFace.BACK);


                GlStateManager.Color c = GlStateManager.colorState;
                c.alpha = (float) (min + (1d - min) * visibility);
                GL11.glColor4f(c.red, c.green, c.blue, c.alpha);
            }
        }
    }

    @SubscribeEvent
    public static void postRender(RenderLivingEvent.Post event)
    {
        if (ready)
        {
            EntityLivingBase livingBase = event.getEntity();
            //Hard stops
            if (Compat.statues && livingBase.getClass().getName().contains("party.lemons.statue")) return;


            //Focused target glowing effect
            Team team = livingBase.getTeam();
            if (team != null)
            {
                scoreboard.removePlayerFromTeam(livingBase.getCachedUniqueIdString(), (ScorePlayerTeam) team);
                livingBase.setGlowing(false);
            }

            //Focused target and soul sight glowing effects
            ClientData.OnPointData data = ClientData.detailData;
            if (clientSettings.hudSettings.targetingStyle.glow && data != null && data.searcherID == livingBase.getEntityId())
            {
                setTempGlow(event);
            }
            else if (ClientData.soulSight && !livingBase.isGlowing())
            {
                setTempGlow(event);
            }


            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableBlend();
        }
    }

    private static void setTempGlow(RenderLivingEvent.Post event)
    {
        EntityLivingBase entity = event.getEntity();
        entity.setGlowing(true);
        glowCache.add(entity);
    }


    public static void replaceLayers(EntityLivingBase livingBase)
    {
        Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(livingBase);
        try
        {
            List<LayerRenderer> list = (List<LayerRenderer>) renderLivingBaseLayerRenderersField.get(render);
            for (LayerRenderer layer : list.toArray(new LayerRenderer[list.size()]))
            {
                if (layer instanceof LayerSpiderEyes)
                {
                    list.remove(layer);
                    list.add(new LayerSpiderEyesEdit((RenderSpider) render));
                }
                else if (layer instanceof LayerEndermanEyes)
                {
                    list.remove(layer);
                    list.add(new LayerEndermanEyesEdit((RenderEnderman) render));
                }
            }
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 157, false);
        }
    }
}
