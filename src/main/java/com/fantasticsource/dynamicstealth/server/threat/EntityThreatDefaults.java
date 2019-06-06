package com.fantasticsource.dynamicstealth.server.threat;

import java.util.ArrayList;

public class EntityThreatDefaults
{
    public static ArrayList<String> threatBypassDefaults = new ArrayList<>();
    public static ArrayList<String> passiveDefaults = new ArrayList<>();

    static
    {
        passiveDefaults.add("shulker, false");
        passiveDefaults.add("snowman, false");

        threatBypassDefaults.add("player");
        threatBypassDefaults.add("slime");
        threatBypassDefaults.add("magma_cube");
        threatBypassDefaults.add("ender_dragon");


        //Compat; these should be added absolutely, not conditionally
        passiveDefaults.add("ebwizardry:wizard, false");
        passiveDefaults.add("techguns:turret, false");
        passiveDefaults.add("rafradek_tf2_weapons:sentry, false");
        passiveDefaults.add("ghast, false");
        passiveDefaults.add("nex:ghastling, false");
        passiveDefaults.add("nex:ghast_queen, false");
        passiveDefaults.add("nex:ghastling, false");

        threatBypassDefaults.add("dissolution:player_corpse");
        threatBypassDefaults.add("millenaire:genericvillager");
        threatBypassDefaults.add("millenaire:genericsimmfemale");
        threatBypassDefaults.add("millenaire:genericasimmfemale");
        threatBypassDefaults.add("tconstruct:blueslime");
        threatBypassDefaults.add("primitivemobs:treasure_slime");
        threatBypassDefaults.add("pvj:pvj_icecube");
        threatBypassDefaults.add("defiledlands:slime_defiled");
    }
}
