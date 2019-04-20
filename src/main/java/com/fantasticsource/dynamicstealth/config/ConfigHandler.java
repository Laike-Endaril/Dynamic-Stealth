package com.fantasticsource.dynamicstealth.config;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.mctools.MCTools;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ConfigHandler
{
    public static final String CONFIG_NAME = DynamicStealth.MODID + "/" + DynamicStealth.CONFIG_VERSION + "+";

    private static String configDir = Loader.instance().getConfigDir().getAbsolutePath() + File.separator;
    private static String dsDir = configDir + DynamicStealth.MODID + File.separator;
    private static File currentFile = new File(dsDir + DynamicStealth.CONFIG_VERSION + "+.cfg");
    public static String fullConfigFilename = currentFile.getAbsolutePath();
    private static File mostRecentFile;
    private static File logFile;
    private static BufferedWriter logWriter;
    private static boolean currentAlreadyExists = false;

    public static void init()
    {
        File file = new File(MCTools.getConfigDir() + "dynamicstealth.cfg");
        if (file.exists()) file.renameTo(new File(MCTools.getConfigDir() + "dynamicstealth/dynamicstealth (old).cfg"));

        mostRecentFile = mostRecent();
        currentAlreadyExists = mostRecentFile.exists();
    }

    private static File mostRecent()
    {
        if (currentFile.exists()) return currentFile;

        File[] files = new File(dsDir).listFiles();
        File result = null;
        if (files != null && files.length > 0)
        {
            int resultSubver = -1;
            for (File file : files)
            {
                String ver = getVersion(file);
                if (ver == null) continue;

                String mcVer = getMCVer(ver);
                if (mcVer == null || !mcVer.equals(getMCVer(getVersion(currentFile)))) continue;

                ver = subVer(ver);
                if (ver == null || !mcVer.equals(getMCVer(getVersion(currentFile)))) continue;

                int subver = Integer.parseInt(ver);

                if (subver > resultSubver)
                {
                    result = file;
                    resultSubver = subver;
                }
            }

            if (result != null) return result;
        }

        result = new File(configDir + "dynamicstealth/dynamicstealth.cfg");
        if (result.exists()) return result;

        return currentFile;
    }

    private static String getVersion(File file)
    {
        if (file == null || !file.getName().contains(".cfg") || file.getName().contains(".cfg.")) return null;

        String name = file.getName();
        int index = name.lastIndexOf("+");
        if (index < 1) return null;
        return name.substring(0, index);
    }

    private static String subVer(String version)
    {
        int index = version.lastIndexOf(".");
        if (index < 1) return null;
        return version.substring(index + 1);
    }

    private static String getMCVer(String version)
    {
        int index = version.lastIndexOf(".");
        if (index < 1) return null;
        return version.substring(0, index);
    }

    public static void update() throws IOException
    {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Dynamic Stealth is checking config...");


        //If newest config version already exists or no config of any version exists, do nothing special
        if (mostRecentFile == currentFile)
        {
            if (currentAlreadyExists)
            {
                System.out.println("Compatible config file found; loading existing config file");
            }
            else
            {
                System.out.println("No config found; generating new config file");
            }

            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            return;
        }


        //If newest config version does not exist but a previous version does, do complicated things
        System.out.println("Old config file found; attempting conversion to new config file (the old file will remain unchanged)...");
        System.out.println();
        System.out.println();

        int recent;
        String mostRecentVer = getVersion(mostRecentFile);
        if (mostRecentVer == null || subVer(mostRecentVer) == null)
        {
            recent = 0;
            logFile = new File(dsDir + "update 1.12.2.55- to " + DynamicStealth.CONFIG_VERSION + "+.log");
        }
        else
        {
            recent = Integer.parseInt(subVer(mostRecentVer));
            logFile = new File(dsDir + "update " + getMCVer(DynamicStealth.CONFIG_VERSION) + recent + "+ to " + DynamicStealth.CONFIG_VERSION + "+.log");
        }
        logWriter = new BufferedWriter(new FileWriter(logFile));


        switch (recent)
        {
            case 0:
                //Config versions 55-
                updatePre56To56();
            case 56:
                //Config versions 56 - 68
                update56To68();
                //Don't use break here; allow cases to pass to the next one, so it does each update function incrementally
        }

        try
        {
            MCTools.reloadConfig(fullConfigFilename, DynamicStealth.MODID);
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 152, true);
        }

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }

    private static void updatePre56To56() throws IOException
    {
        Configuration current = new Configuration(currentFile);
        Configuration old = new Configuration(mostRecentFile);

        log("Setting \"Allow On-Point HUD For Clients\" based on \"On-Point HUD for OP players\" and \"On-Point HUD for normal players\"");
        Property p = current.getCategory("general.server settings.client hud allowances").get("Allow On-Point HUD For Clients");
        if (old.get("general.server settings.client hud allowances", "On-Point HUD for normal players", 2).getInt() > 0) p.set(2);
        else if (old.get("general.server settings.client hud allowances", "On-Point HUD for OP players", 2).getInt() > 0) p.set(1);
        else p.set(0);
        log();
        log();
        log();

        rename(old, "general.server settings.client hud allowances", "Allow detailed HUD on clients", "general.server settings.client hud allowances", "Allow Targeting HUD For Clients");

        log();
        log();
        log();

        transferAll(old, current);
    }

    private static void update56To68() throws IOException
    {
        Configuration current = new Configuration(currentFile);
        Configuration old = new Configuration(mostRecentFile);

        rename(old, "general.server settings.client hud allowances", "Allow Targeting HUD For Clients", "general.server settings.client hud allowances.targeting allowances", "050 Allow 'Action' Element");
        rename(old, "general.server settings.client hud allowances", "Allow On-Point HUD For Clients", "general.server settings.client hud allowances.ophud allowances", "000 Allow On-Point HUD For Clients");
        rename(old, "general.server settings.client hud allowances", "OPHUD Range", "general.server settings.client hud allowances.ophud allowances", "010 OPHUD Range");
        rename(old, "general.server settings.client hud allowances", "OPHUD Update Delay", "general.server settings.client hud allowances.ophud allowances", "020 OPHUD Update Delay");

        rename(old, "general.client settings.hud.targeting hud style.components", "Target's Name", "general.client settings.hud.targeting hud style.components", "010 Name");
        rename(old, "general.client settings.hud.targeting hud style.components", "Target's Health", "general.client settings.hud.targeting hud style.components", "020 Health");
        rename(old, "general.client settings.hud.targeting hud style.components", "Target's Target", "general.client settings.hud.targeting hud style.components", "030 Action");
        rename(old, "general.client settings.hud.targeting hud style.components", "Target's Threat", "general.client settings.hud.targeting hud style.components", "040 Threat");
        rename(old, "general.client settings.hud.targeting hud style.components", "Target's Distance", "general.client settings.hud.targeting hud style.components", "050 Distance");

        rename(old, "general.client settings.hud.targeting filter", "Max Distance", "general.client settings.hud.targeting filter", "020 Max Distance");
        rename(old, "general.client settings.hud.targeting filter", "Max Angle", "general.client settings.hud.targeting filter", "030 Max Angle");
        rename(old, "general.client settings.hud.targeting filter", "Bypass", "general.client settings.hud.targeting filter", "070 Bypass");
        rename(old, "general.client settings.hud.targeting filter", "Passive", "general.client settings.hud.targeting filter", "071 Idle (Passive)");
        rename(old, "general.client settings.hud.targeting filter", "Idle", "general.client settings.hud.targeting filter", "072 Idle (Non-Passive)");
        rename(old, "general.client settings.hud.targeting filter", "Attacking Other", "general.client settings.hud.targeting filter", "073 Attacking Other");
        rename(old, "general.client settings.hud.targeting filter", "Alert", "general.client settings.hud.targeting filter", "074 Alert");
        rename(old, "general.client settings.hud.targeting filter", "Attacking You", "general.client settings.hud.targeting filter", "075 Attacking You");
        rename(old, "general.client settings.hud.targeting filter", "Flee2", "general.client settings.hud.targeting filter", "076 Fleeing (Passive)");
        rename(old, "general.client settings.hud.targeting filter", "Flee", "general.client settings.hud.targeting filter", "077 Fleeing (Non-Passive)");

        log();
        log();
        log();

        transferAll(old, current);
    }

    private static void rename(Configuration old, String oldCat, String oldName, String newCat, String newName) throws IOException
    {
        log("* Renaming... " + oldCat + " -> " + oldName);
        log("* To... " + newCat + " -> " + newName);
        old.moveProperty(oldCat, oldName, newCat);
        old.renameProperty(newCat, oldName, newName);
    }

    private static void transferAll(Configuration old, Configuration current) throws IOException
    {
        for (String string : current.getCategoryNames())
        {
            if (old.hasCategory(string))
            {
                log("~ Found matching category: \"" + string + "\"...");
                log();

                ConfigCategory oldCat = old.getCategory(string);
                Set<String> oldKeys = oldCat.keySet();
                for (Map.Entry<String, Property> entry : current.getCategory(string).entrySet())
                {
                    String k = entry.getKey();
                    if (oldKeys.contains(k))
                    {
                        entry.setValue(oldCat.get(k));
                        log("~ Copied values for matching entry: \"" + k + "\"");
                    }
                    else log("+ Adding new entry: \"" + k + "\"");
                }
            }
            else
            {
                log("+ Adding new category: \"" + string + "\"...");
                log();

                for (Map.Entry<String, Property> entry : current.getCategory(string).entrySet())
                {
                    log("+ Adding new entry: \"" + entry.getKey() + "\"");
                }
            }

            log();
            log();
        }


        log();
        log();


        for (String string : old.getCategoryNames())
        {
            if (!current.hasCategory(string))
            {
                log("- Removing old category: \"" + string + "\"...");
                log();

                for (Map.Entry<String, Property> entry : old.getCategory(string).entrySet())
                {
                    log("- Removing old entry: \"" + entry.getKey() + "\"");
                }

                log();
                log();
            }
            else
            {
                boolean printedCat = false;
                Set<String> newKeys = current.getCategory(string).keySet();
                for (Map.Entry<String, Property> entry : old.getCategory(string).entrySet())
                {
                    String k = entry.getKey();
                    if (!newKeys.contains(k))
                    {
                        if (!printedCat)
                        {
                            log("- Removing some entries from category: \"" + string + "\"...");
                            log();
                            printedCat = true;
                        }

                        log("- Removing old entry: \"" + k + "\"");
                    }
                }

                if (printedCat)
                {
                    log();
                    log();
                }
            }
        }

        current.save();
        logWriter.close();
    }


    private static void log() throws IOException
    {
        logWriter.write("\r\n");
        System.out.println();
    }

    private static void log(String string) throws IOException
    {
        logWriter.write(string + "\r\n");
        System.out.println(string);
    }
}
