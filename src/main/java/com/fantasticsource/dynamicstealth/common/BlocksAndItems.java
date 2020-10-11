package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.common.items.ItemHandMirror;
import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static com.fantasticsource.dynamicstealth.DynamicStealth.MODID;

public class BlocksAndItems
{
    @GameRegistry.ObjectHolder(MODID + ":handmirror")
    public static ItemHandMirror itemHandMirror;


    public static CreativeTabs creativeTab = new CreativeTabs(MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(itemHandMirror);
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> itemStacks)
        {
            super.displayAllRelevantItems(itemStacks);
        }
    };

    @SubscribeEvent
    public static void itemRegistry(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new ItemHandMirror());
    }

    @SubscribeEvent
    public static void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(itemHandMirror, 0, new ModelResourceLocation(MODID + ":handmirror", "inventory"));
    }

    public static void registerRecipes(FMLPostInitializationEvent event)
    {
        if (DynamicStealthConfig.serverSettings.itemSettings.handMirrorRecipe) GameRegistry.addShapedRecipe(new ResourceLocation(MODID, "handmirror"), new ResourceLocation(MODID, "items"), new ItemStack(itemHandMirror), new String[]{"g", "i"}, 'g', new ItemStack(Blocks.GLASS_PANE), 'i', new ItemStack(Items.IRON_INGOT));
    }
}
