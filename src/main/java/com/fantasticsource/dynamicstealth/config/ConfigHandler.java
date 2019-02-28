package com.fantasticsource.dynamicstealth.config;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler
{
    public static final String CONFIG_NAME = DynamicStealth.MODID + "/" + DynamicStealth.CONFIG_VERSION + "+";

    private static String configDir = new File(".").getAbsolutePath() + File.separator + "config" + File.separator;
    private static String dsDir = configDir + DynamicStealth.MODID + File.separator;
    private static File currentFile = new File(dsDir + DynamicStealth.CONFIG_VERSION + "+.cfg");
    private static File mostRecentFile = new File(dsDir + DynamicStealth.CONFIG_VERSION + "+.cfg");

    public static void init()
    {
        mostRecentFile = mostRecent();
    }


    //EVERYTHING ABOVE THIS HAPPENS *BEFORE* FORGE LOADS THE CONFIG


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


    //EVERYTHING BELOW THIS HAPPENS *AFTER* FORGE LOADS THE CONFIG


    public static void update()
    {
        //If newest config version already exists or no config of any version exists, do nothing special
        if (mostRecentFile == null || mostRecentFile == currentFile) return;

        //If newest config version does not exist but a previous version does, do complicated things
        int recent;
        String mostRecentVer = getVersion(mostRecentFile);
        if (mostRecentVer == null || subVer(mostRecentVer) == null)
        {
            //Config versions 55-
            initUpdatePre56To56(mostRecentFile);
            recent = 56;
        }
        else recent = Integer.parseInt(subVer(mostRecentVer));

        switch (recent)
        {
            case 56:
                //TODO This is where changes go for 56+ -> XX+ (w/e the next config version is)
                //Would look something like...
                //initUpdate56ToXX();
                //Don't use break here; allow cases to pass to the next one, so it does each update function incrementally
        }
    }

    private static void initUpdatePre56To56(File oldConfig)
    {
        Configuration old = new Configuration(oldConfig);
        boolean test = old.get("general.client settings.hud.on-point hud filter", "Alert", false).getBoolean();
        System.out.println("================================================================================================================================");
        System.out.println(test);
    }
}
