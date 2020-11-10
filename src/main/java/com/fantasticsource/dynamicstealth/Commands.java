package com.fantasticsource.dynamicstealth;

import com.fantasticsource.dynamicstealth.config.ConfigHandler;
import com.fantasticsource.dynamicstealth.server.senses.HidingData;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.PlayerData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.*;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.WHITE;

public class Commands extends CommandBase
{
    @Override
    public String getName()
    {
        return "dstealth";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        if (sender.canUseCommand(2, getName()))
        {
            return AQUA + "/dstealth threat <entity UUID> <target entity UUID> <amount>" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.threat.comment") + "\n" +

                    AQUA + "/dstealth hidefrom <playername> <t/f/true/false>" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.hidefromPlayerTF.comment") + "\n" +
                    AQUA + "/dstealth hidefrom <playername>" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.hidefromPlayer.comment") + "\n" +
                    AQUA + "/dstealth hidefrom" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.hidefrom.comment");
        }
        else
        {
            return AQUA + "/dstealth hidefrom <playername> <t/f/true/false>" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.hidefromPlayerTF.comment") + "\n" +
                    AQUA + "/dstealth hidefrom <playername>" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.hidefromPlayer.comment") + "\n" +
                    AQUA + "/dstealth hidefrom" + WHITE + " - " + I18n.translateToLocalFormatted(DynamicStealth.MODID + ".cmd.hidefrom.comment");
        }
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 0) sender.getCommandSenderEntity().sendMessage(new TextComponentString(getUsage(sender)));
        else subCommand(sender, args);
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        ArrayList<String> result = new ArrayList<>();

        String partial = args[args.length - 1];
        if (args.length == 1)
        {
            if (sender.canUseCommand(2, getName()))
            {
                result.add("threat");
            }
            result.add("hidefrom");

            if (partial.length() != 0) result.removeIf(k -> partial.length() > k.length() || !k.substring(0, partial.length()).equalsIgnoreCase(partial));
        }
        else if (args.length == 2)
        {
            if (args[0].equals("hidefrom"))
            {
                result.addAll(Arrays.asList(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOnlinePlayerNames()));

                for (PlayerData data : PlayerData.playerData.values())
                {
                    if (!result.contains(data.name)) result.add(data.name);
                }

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
            case "hidefrom":
                try
                {
                    EntityPlayer player = getCommandSenderAsPlayer(sender);
                    if (args.length == 1)
                    {
                        HashSet<UUID> set = HidingData.hidingData.computeIfAbsent(player.getPersistentID(), k -> new HidingData(player)).notHidingFrom;
                        if (set.size() == 0) notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromEmpty");
                        else
                        {
                            StringBuilder textList = new StringBuilder();
                            for (UUID id : set)
                            {
                                String name = PlayerData.getName(id);
                                if (name != null) textList.append("\n§e").append(name);
                            }
                            notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefrom", textList.toString());
                        }
                    }
                    else if (args.length == 2)
                    {
                        if (HidingData.isHidingFrom(player, args[1])) notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerTrue", args[1]);
                        else notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerFalse", args[1]);
                    }
                    else
                    {
                        if (args[2].equalsIgnoreCase("t") || args[2].equalsIgnoreCase("true"))
                        {
                            if (HidingData.isHidingFrom(player, args[1])) notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerTrue", args[1]);
                            else
                            {
                                HidingData.hideFrom(player, args[1], true);
                                notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerSetTrue", args[1]);
                            }
                        }
                        else if (args[2].equalsIgnoreCase("f") || args[2].equalsIgnoreCase("false"))
                        {
                            if (!HidingData.isHidingFrom(player, args[1])) notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerFalse", args[1]);
                            else
                            {
                                HidingData.hideFrom(player, args[1], false);
                                notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerSetFalse", args[1]);
                            }
                        }
                        else
                        {
                            notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.hidefromPlayerTFError");
                        }
                    }
                }
                catch (PlayerNotFoundException e)
                {
                    e.printStackTrace();
                }
                break;


            case "threat":
                if (!sender.canUseCommand(2, getName()))
                {
                    notifyCommandListener(sender, this, "commands.generic.permission");
                    return;
                }

                if (args.length != 4)
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    break;
                }
                Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(UUID.fromString(args[1]));
                if (entity == null)
                {
                    notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.threatEntityMissing", args[1]);
                    break;
                }
                if (!(entity instanceof EntityLivingBase))
                {
                    notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.threatEntityNotLivingBase", args[1]);
                    break;
                }
                Entity target = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(UUID.fromString(args[2]));
                if (target == null)
                {
                    notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.threatEntityMissing", args[2]);
                    break;
                }
                if (!(target instanceof EntityLivingBase))
                {
                    notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.threatEntityNotLivingBase", args[2]);
                    break;
                }
                float amount;
                try
                {
                    amount = Float.parseFloat(args[3]);
                }
                catch (NumberFormatException e)
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    break;
                }
                if (amount < 0 || amount > 100)
                {
                    notifyCommandListener(sender, this, DynamicStealth.MODID + ".cmd.threatInvalidAmount", args[2]);
                    break;
                }
                Threat.set((EntityLivingBase) entity, (EntityLivingBase) target, amount);
                break;


            default:
                notifyCommandListener(sender, this, getUsage(sender));
                break;
        }
    }
}
