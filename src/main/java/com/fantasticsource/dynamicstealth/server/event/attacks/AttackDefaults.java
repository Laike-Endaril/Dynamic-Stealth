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


        stealthAttackDefaults.add("backstab:wood_dagger, true, 5");
        stealthAttackDefaults.add("backstab:stone_dagger, true, 5");
        stealthAttackDefaults.add("backstab:iron_dagger, true, 5");
        stealthAttackDefaults.add("backstab:diamond_dagger, true, 5");
        stealthAttackDefaults.add("backstab:gold_dagger, true, 5");

        stealthAttackDefaults.add("natura:ghostwood_kama, true, 3");
        stealthAttackDefaults.add("natura:bloodwood_kama, true, 3");
        stealthAttackDefaults.add("natura:darkwood_kama, true, 3");
        stealthAttackDefaults.add("natura:fusewood_kama, true, 3");
        stealthAttackDefaults.add("natura:netherquartz_kama, true, 3");
        stealthAttackDefaults.add("tetra:duplex_tool_modular > duplex/sickle_left_material & duplex/butt_right_material, true, 3");
        stealthAttackDefaults.add("tconstruct:kama, true, 3");
    }
}
