package com.fantasticsource.dynamicstealth.server.threat;

import com.fantasticsource.dynamicstealth.server.configdata.EntityThreatDefaults;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;

public class EntityThreatData
{
    private static ArrayList<Class<? extends Entity>> threatBypass = new ArrayList<>();
    private static ArrayList<Class<? extends Entity>> isPassive = new ArrayList<>();
    private static ArrayList<Class<? extends Entity>> isNonPassive = new ArrayList<>();
    private static ArrayList<Class<? extends Entity>> isFearless = new ArrayList<>();


    static
    {
        EntityEntry entry;
        String[] tokens;
        String token;
        int mode;

        for (String string : serverSettings.threat.y_entityOverrides.fearless)
        {
            if (string.equals("player")) isFearless.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));

                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else isFearless.add(entry.getEntityClass());
            }
        }

        for (String string : serverSettings.threat.y_entityOverrides.threatBypass)
        {
            if (string.equals("player")) threatBypass.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));

                if (entry == null)
                {
                    if (!EntityThreatDefaults.threatBypassDefaults.contains(string)) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                }
                else
                {
                    threatBypass.add(entry.getEntityClass());
                }
            }
        }

        for (String string : serverSettings.threat.y_entityOverrides.isPassive)
        {
            tokens = string.split(",");
            if (tokens.length != 2) System.err.println("Wrong number of arguments for entity-specific passivity override; please check example in tooltip");
            else
            {
                mode = 0;
                if (tokens[1].trim().equals("true")) mode = 1;
                else if (tokens[1].trim().equals("false")) mode = 2;
                else System.err.println("Second argument for entity-specific passivity override was not true or false; please check example in tooltip");

                if (mode > 0)
                {
                    token = tokens[0].trim();
                    if (token.equals("player"))
                    {
                        if (mode == 1) isPassive.add(EntityPlayerMP.class);
                        else isNonPassive.add(EntityPlayerMP.class);
                    }
                    else
                    {
                        entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(token));
                        if (entry == null)
                        {
                            if (!EntityThreatDefaults.passiveDefaults.contains(string)) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                        }
                        else
                        {
                            if (mode == 1) isPassive.add(entry.getEntityClass());
                            else isNonPassive.add(entry.getEntityClass());
                        }
                    }
                }
            }
        }
    }


    public static boolean bypassesThreat(EntityLivingBase livingBase)
    {
        if (serverSettings.threat.bypassThreatSystem || livingBase == null) return true;

        for (Class<? extends Entity> clss : threatBypass)
        {
            if (clss.isAssignableFrom(livingBase.getClass())) return true;
        }

        return false;
    }

    public static boolean isPassive(EntityLivingBase livingBase)
    {
        if (livingBase == null || bypassesThreat(livingBase)) return false;

        for (Class<? extends Entity> clss : isPassive)
        {
            if (clss.isAssignableFrom(livingBase.getClass())) return true;
        }

        for (Class<? extends Entity> clss : isNonPassive)
        {
            if (clss.isAssignableFrom(livingBase.getClass())) return false;
        }

        return MCTools.isPassive(livingBase);
    }


    public static boolean isAfraid(EntityLivingBase livingBase)
    {
        for (Class<? extends Entity> clss : isFearless)
        {
            if (clss.isAssignableFrom(livingBase.getClass())) return false;
        }

        return Threat.getThreat(livingBase) > 0 && (isPassive(livingBase) || livingBase.getHealth() / livingBase.getMaxHealth() < 0.25);
    }
}
