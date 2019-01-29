package com.fantasticsource.dynamicstealth.common.potions.soulsight;

import com.fantasticsource.dynamicstealth.common.potions.Potions;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipe;

import javax.annotation.Nonnull;

public class RecipePotionSoulSight extends BrewingRecipe
{
    private static final ItemStack INPUT = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.THICK);
    private static final ItemStack REAGENT = new ItemStack(Items.ENDER_EYE);
    private static final ItemStack DEFAULT_OUTPUT = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), Potions.POTIONTYPE_SOULSIGHT_NORMAL);

    public RecipePotionSoulSight(@Nonnull ItemStack input, @Nonnull ItemStack ingredient, @Nonnull ItemStack output)
    {
        super(INPUT, REAGENT, DEFAULT_OUTPUT);
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack)
    {
        return PotionUtils.getPotionFromItem(stack) == PotionTypes.THICK;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(@Nonnull ItemStack input, @Nonnull ItemStack ingredient)
    {
        if (isInput(input) && isIngredient(ingredient)) return PotionUtils.addPotionToItemStack(new ItemStack(input.getItem()), Potions.POTIONTYPE_SOULSIGHT_NORMAL);
        return ItemStack.EMPTY;
    }
}
