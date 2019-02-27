package com.fantasticsource.dynamicstealth.config;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.tools.ReflectionTool;

import java.io.File;

public class ConfigHandler
{
    public static final String CONFIG_NAME = DynamicStealth.MODID + "/" + DynamicStealth.CONFIG_VERSION + "+";

    private static String configDir = new File(".").getAbsolutePath() + File.separator + "config" + File.separator;
    private static String dsDir = configDir + DynamicStealth.MODID + File.separator;
    private static File currentVer = new File(dsDir + DynamicStealth.CONFIG_VERSION + "+.cfg");

    public static void init()
    {
        //If newest config version already exists or no config of any version exists, do nothing special
        File mostRecent = mostRecent();
        if (mostRecent == null || mostRecent == currentVer) return;

        //If newest config version does not exist but a previous version does, do complicated things
        update(mostRecent);
    }

    private static void setName(String name)
    {
        try
        {
            ReflectionTool.getField(ConfigHandler.class, "CONFIG_NAME").set(null, name);
        }
        catch (IllegalAccessException | NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    private static File mostRecent()
    {
        if (currentVer.exists()) return currentVer;

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
                if (mcVer == null || !mcVer.equals(getMCVer(getVersion(currentVer)))) continue;

                ver = subVer(ver);
                if (ver == null || !mcVer.equals(getMCVer(getVersion(currentVer)))) continue;

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

        return currentVer;
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

    private static void update(File mostRecent)
    {
        //TODO
    }
}
