package com.fantasticsource.dynamicstealth.server.event.attacks;

import com.fantasticsource.dynamicstealth.config.server.interactions.StealthAttackConfig;
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

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class WeaponEntry
{
    public static final int TYPE_NORMAL = 0, TYPE_STEALTH = 1, TYPE_ASSASSINATION = 2;
    public static StealthAttackConfig config = serverSettings.interactions.stealthAttack;

    public boolean armorPenetration = config.armorPenetration;
    public double damageMultiplier = config.damageMultiplier;
    public ArrayList<PotionEffect> attackerEffects = AttackData.stealthAttackerEffects;
    public ArrayList<PotionEffect> victimEffects = AttackData.stealthVictimEffects;
    public boolean consumeItem = false;

    public ItemStack itemStack = null;
    private LinkedHashMap<String, String> tags = new LinkedHashMap<>();

    private WeaponEntry()
    {
    }

    public WeaponEntry(String configEntry, int type)
    {
        String[] tokens = configEntry.split(Pattern.quote(","));
        String token;

        if (tokens.length < 2)
        {
            System.err.println("Not enough arguments for weapon entry: " + configEntry);
            return;
        }
        if (((type == TYPE_NORMAL || type == TYPE_STEALTH) && tokens.length > 6) || (type == TYPE_ASSASSINATION && tokens.length > 2))
        {
            System.err.println("Too many arguments for weapon entry: " + configEntry);
            return;
        }

        String[] namePlusTags = tokens[0].trim().split(Pattern.quote(">"));
        if (namePlusTags.length > 2)
        {
            System.err.println("Too many arguments for name/NBT pair in weapon entry: " + configEntry);
            return;
        }


        //Item and meta
        token = namePlusTags[0].trim();
        if (token.equals("")) itemStack = new ItemStack(Items.AIR);
        else
        {
            ResourceLocation resourceLocation;
            int meta = 0;

            String[] innerTokens = token.split(Pattern.quote(":"));
            if (innerTokens.length > 3)
            {
                System.err.println("Bad item name: " + token);
                return;
            }
            if (innerTokens.length == 3)
            {
                resourceLocation = new ResourceLocation(innerTokens[0], innerTokens[1]);
                meta = Integer.parseInt(innerTokens[2]);
            }
            else if (innerTokens.length == 1) resourceLocation = new ResourceLocation("minecraft", innerTokens[0]);
            else
            {
                try
                {
                    meta = Integer.parseInt(innerTokens[1]);
                    resourceLocation = new ResourceLocation("minecraft", innerTokens[0]);
                }
                catch (NumberFormatException e)
                {
                    meta = 0;
                    resourceLocation = new ResourceLocation(innerTokens[0], innerTokens[1]);
                }
            }


            Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
            if (item != null)
            {
                itemStack = new ItemStack(item, 1, meta);
            }
            else
            {
                Block block = ForgeRegistries.BLOCKS.containsKey(resourceLocation) ? ForgeRegistries.BLOCKS.getValue(resourceLocation) : null;
                if (block != null) itemStack = new ItemStack(block, 1, Integer.parseInt(innerTokens[2]));
            }
        }

        if (itemStack == null)
        {
            if (type == TYPE_NORMAL && !AttackDefaults.normalAttackDefaults.contains(configEntry)) System.err.println("Item for normal attack weapon entry not found: " + token);
            if (type == TYPE_STEALTH && !AttackDefaults.stealthAttackDefaults.contains(configEntry)) System.err.println("Item for stealth attack weapon entry not found: " + token);
            if (type == TYPE_ASSASSINATION && !AttackDefaults.assassinationDefaults.contains(configEntry)) System.err.println("Item for assassination weapon entry not found: " + token);
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
                    System.err.println("Each NBT tag can only be set to one value!  Error in weapon entry: " + configEntry);
                    return;
                }

                String key = keyValue[0].trim();
                if (!key.equals("")) this.tags.put(key, keyValue.length == 2 ? keyValue[1].trim() : null);
            }
        }


        //Easy stuff...
        armorPenetration = Boolean.parseBoolean(tokens[1]);
        if (tokens.length > 2) damageMultiplier = Double.parseDouble(tokens[2]);


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

    public static WeaponEntry get(ItemStack itemStack, int type)
    {
        NBTTagCompound compound;
        boolean match;

        LinkedHashMap<ItemStack, WeaponEntry> map = null;
        if (type == TYPE_NORMAL) map = AttackData.normalWeaponSpecific;
        else if (type == TYPE_STEALTH) map = AttackData.stealthWeaponSpecific;
        else if (type == TYPE_ASSASSINATION) map = AttackData.assassinationWeaponSpecific;

        for (Map.Entry<ItemStack, WeaponEntry> weaponMapping : map.entrySet())
        {
            WeaponEntry weaponEntry = weaponMapping.getValue();
            ItemStack item = weaponMapping.getKey();
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
