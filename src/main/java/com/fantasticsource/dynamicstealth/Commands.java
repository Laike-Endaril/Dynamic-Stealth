package com.fantasticsource.dynamicstealth;

import com.fantasticsource.dynamicstealth.config.ConfigHandler;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.*;

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
        return AQUA + "/dstealth reload" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.reload.comment") + "\n" +
                AQUA + "/dstealth hidefrom <playername> <t/f/true/false>" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.hidefromPlayerTF.comment") + "\n" +
                AQUA + "/dstealth hidefrom <playername>" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.hidefromPlayer.comment") + "\n" +
                AQUA + "/dstealth hidefrom" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.hidefrom.comment");
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

        String partial = args[args.length - 1];
        if (args.length == 1)
        {
            result.add("reload");
            result.add("hidefrom");

            if (partial.length() != 0) result.removeIf(k -> partial.length() > k.length() || !k.substring(0, partial.length()).equalsIgnoreCase(partial));
        }
        else if (args.length == 2)
        {
            if (args[0].equals("hidefrom"))
            {
                result.addAll(Arrays.asList(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOnlinePlayerNames()));

                if (partial.length() != 0) result.removeIf(k -> partial.length() > k.length() || !k.substring(0, partial.length()).equalsIgnoreCase(partial));
            }
        }
        else if (args.length == 3)
        {
            if (args[0].equals("hidefrom"))
            {
                result.add("true");
                result.add("false");

                if (partial.length() != 0) result.removeIf(k -> partial.length() > k.length() || !k.substring(0, partial.length()).equalsIgnoreCase(partial));
            }
        }
        return result;
    }

    private void subCommand(ICommandSender sender, String[] args)
    {
        String cmd = args[0];
        switch (cmd)
        {
            case "reload":
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
                break;
            case "hidefrom":
                if (args.length == 1)
                {
                    notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefrom");
                }
                else if (args.length == 2)
                {
                    notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerTrue", args[1]);
                    notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerFalse", args[1]);
                }
                else
                {
                    if (args[2].equalsIgnoreCase("t") || args[2].equalsIgnoreCase("true"))
                    {
                        notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerSetTrue", args[1]);
                        notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerTrue", args[1]);
                    }
                    else if (args[2].equalsIgnoreCase("f") || args[2].equalsIgnoreCase("false"))
                    {
                        notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerSetFalse", args[1]);
                        notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerFalse", args[1]);
                    }
                    else
                    {

                    }
                }
                break;
            default:
                notifyCommandListener(sender, this, getUsage(sender));
                break;
        }
    }
}
