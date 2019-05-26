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


        //Compat; these should be added absolutely, not conditionally
        fearlessDefaults.add("harvestersnight:harvester");
    }
}
