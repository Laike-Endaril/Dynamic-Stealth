package com.fantasticsource.dynamicstealth.config.server.ai;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class FleeConfig
{
    @Config.Name("HP Threshold")
    @Config.LangKey(DynamicStealth.MODID + ".config.fleeThreshold")
    @Config.Comment("The % of health at which entities start to flee")
    @Config.RangeInt(min = 0, max = 100)
    public int threshold = 40;

    @Config.Name("Damage Increases Flee Duration")
    @Config.LangKey(DynamicStealth.MODID + ".config.fleeDamageLengthen")
    @Config.Comment("If set to true, then when an entity is *already* in flee mode, damage will increase its flee duration")
    public boolean increaseOnDamage = true;

    @Config.Name("CNPCs flee To Home Position")
    @Config.LangKey(DynamicStealth.MODID + ".config.fleeHome")
    @Config.Comment("If enabled, Custom NPCs flee to their home position instead of away from what hit them")
    public boolean cnpcsRunHome = false;
}
