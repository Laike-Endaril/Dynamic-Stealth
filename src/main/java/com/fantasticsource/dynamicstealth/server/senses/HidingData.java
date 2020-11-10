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
    public boolean creativeInvis;
    public HashSet<UUID> notHidingFrom = new HashSet<>();

    public HidingData(EntityPlayer player, boolean creativeInvis, UUID... notHidingFrom)
    {
        this.player = player;
        this.creativeInvis = creativeInvis;
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
            HidingData data = hidingData.computeIfAbsent(player.getPersistentID(), k -> new HidingData(player, true));


            //Save general settings
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename + File.separator + "settings.txt")));

            writer.write(data.creativeInvis + "\r\n");

            writer.close();


            //Save not-hiding-from list
            writer = new BufferedWriter(new FileWriter(new File(filename + File.separator + "not hiding from.txt")));

            for (UUID id : data.notHidingFrom)
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


        save = false;

        try
        {
            //Load general settings
            file = new File(filename + File.separator + "settings.txt");
            if (file.exists())
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line = reader.readLine();
                if (line != null && line.trim().equals("false")) setCreativeInvis(player, false);

                reader.close();
            }

            //Load not-hiding-from list
            file = new File(filename + File.separator + "not hiding from.txt");
            if (file.exists())
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line = reader.readLine();
                while (line != null && !line.trim().equals(""))
                {
                    hideFrom(player, UUID.fromString(line), false);
                    line = reader.readLine();
                }

                reader.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        save = true;
    }


    public static boolean isCreativeInvis(EntityPlayer player)
    {
        HidingData data = hidingData.get(player.getPersistentID());
        if (data == null) return true;

        return data.creativeInvis;
    }

    public static void setCreativeInvis(EntityPlayer player, boolean creativeInvis)
    {
        if (creativeInvis == isCreativeInvis(player)) return;

        HidingData data = hidingData.computeIfAbsent(player.getPersistentID(), k -> new HidingData(player, creativeInvis));
        data.creativeInvis = creativeInvis;

        if (save) save(player);
    }


    public static boolean isHidingFrom(EntityPlayer player, String targetName)
    {
        return isHidingFrom(player, PlayerData.getID(targetName));
    }

    public static boolean isHidingFrom(EntityPlayer player, UUID id)
    {
        if (id == null) return true;

        if (id.equals(player.getPersistentID())) return false;

        HidingData data = hidingData.get(player.getPersistentID());
        if (data == null) return true;

        return !data.notHidingFrom.contains(id);
    }

    public static void hideFrom(EntityPlayer player, String targetName, boolean hide)
    {
        UUID id = PlayerData.getID(targetName);
        if (id != null) hideFrom(player, PlayerData.getID(targetName), hide);
        else System.out.println(player.getName() + " tried to hide from " + targetName + " but " + targetName + " was not found in PlayerData!");
    }

    public static void hideFrom(EntityPlayer player, UUID id, boolean hide)
    {
        if (hide == isHidingFrom(player, id)) return;

        HidingData data = hidingData.computeIfAbsent(player.getPersistentID(), k -> new HidingData(player, true));

        if (hide) data.notHidingFrom.remove(id);
        else data.notHidingFrom.add(id);

        if (save) save(player);
    }
}
