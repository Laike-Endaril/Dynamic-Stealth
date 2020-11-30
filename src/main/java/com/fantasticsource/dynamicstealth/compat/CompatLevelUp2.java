package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;

public class CompatLevelUp2
{
    protected static final ResourceLocation LEVEL_UP_2_STEALTH_SKILL = new ResourceLocation("levelup", "stealth");
    protected static final Method SKILL_REGISTRY_GET_SKILL_LEVEL_METHOD;

    static
    {
        if (Loader.isModLoaded("levelup2"))
        {
            SKILL_REGISTRY_GET_SKILL_LEVEL_METHOD = ReflectionTool.getMethod(ReflectionTool.getClassByName("levelup2.skills.SkillRegistry"), "getSkillLevel");
        }
        else
        {
            SKILL_REGISTRY_GET_SKILL_LEVEL_METHOD = null;
        }
    }

    public static double stealthLevelVisMultiplier(EntityPlayer player)
    {
        if (SKILL_REGISTRY_GET_SKILL_LEVEL_METHOD == null) return 1;

        int level = (int) ReflectionTool.invoke(SKILL_REGISTRY_GET_SKILL_LEVEL_METHOD, null, player, LEVEL_UP_2_STEALTH_SKILL);
        return 1d - DynamicStealthConfig.serverSettings.senses.sight.a_stealthMultipliers.levelUp2StealthMultiplier * level;
    }
}
