package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;

public class CompatCorailTombstone
{
    public static Enchantment shadowStepEnchantment = null;

    public static double shadowStepVisMultiplier(EntityLivingBase livingBase, int lightLevel)
    {
        if (shadowStepEnchantment == null) return 1;

        return 1d - (15 - lightLevel) * DynamicStealthConfig.serverSettings.senses.sight.a_stealthMultipliers.corailTombstoneShadowStepStealthMultiplier * EnchantmentHelper.getMaxEnchantmentLevel(shadowStepEnchantment, livingBase);
    }
}
