package com.fantasticsource.dynamicstealth.server.threat;

import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.server.GlobalDefaultsAndData;
import com.fantasticsource.mctools.MCTools;
import ladysnake.dissolution.api.corporeality.IPossessable;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityThreatData
{
    private static LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> threatBypass;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> isPassive;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> isNonPassive;


    public static void update()
    {
        threatBypass = new LinkedHashMap<>();
        isPassive = new LinkedHashMap<>();
        isNonPassive = new LinkedHashMap<>();

        MCTools.populateEntityMap(serverSettings.threat.y_entityOverrides.threatBypass, threatBypass);

        ArrayList<String> passive = new ArrayList<>();
        ArrayList<String> nonPassive = new ArrayList<>();
        for (String string : serverSettings.threat.y_entityOverrides.isPassive)
        {
            String[] tokens = string.split(",");
            if (tokens.length != 2) System.err.println("Wrong number of arguments for entity-specific passivity override; please check example in tooltip");
            else
            {
                if (tokens[1].trim().equals("true"))
                {
                    passive.add(tokens[0].trim());
                }
                else if (tokens[1].trim().equals("false"))
                {
                    nonPassive.add(tokens[0].trim());
                }
                else
                {
                    System.err.println("Second argument for entity-specific passivity override was not true or false; please check example in tooltip");
                }
            }
        }
        MCTools.populateEntityMap(passive.toArray(new String[0]), isPassive);
        MCTools.populateEntityMap(nonPassive.toArray(new String[0]), isNonPassive);
    }


    public static boolean bypassesThreat(EntityLivingBase livingBase)
    {
        if (serverSettings.threat.bypassThreatSystem || livingBase == null) return true;

        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return true;

        if (Compat.dissolution && livingBase instanceof IPossessable && ((IPossessable) livingBase).getPossessingEntity() != null) return true;

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

        return MCTools.entityMatchesMap(livingBase, threatBypass);
    }

    public static boolean isPassive(EntityLivingBase livingBase)
    {
        if (livingBase == null || bypassesThreat(livingBase)) return false;

        if (MCTools.entityMatchesMap(livingBase, isNonPassive)) return false;
        if (MCTools.entityMatchesMap(livingBase, isPassive)) return true;

        return MCTools.isPassive(livingBase);
    }
}
