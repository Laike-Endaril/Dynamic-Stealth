package com.fantasticsource.dynamicstealth.server.senses.sight;

import java.util.ArrayList;

public class EntitySightDefaults
{
    public static ArrayList<String> naturallyBrightDefaults = new ArrayList<>();

    static
    {
        naturallyBrightDefaults.add("blaze");
        naturallyBrightDefaults.add("magma_cube");
        naturallyBrightDefaults.add("ender_dragon");
        naturallyBrightDefaults.add("wither");

        //Compat; these should be added absolutely, not conditionally
        naturallyBrightDefaults.add("infernomobs:khalk");
        naturallyBrightDefaults.add("infernomobs:afrit");
        naturallyBrightDefaults.add("infernomobs:lobber");
        naturallyBrightDefaults.add("infernomobs:gorger");
        naturallyBrightDefaults.add("infernomobs:salamander");
        naturallyBrightDefaults.add("infernomobs:cephignis");
        naturallyBrightDefaults.add("infernomobs:ignibus");
        naturallyBrightDefaults.add("demonmobs:rahovart");
        naturallyBrightDefaults.add("elementalmobs:nymph");
        naturallyBrightDefaults.add("elementalmobs:zephyr");
        naturallyBrightDefaults.add("elementalmobs:wisp");
        naturallyBrightDefaults.add("elementalmobs:cinder");
        naturallyBrightDefaults.add("elementalmobs:banshee");
        naturallyBrightDefaults.add("elementalmobs:aegis");
        naturallyBrightDefaults.add("elementalmobs:sylph");
        naturallyBrightDefaults.add("elementalmobs:xaphan");
    }
}
