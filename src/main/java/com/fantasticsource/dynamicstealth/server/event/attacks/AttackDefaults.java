package com.fantasticsource.dynamicstealth.server.event.attacks;

import java.util.ArrayList;

public class AttackDefaults
{
    public static ArrayList<String> normalAttackDefaults = new ArrayList<>();
    public static ArrayList<String> stealthAttackDefaults = new ArrayList<>();
    public static ArrayList<String> assassinationDefaults = new ArrayList<>();

    static
    {
        normalAttackDefaults.add("dye, false, 0, , blindness.20, true");
        normalAttackDefaults.add("glowstone_dust, false, 0, , glowing.20, true");


        //Daggers, knives, claws, katars, chisels, spikes, spines, tantos, shivs, and sharp bones
        stealthAttackDefaults.add("backstab:wood_dagger, true, 5");
        stealthAttackDefaults.add("backstab:stone_dagger, true, 5");
        stealthAttackDefaults.add("backstab:iron_dagger, true, 5");
        stealthAttackDefaults.add("backstab:diamond_dagger, true, 5");
        stealthAttackDefaults.add("backstab:gold_dagger, true, 5");
        stealthAttackDefaults.add("tetra:sword_modular > sword/short_blade_material, true, 5");
        stealthAttackDefaults.add("architecturecraft:chisel, true, 5");
        stealthAttackDefaults.add("chisel:chisel_iron, true, 5");
        stealthAttackDefaults.add("chisel:chisel_diamond, true, 5");
        stealthAttackDefaults.add("chisel:chisel_hitech, true, 5");
        stealthAttackDefaults.add("tcomplement:chisel, true, 5");
        stealthAttackDefaults.add("rootsclassic:druidknife, true, 5");
        stealthAttackDefaults.add("xreliquary:magicbane, true, 5");
        stealthAttackDefaults.add("elderarsenal:wooden_dagger, true, 5");
        stealthAttackDefaults.add("elderarsenal:stone_dagger, true, 5");
        stealthAttackDefaults.add("elderarsenal:iron_dagger, true, 5");
        stealthAttackDefaults.add("elderarsenal:golden_dagger, true, 5");
        stealthAttackDefaults.add("elderarsenal:diamond_dagger, true, 5");
        stealthAttackDefaults.add("elderarsenal:emerald_dagger, true, 5");
        stealthAttackDefaults.add("elderarsenal:hunger_dagger, true, 5");
        stealthAttackDefaults.add("elderarsenal:wooden_katar, true, 5");
        stealthAttackDefaults.add("elderarsenal:stone_katar, true, 5");
        stealthAttackDefaults.add("elderarsenal:iron_katar, true, 5");
        stealthAttackDefaults.add("elderarsenal:golden_katar, true, 5");
        stealthAttackDefaults.add("elderarsenal:diamond_katar, true, 5");
        stealthAttackDefaults.add("elderarsenal:emerald_katar, true, 5");
        stealthAttackDefaults.add("elderarsenal:wooden_knife, true, 5");
        stealthAttackDefaults.add("elderarsenal:stone_knife, true, 5");
        stealthAttackDefaults.add("elderarsenal:iron_knife, true, 5");
        stealthAttackDefaults.add("elderarsenal:golden_knife, true, 5");
        stealthAttackDefaults.add("elderarsenal:diamond_knife, true, 5");
        stealthAttackDefaults.add("elderarsenal:emerald_knife, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_wood, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_stone, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_iron, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_gold, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_diamond, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_wood, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_stone, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_iron, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_gold, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_diamond, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_copper, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_copper, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_tin, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_tin, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_bronze, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_bronze, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_steel, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_steel, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_silver, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_silver, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_invar, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_invar, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_platinum, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_platinum, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_electrum, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_electrum, true, 5");
        stealthAttackDefaults.add("spartanweaponry:dagger_nickel, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_nickel, true, 5");
        stealthAttackDefaults.add("spartanweaponry:throwing_knife_lead, true, 5");
        stealthAttackDefaults.add("sharpbone:sharp_bone, true, 5");

        //Kamas, katanas, rapiers, and sickles
        stealthAttackDefaults.add("natura:ghostwood_kama, true, 3");
        stealthAttackDefaults.add("natura:bloodwood_kama, true, 3");
        stealthAttackDefaults.add("natura:darkwood_kama, true, 3");
        stealthAttackDefaults.add("natura:fusewood_kama, true, 3");
        stealthAttackDefaults.add("natura:netherquartz_kama, true, 3");
        stealthAttackDefaults.add("tetra:duplex_tool_modular > duplex/sickle_left_material & duplex/butt_right_material, true, 3");
        stealthAttackDefaults.add("tconstruct:kama, true, 3");
        stealthAttackDefaults.add("elderarsenal:wooden_katana, true, 3");
        stealthAttackDefaults.add("elderarsenal:stone_katana, true, 3");
        stealthAttackDefaults.add("elderarsenal:iron_katana, true, 3");
        stealthAttackDefaults.add("elderarsenal:golden_katana, true, 3");
        stealthAttackDefaults.add("elderarsenal:diamond_katana, true, 3");
        stealthAttackDefaults.add("elderarsenal:emerald_katana, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_wood, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_stone, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_iron, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_gold, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_diamond, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_wood, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_stone, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_iron, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_gold, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_diamond, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_copper, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_copper, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_tin, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_tin, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_bronze, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_bronze, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_steel, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_steel, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_silver, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_silver, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_invar, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_invar, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_platinum, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_platinum, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_electrum, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_electrum, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_nickel, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_nickel, true, 3");
        stealthAttackDefaults.add("spartanweaponry:katana_lead, true, 3");
        stealthAttackDefaults.add("spartanweaponry:rapier_lead, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_copper, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_tin, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_silver, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_lead, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_aluminum, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_nickel, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_platinum, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_steel, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_electrum, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_invar, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_bronze, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_constantan, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_wood, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_stone, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_iron, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_diamond, true, 3");
        stealthAttackDefaults.add("thermalfoundation:tool.sickle_gold, true, 3");
    }
}
