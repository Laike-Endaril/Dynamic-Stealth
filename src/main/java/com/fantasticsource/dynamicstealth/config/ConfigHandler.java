package com.fantasticsource.dynamicstealth.config;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
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
    private static File mostRecentFile = new File(dsDir + DynamicStealth.CONFIG_VERSION + "+.cfg");
    private static File logFile;
    private static BufferedWriter logWriter;

    public static void init()
    {
        mostRecentFile = mostRecent();
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

        result = new File(configDir + "dynamicstealth.cfg");
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
        if (mostRecentFile == null)
        {
            System.out.println("No config found; generating new config file");
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            return;
        }

        if (mostRecentFile == currentFile)
        {
            System.out.println("Compatible config file found; loading existing config file");
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
                updatePre56To56(mostRecentFile);
            case 56:
                //TODO This is where changes go for 56+ -> XX+ (w/e the next config version is)
                //Would look something like...
                //update56ToXX();
                //Don't use break here; allow cases to pass to the next one, so it does each update function incrementally
        }


        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }

    private static void updatePre56To56(File oldConfig) throws IOException
    {
        transferAll(oldConfig);
        try
        {
            MCTools.reloadConfig(fullConfigFilename, DynamicStealth.MODID);
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 152, true);
        }
    }

    private static void transferAll(File oldConfig) throws IOException
    {
        Configuration current = new Configuration(currentFile);
        Configuration old = new Configuration(oldConfig);

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

                for (Map.Entry<String, Property> entry : current.getCategory(string).entrySet())
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
