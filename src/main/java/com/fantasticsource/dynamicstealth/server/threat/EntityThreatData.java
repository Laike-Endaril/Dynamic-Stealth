package com.fantasticsource.dynamicstealth.server.threat;

import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.server.GlobalDefaultsAndData;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.Pair;
import ladysnake.dissolution.api.corporeality.IPossessable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;

import java.util.HashSet;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityThreatData
{
    private static HashSet<Class<? extends EntityLivingBase>> threatBypass;
    private static HashSet<Pair<Class<? extends EntityLivingBase>, String>> threatBypassNamed;
    private static HashSet<Class<? extends EntityLivingBase>> isPassive;
    private static HashSet<Pair<Class<? extends EntityLivingBase>, String>> isPassiveNamed;
    private static HashSet<Class<? extends EntityLivingBase>> isNonPassive;
    private static HashSet<Pair<Class<? extends EntityLivingBase>, String>> isNonPassiveNamed;


    public static void update()
    {
        threatBypass = new HashSet<>();
        threatBypassNamed = new HashSet<>();
        isPassive = new HashSet<>();
        isPassiveNamed = new HashSet<>();
        isNonPassive = new HashSet<>();
        isNonPassiveNamed = new HashSet<>();

        EntityEntry entry;
        String[] tokens;
        String token;
        int mode;

        for (String string : serverSettings.threat.y_entityOverrides.threatBypass)
        {
            if (string.equals("player")) threatBypass.add(EntityPlayerMP.class);
            else if (string.indexOf(":") != string.lastIndexOf(":"))
            {
                String[] tokens2 = string.split(":");
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens2[0], tokens2[1]));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else threatBypassNamed.add(new Pair<>((Class<? extends EntityLivingBase>) entry.getEntityClass(), tokens2[2]));
            }
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));

                if (entry == null)
                {
                    if (!EntityThreatDefaults.threatBypassDefaults.contains(string)) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                }
                else
                {
                    Class c = entry.getEntityClass();
                    if (EntityLivingBase.class.isAssignableFrom(c)) threatBypass.add(c);
                    else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
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
                    else if (token.indexOf(":") != token.lastIndexOf(":"))
                    {
                        String[] tokens2 = token.split(":");
                        entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens2[0], tokens2[1]));
                        if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                        else
                        {
                            if (mode == 1) isPassiveNamed.add(new Pair<>((Class<? extends EntityLivingBase>) entry.getEntityClass(), tokens2[2]));
                            else isNonPassiveNamed.add(new Pair<>((Class<? extends EntityLivingBase>) entry.getEntityClass(), tokens2[2]));
                        }
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
                            Class c = entry.getEntityClass();
                            if (EntityLivingBase.class.isAssignableFrom(c))
                            {
                                if (mode == 1) isPassive.add(c);
                                else isNonPassive.add(c);
                            }
                            else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
                        }
                    }
                }
            }
        }
    }


    public static boolean bypassesThreat(EntityLivingBase livingBase)
    {
        if (serverSettings.threat.bypassThreatSystem || livingBase == null) return true;

        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return true;

        if (Compat.dissolution && livingBase instanceof IPossessable && ((IPossessable) livingBase).getPossessingEntity() != null) return true;

        for (Pair pair : threatBypassNamed)
        {
            if (pair.getKey().equals(livingBase.getClass()) && pair.getValue().equals(livingBase.getName())) return true;
        }

        if (Compat.customnpcs)
        {
            IEntity iEntity = NpcAPI.Instance().getIEntity(livingBase);
            if (iEntity instanceof ICustomNpc)
            {
                ICustomNpc npc = (ICustomNpc) iEntity;
                String faction = npc.getFaction().getName();
                for (String string : serverSettings.threat.cnpcThreatConfig.threatBypassFactions)
                {
                    if (faction.equals(string)) return true;
                }
            }
        }

        return threatBypass.contains(livingBase.getClass());
    }

    public static boolean isPassive(EntityLivingBase livingBase)
    {
        if (livingBase == null || bypassesThreat(livingBase)) return false;

        Class cl = livingBase.getClass();

        for (Pair pair : isNonPassiveNamed)
        {
            if (pair.getKey().equals(livingBase.getClass()) && pair.getValue().equals(livingBase.getName())) return false;
        }
        for (Pair pair : isPassiveNamed)
        {
            if (pair.getKey().equals(livingBase.getClass()) && pair.getValue().equals(livingBase.getName())) return true;
        }

        if (isNonPassive.contains(cl)) return false;
        if (isPassive.contains(cl)) return true;

        return MCTools.isPassive(livingBase);
    }
}
