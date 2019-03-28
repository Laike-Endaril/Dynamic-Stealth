package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.client.layeredits.LayerEndermanEyesEdit;
import com.fantasticsource.dynamicstealth.client.layeredits.LayerSpiderEyesEdit;
import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.compat.Compat;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.clientSettings;

@SideOnly(Side.CLIENT)
public class RenderAlterer
{
    private static ArrayList<ScorePlayerTeam> colorTeams = new ArrayList<>();

    private static Scoreboard scoreboard;
    private static ArrayList<EntityLivingBase> glowCache = new ArrayList<>();
    private static LinkedHashMap<EntityLivingBase, Team> teamCache = new LinkedHashMap<>();

    private static boolean ready = false;

    @SubscribeEvent
    public static void init(EntityJoinWorldEvent event)
    {
        if (!ready && event.getEntity() == Minecraft.getMinecraft().player)
        {
            scoreboard = Minecraft.getMinecraft().world.getScoreboard();

            ScorePlayerTeam team = scoreboard.createTeam("green");
            team.setPrefix(TextFormatting.GREEN.toString());
            colorTeams.add(team);
            team = scoreboard.createTeam("blue");
            team.setPrefix(TextFormatting.BLUE.toString());
            colorTeams.add(team);
            team = scoreboard.createTeam("yellow");
            team.setPrefix(TextFormatting.YELLOW.toString());
            colorTeams.add(team);
            team = scoreboard.createTeam("orange");
            team.setPrefix(TextFormatting.GOLD.toString());
            colorTeams.add(team);
            team = scoreboard.createTeam("red");
            team.setPrefix(TextFormatting.RED.toString());
            colorTeams.add(team);
            team = scoreboard.createTeam("black");
            team.setPrefix(TextFormatting.BLACK.toString());
            colorTeams.add(team);
            team = scoreboard.createTeam("purple");
            team.setPrefix(TextFormatting.DARK_PURPLE.toString());
            colorTeams.add(team);

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
            case ClientData.COLOR_SEARCHING_FOR_UNSEEN:
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

            for (Object object : teamCache.entrySet().toArray())
            {
                Map.Entry<EntityLivingBase, ScorePlayerTeam> entry = (Map.Entry<EntityLivingBase, ScorePlayerTeam>) object;
                EntityLivingBase livingBase = entry.getKey();
                Team team = entry.getKey().getTeam();

                if (team != null) scoreboard.addPlayerToTeam(livingBase.getUniqueID().toString(), team.getName());

                teamCache.remove(livingBase);
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
            if (clientSettings.hudSettings.targetingStyle.glow && clientSettings.hudSettings.targetingStyle.stateColoredGlow)
            {
                ClientData.OnPointData data = ClientData.targetData;
                if (data != null && data.searcherID == livingBase.getEntityId())
                {
                    Team team = livingBase.getTeam();
                    if (team != null) teamCache.put(livingBase, team);
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
            if (colorTeams.contains(team))
            {
                scoreboard.removePlayerFromTeam(livingBase.getCachedUniqueIdString(), (ScorePlayerTeam) team);
                if (teamCache.containsKey(livingBase))
                {
                    scoreboard.addPlayerToTeam(livingBase.getCachedUniqueIdString(), teamCache.get(livingBase).getName());
                }
                livingBase.setGlowing(false);
            }

            //Focused target and soul sight glowing effects
            ClientData.OnPointData data = ClientData.targetData;
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
        if (render instanceof RenderLivingBase)
        {
            List<LayerRenderer> list = ((RenderLivingBase) render).layerRenderers;
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
    }
}
