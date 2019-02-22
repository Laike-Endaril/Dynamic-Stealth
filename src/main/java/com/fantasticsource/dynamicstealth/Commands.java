package com.fantasticsource.dynamicstealth;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Commands extends CommandBase
{
    private static File configFile = new File(Loader.instance().getConfigDir(), DynamicStealth.MODID + ".cfg");
    private static String configFilename = configFile.getAbsolutePath();
    private static Field configManagerCONFIGSField;

    static
    {
        try
        {
            configManagerCONFIGSField = ReflectionTool.getField(ConfigManager.class, "CONFIGS");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            MCTools.crash(e, 155, true);
        }
    }

    @Override
    public String getName()
    {
        return "dstealth";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/dstealth reload";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 0) sender.getCommandSenderEntity().sendMessage(new TextComponentString(getUsage(sender)));
        else
        {
            subCommand(sender, args);
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        ArrayList<String> result = new ArrayList<>();
        if (args.length == 1)
        {
            result.add("reload");
        }
        return result;
    }

    private void subCommand(ICommandSender sender, String[] args)
    {
        String cmd = args[0];
        if (cmd.equals("reload"))
        {
            try
            {
                ((Map<String, Configuration>) configManagerCONFIGSField.get(null)).remove(configFilename);
                ConfigManager.sync(DynamicStealth.MODID, Config.Type.INSTANCE);
                notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.reloaded");
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            notifyCommandListener(sender, this, getUsage(sender));
        }
    }
}
