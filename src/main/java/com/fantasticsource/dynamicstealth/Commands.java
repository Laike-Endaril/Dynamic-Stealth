package com.fantasticsource.dynamicstealth;

import com.fantasticsource.dynamicstealth.config.ConfigHandler;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Commands extends CommandBase
{
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
                MCTools.reloadConfig(ConfigHandler.fullConfigFilename, DynamicStealth.MODID);
                DynamicStealth.update();
                notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.reloaded1");
                notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.reloaded2");
                notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.reloaded3");
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
