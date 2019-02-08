package com.fantasticsource.mctools.advancedconfig;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.DefaultGuiFactory;

public abstract class AdvancedConfigFactory extends DefaultGuiFactory
{
    /**
     * How to use:<br>
     * 1. Extend this class<br>
     * 2. In your class extending this one, create a PUBLIC constructor that takes no arguments and calls super(), passing in your preferred values for modid and title/name<br>
     * 3. In your main mod class's @Mod tag, reference your class which extends this class, eg...<br>
     *<br>
     * {@literal @}Mod(guiFactory = "com.example.modid.YourConfigFactoryExtendingThisClass")<br>
     *<br>
     * Be sure to use the full classpath
     */

    //Just in case someone is looking at this directly...in plain text, that tag example is...
    //@Mod(guiFactory = "com.example.modid.YourConfigFactoryExtendingThisClass")
    public AdvancedConfigFactory(String modid, String title)
    {
        super(modid, title);
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen)
    {
        return new AdvancedGuiConfig(parentScreen, modid, title);
    }
}
