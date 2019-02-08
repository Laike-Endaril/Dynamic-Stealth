package com.fantasticsource.dynamicstealth.config.factoryclasses;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import javax.annotation.Nullable;
import java.util.List;

public class DynamicStealthGuiConfig extends GuiConfig
{
    public DynamicStealthGuiConfig(GuiScreen parentScreen, String modid, String title)
    {
        super(parentScreen, modid, title);
    }

    public DynamicStealthGuiConfig(GuiScreen parentScreen, String modID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title, Class<?>... configClasses)
    {
        super(parentScreen, modID, allRequireWorldRestart, allRequireMcRestart, title, configClasses);
    }

    public DynamicStealthGuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, String configID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title)
    {
        super(parentScreen, configElements, modID, configID, allRequireWorldRestart, allRequireMcRestart, title);
    }

    public DynamicStealthGuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title)
    {
        super(parentScreen, configElements, modID, allRequireWorldRestart, allRequireMcRestart, title);
    }

    public DynamicStealthGuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title, String titleLine2)
    {
        super(parentScreen, configElements, modID, allRequireWorldRestart, allRequireMcRestart, title, titleLine2);
    }

    public DynamicStealthGuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, @Nullable String configID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title, @Nullable String titleLine2)
    {
        super(parentScreen, configElements, modID, configID, allRequireWorldRestart, allRequireMcRestart, title, titleLine2);
    }
}
