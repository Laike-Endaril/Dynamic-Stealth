package com.fantasticsource.dynamicstealth.server.senses;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.UUID;

public class HidingData
{
    public static LinkedHashMap<UUID, HidingData> hidingData = new LinkedHashMap<>();
    private static boolean save = true;

    public EntityPlayer player;
    public HashSet<UUID> notHidingFrom = new HashSet<>();

    public HidingData(EntityPlayer player, UUID... notHidingFrom)
    {
        this.player = player;
        this.notHidingFrom.addAll(Arrays.asList(notHidingFrom));
    }

    public static void save(EntityPlayer player)
    {
        String filename = MCTools.getDataDir(FMLCommonHandler.instance().getMinecraftServerInstance());
        File file = new File(filename);
        if (!file.exists()) file.mkdir();

        filename += File.separator + DynamicStealth.MODID;
        file = new File(filename);
        if (!file.exists()) file.mkdir();

        filename += File.separator + player.getPersistentID();
        file = new File(filename);
        if (!file.exists()) file.mkdir();

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename + File.separator + "not hiding from.txt")));
            for (UUID id : hidingData.computeIfAbsent(player.getPersistentID(), k -> new HidingData(player)).notHidingFrom)
            {
                writer.write(id.toString() + "\r\n");
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void load(EntityPlayer player)
    {
        String filename = MCTools.getDataDir(FMLCommonHandler.instance().getMinecraftServerInstance());
        File file = new File(filename);
        if (!file.exists()) file.mkdir();

        filename += File.separator + DynamicStealth.MODID;
        file = new File(filename);
        if (!file.exists()) file.mkdir();

        filename += File.separator + player.getPersistentID();
        file = new File(filename);
        if (!file.exists()) file.mkdir();

        file = new File(filename + File.separator + "not hiding from.txt");
        try
        {
            if (!file.exists()) file.createNewFile();
            else
            {
                save = false;

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null && !line.trim().equals(""))
                {
                    hideFrom(player, UUID.fromString(line), false);
                    line = reader.readLine();
                }

                save(player);
                save = true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isHidingFrom(EntityPlayer player, String targetName)
    {
        return isHidingFrom(player, PlayerData.getID(targetName));
    }

    public static boolean isHidingFrom(EntityPlayer player, UUID id)
    {
        if (id.equals(player.getPersistentID())) return false;

        HidingData data = hidingData.get(player.getPersistentID());
        if (data == null) return true;

        return !data.notHidingFrom.contains(id);
    }

    public static void hideFrom(EntityPlayer player, String targetName, boolean hide)
    {
        hideFrom(player, PlayerData.getID(targetName), hide);
    }

    public static void hideFrom(EntityPlayer player, UUID id, boolean hide)
    {
        if (hide == isHidingFrom(player, id)) return;

        HidingData data = hidingData.computeIfAbsent(player.getPersistentID(), k -> new HidingData(player));

        if (hide) data.notHidingFrom.remove(id);
        else data.notHidingFrom.add(id);

        if (save) save(player);
    }
}
