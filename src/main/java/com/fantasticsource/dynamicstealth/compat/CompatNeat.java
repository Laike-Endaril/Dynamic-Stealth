package com.fantasticsource.dynamicstealth.compat;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public class CompatNeat
{
    public static Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), "neat.cfg"));
    public static double heightAboveMob = config.get("general", "Height Above Mob", 0.6).getDouble();
}
