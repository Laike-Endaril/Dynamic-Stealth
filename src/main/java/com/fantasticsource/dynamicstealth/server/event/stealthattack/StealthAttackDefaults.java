package com.fantasticsource.dynamicstealth.server.event.stealthattack;

import java.util.ArrayList;

public class StealthAttackDefaults
{
    public static ArrayList<String> weaponSpecific = new ArrayList<>();

    static
    {
        weaponSpecific.add(", false, 1, , blindness.100");
        weaponSpecific.add("minecraft:dye:0, false, 0, , blindness.100, true");
        weaponSpecific.add("backstab:wood_dagger:0, true, 3");
        weaponSpecific.add("backstab:stone_dagger:0, true, 3");
        weaponSpecific.add("backstab:iron_dagger:0, true, 3");
        weaponSpecific.add("backstab:diamond_dagger:0, true, 3");
        weaponSpecific.add("backstab:gold_dagger:0, true, 3");
        weaponSpecific.add("natura:ghostwood_kama:0, true, 2");
        weaponSpecific.add("natura:bloodwood_kama:0, true, 2");
        weaponSpecific.add("natura:darkwood_kama:0, true, 2");
        weaponSpecific.add("natura:fusewood_kama:0, true, 2");
        weaponSpecific.add("natura:netherquartz_kama:0, true, 2");
        weaponSpecific.add("tetra:duplex_tool_modular:0 > duplex/sickle_left_material & duplex/butt_right_material, true, 2");
        weaponSpecific.add("tconstruct:kama:0, true, 2");
    }
}
