package com.fantasticsource.dynamicstealth.server.event;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class WeaponEntry
{
    public boolean armorPenetration = false;
    public double damageMultiplier = 1;
    public ArrayList<PotionEffect> attackerEffects = StealthAttackData.attackerEffects;
    public ArrayList<PotionEffect> victimEffects = StealthAttackData.victimEffects;
    public boolean consumeItem = false;

    ItemStack itemStack = null;
    private LinkedHashMap<String, String> tags = new LinkedHashMap<>();

    private WeaponEntry()
    {
    }

    public WeaponEntry(String configEntry)
    {
        String[] tokens = configEntry.split(Pattern.quote(","));
        String token;

        if (tokens.length < 2)
        {
            System.err.println("Not enough arguments for weapon-specific stealth attack entry: " + configEntry);
            return;
        }
        if (tokens.length > 6)
        {
            System.err.println("Too many arguments for weapon-specific stealth attack entry: " + configEntry);
            return;
        }

        String[] namePlusTags = tokens[0].trim().split(Pattern.quote(">"));
        if (namePlusTags.length > 2)
        {
            System.err.println("Too many arguments for name/NBT pair in weapon-specific stealth attack entry: " + configEntry);
            return;
        }


        //Item and meta
        token = namePlusTags[0].trim();
        if (token.equals("")) itemStack = new ItemStack(Items.AIR);
        else
        {
            String[] innerTokens = token.split(Pattern.quote(":"));
            if (innerTokens.length != 3)
            {
                System.err.println("Invalid item name for weapon-specific stealth attack entry: " + token + ". This requires a full name, eg. minecraft:dye:0");
                return;
            }

            ResourceLocation resourceLocation = new ResourceLocation(innerTokens[0], innerTokens[1]);
            Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
            if (item != null)
            {
                itemStack = new ItemStack(item, 1, Integer.parseInt(innerTokens[2]));
            }
            else
            {
                Block block = ForgeRegistries.BLOCKS.containsKey(resourceLocation) ? ForgeRegistries.BLOCKS.getValue(resourceLocation) : null;
                if (block != null) itemStack = new ItemStack(block, 1, Integer.parseInt(innerTokens[2]));
            }
        }

        if (itemStack == null)
        {
            System.err.println("Item for weapon-specific stealth attack not found: " + token);
            return;
        }


        //NBT
        if (namePlusTags.length > 1)
        {
            String[] tags = namePlusTags[1].trim().split(Pattern.quote("&"));
            for (String tag : tags)
            {
                tag = tag.trim();
                if (tag.equals("")) continue;

                String[] keyValue = tag.split(Pattern.quote("="));
                if (keyValue.length > 2)
                {
                    System.err.println("Each NBT tag can only be set to one value!  Error in weapon-specific stealth attack entry: " + configEntry);
                    return;
                }

                String key = keyValue[0].trim();
                if (!key.equals("")) this.tags.put(key, keyValue.length == 2 ? keyValue[1].trim() : null);
            }
        }


        //Easy stuff...
        armorPenetration = Boolean.parseBoolean(tokens[1]);
        damageMultiplier = Double.parseDouble(tokens[2]);


        //Potion effects
        if (tokens.length > 3)
        {
            attackerEffects = getPotions(tokens[3].split(Pattern.quote("&")));
            if (tokens.length > 4)
            {
                victimEffects = getPotions(tokens[4].split(Pattern.quote("&")));
            }
        }


        //More easy stuff...
        if (tokens.length > 5) consumeItem = Boolean.parseBoolean(tokens[5].trim());
    }

    public static WeaponEntry get(ItemStack itemStack)
    {
        NBTTagCompound compound;
        boolean match;

        for (Map.Entry<ItemStack, WeaponEntry> weaponMapping : StealthAttackData.weaponSpecific.entrySet())
        {
            WeaponEntry weaponEntry = weaponMapping.getValue();
            ItemStack item = weaponMapping.getKey();
            System.out.println(item.getItem() + " ?= " + itemStack.getItem());
            if (item.getItem().equals(itemStack.getItem()) && (itemStack.isItemStackDamageable() || item.getMetadata() == itemStack.getMetadata()))
            {
                match = true;
                Set<Map.Entry<String, String>> entrySet = weaponEntry.tags.entrySet();

                if (entrySet.size() > 0)
                {
                    compound = itemStack.getTagCompound();
                    if (compound == null) match = false;
                    else
                    {
                        for (Map.Entry<String, String> entry : entrySet)
                        {
                            if (!compound.hasKey(entry.getKey()) || (entry.getValue() != null && !compound.getTag(entry.getKey()).toString().equals(entry.getValue())))
                            {
                                match = false;
                                break;
                            }
                        }
                    }
                }

                if (match) return weaponEntry;
            }
        }

        return new WeaponEntry();
    }


    public static ArrayList<PotionEffect> getPotions(String[] potionList)
    {
        String[] tokens;
        int duration, amplifier;
        Potion potion;
        ArrayList<PotionEffect> result = new ArrayList<>();

        for (String string : potionList)
        {
            string = string.trim();
            if (!string.equals(""))
            {
                tokens = string.split(Pattern.quote("."));
                if (tokens.length > 3)
                {
                    System.err.println("Too many arguments for potion effect; should be max of 3 (see the Attacker Effects config tooltip for example)");
                    continue;
                }

                duration = 0;
                amplifier = 0;

                if (tokens.length > 0)
                {
                    potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(tokens[0].trim()));

                    if (potion == null)
                    {
                        System.err.println("ResourceLocation for potion \"" + string + "\" not found!");
                        continue;
                    }

                    if (tokens.length > 1) duration = Integer.parseInt(tokens[1].trim());
                    if (tokens.length > 2) amplifier = Integer.parseInt(tokens[2].trim());
                    if (amplifier > 0) amplifier--; //Makes it so ppl can just type 2 for stength 2 instead of typing 1

                    result.add(new PotionEffect(potion, duration, amplifier, false, true));
                }
            }
        }

        return result;
    }
}
