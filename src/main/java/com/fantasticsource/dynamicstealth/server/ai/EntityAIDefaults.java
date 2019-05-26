package com.fantasticsource.dynamicstealth.server.ai;

import java.util.ArrayList;

public class EntityAIDefaults
{
    public static ArrayList<String> fearlessDefaults = new ArrayList<>();

    static
    {
        fearlessDefaults.add("player");
        fearlessDefaults.add("zombie");
        fearlessDefaults.add("zombie_villager");
        fearlessDefaults.add("husk");
        fearlessDefaults.add("skeleton");
        fearlessDefaults.add("stray");
        fearlessDefaults.add("wither_skeleton");
        fearlessDefaults.add("creeper");
        fearlessDefaults.add("ghast");
        fearlessDefaults.add("slime");
        fearlessDefaults.add("magma_cube");
        fearlessDefaults.add("enderman");
        fearlessDefaults.add("ender_dragon");
        fearlessDefaults.add("wither");
        fearlessDefaults.add("skeleton_horse");
        fearlessDefaults.add("zombie_horse");


        //Compat; these should be added absolutely, not conditionally
        fearlessDefaults.add("harvestersnight:harvester");
        fearlessDefaults.add("ebwizardry:skeleton_minion");
        fearlessDefaults.add("ebwizardry:spirit_wolf");
        fearlessDefaults.add("ebwizardry:ice_wraith");
        fearlessDefaults.add("ebwizardry:lightning_wraith");
        fearlessDefaults.add("ebwizardry:shadow_wraith");
        fearlessDefaults.add("ebwizardry:magic_slime");
        fearlessDefaults.add("ebwizardry:spirit_horse");
        fearlessDefaults.add("ebwizardry:phoenix");
        fearlessDefaults.add("ebwizardry:storm_elemental");
        fearlessDefaults.add("ebwizardry:wither_skeleton_minion");
    }
}
