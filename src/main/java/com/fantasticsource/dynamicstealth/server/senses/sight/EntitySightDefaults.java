package com.fantasticsource.dynamicstealth.server.senses.sight;

import java.util.ArrayList;

public class EntitySightDefaults
{
    public static ArrayList<String> naturallyBrightDefaults = new ArrayList<>();
    public static ArrayList<String> naturalNightvisionDefaults = new ArrayList<>();
    public static ArrayList<String> naturalSoulSightDefaults = new ArrayList<>();

    static
    {
        naturallyBrightDefaults.add("blaze");
        naturallyBrightDefaults.add("magma_cube");
        naturallyBrightDefaults.add("ender_dragon");
        naturallyBrightDefaults.add("wither");


        naturalNightvisionDefaults.add("squid");
        naturalNightvisionDefaults.add("guardian");
        naturalNightvisionDefaults.add("elder_guardian");
        naturalNightvisionDefaults.add("cow");
        naturalNightvisionDefaults.add("mooshroom");
        naturalNightvisionDefaults.add("ocelot");
        naturalNightvisionDefaults.add("wolf");
        naturalNightvisionDefaults.add("polar_bear");
        naturalNightvisionDefaults.add("silverfish");
        naturalNightvisionDefaults.add("endermite");
        naturalNightvisionDefaults.add("enderman");
        naturalNightvisionDefaults.add("ender_dragon");
        naturalNightvisionDefaults.add("wither");
        naturalNightvisionDefaults.add("vex");
        naturalNightvisionDefaults.add("ghast");


        naturalSoulSightDefaults.add("ender_dragon");
        naturalSoulSightDefaults.add("vex");


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
        naturallyBrightDefaults.add("harvestersnight:harvester");


        naturalSoulSightDefaults.add("harvestersnight:harvester");
        naturalSoulSightDefaults.add("defiledlands:the_destroyer");
        naturalSoulSightDefaults.add("defiledlands:the_mourner");
        naturalSoulSightDefaults.add("demonmobs:rahovart");
        naturalSoulSightDefaults.add("demonmobs:asmodeus");

        naturalSoulSightDefaults.add("elementalmobs:banshee");
        naturalSoulSightDefaults.add("infernomobs:gorger");
    }
}
