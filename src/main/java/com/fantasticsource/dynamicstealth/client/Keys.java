package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final KeyBinding SHOW_HIDE_STEALTH_GAUGE = new KeyBinding(DynamicStealth.MODID + ".key.showHideStealthGauge", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, DynamicStealth.MODID + ".keyCategory");

    public static void init(FMLPreInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(SHOW_HIDE_STEALTH_GAUGE);
    }
}
