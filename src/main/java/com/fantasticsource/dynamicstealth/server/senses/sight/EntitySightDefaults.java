package com.fantasticsource.dynamicstealth.server.senses.sight;

import java.util.ArrayList;

public class EntitySightDefaults
{
    public static ArrayList<String> naturallyBrightDefaults = new ArrayList<>();
    public static ArrayList<String> naturalNightvisionDefaults = new ArrayList<>();
    public static ArrayList<String> naturalSoulSightDefaults = new ArrayList<>();
    public static ArrayList<String> angleDefaults = new ArrayList<>();
    public static ArrayList<String> distanceDefaults = new ArrayList<>();

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


        angleDefaults.add("player, 70, 0");
        angleDefaults.add("ghast, 90, 0");
        angleDefaults.add("wither, 90, 0");


        distanceDefaults.add("player, 50, 5");
        distanceDefaults.add("ghast, 50, 20");
        distanceDefaults.add("ender_dragon, 100, 60");
        distanceDefaults.add("wither, 100, 30");


        //Compat; these should be added absolutely, not conditionally


        naturallyBrightDefaults.add("infernomobs:khalk");
        naturallyBrightDefaults.add("lycanitesmobsmobs:khalk");
        naturallyBrightDefaults.add("shadowmobs:phantom");
        naturallyBrightDefaults.add("lycanitesmobsmobs:phantom");
        naturallyBrightDefaults.add("infernomobs:afrit");
        naturallyBrightDefaults.add("lycanitesmobsmobs:afrit");
        naturallyBrightDefaults.add("infernomobs:lobber");
        naturallyBrightDefaults.add("lycanitesmobsmobs:lobber");
        naturallyBrightDefaults.add("infernomobs:gorger");
        naturallyBrightDefaults.add("lycanitesmobsmobs:gorger");
        naturallyBrightDefaults.add("infernomobs:salamander");
        naturallyBrightDefaults.add("lycanitesmobsmobs:salamander");
        naturallyBrightDefaults.add("infernomobs:cephignis");
        naturallyBrightDefaults.add("lycanitesmobsmobs:cephignis");
        naturallyBrightDefaults.add("infernomobs:ignibus");
        naturallyBrightDefaults.add("lycanitesmobsmobs:ignibus");
        naturallyBrightDefaults.add("demonmobs:rahovart");
        naturallyBrightDefaults.add("lycanitesmobsmobs:rahovart");
        naturallyBrightDefaults.add("elementalmobs:nymph");
        naturallyBrightDefaults.add("lycanitesmobsmobs:nymph");
        naturallyBrightDefaults.add("elementalmobs:zephyr");
        naturallyBrightDefaults.add("lycanitesmobsmobs:zephyr");
        naturallyBrightDefaults.add("elementalmobs:wisp");
        naturallyBrightDefaults.add("lycanitesmobsmobs:wisp");
        naturallyBrightDefaults.add("elementalmobs:cinder");
        naturallyBrightDefaults.add("lycanitesmobsmobs:cinder");
        naturallyBrightDefaults.add("elementalmobs:banshee");
        naturallyBrightDefaults.add("lycanitesmobsmobs:banshee");
        naturallyBrightDefaults.add("elementalmobs:aegis");
        naturallyBrightDefaults.add("lycanitesmobsmobs:aegis");
        naturallyBrightDefaults.add("elementalmobs:sylph");
        naturallyBrightDefaults.add("lycanitesmobsmobs:sylph");
        naturallyBrightDefaults.add("elementalmobs:xaphan");
        naturallyBrightDefaults.add("lycanitesmobsmobs:xaphan");
        naturallyBrightDefaults.add("lycanitesmobsmobs:volcan");
        naturallyBrightDefaults.add("harvestersnight:harvester");
        naturallyBrightDefaults.add("sanity:ghost_zombie");


        naturalNightvisionDefaults.add("nex:ghast_queen");
        naturalNightvisionDefaults.add("defiledlands:the_destroyer");
        naturalNightvisionDefaults.add("demonmobs:rahovart");
        naturalNightvisionDefaults.add("lycanitesmobsmobs:rahovart");
        naturalNightvisionDefaults.add("demonmobs:asmodeus");
        naturalNightvisionDefaults.add("lycanitesmobsmobs:asmodeus");


        naturalSoulSightDefaults.add("elementalmobs:banshee");
        naturalSoulSightDefaults.add("lycanitesmobsmobs:banshee");
        naturalSoulSightDefaults.add("infernomobs:gorger");
        naturalSoulSightDefaults.add("lycanitesmobsmobs:gorger");
        naturalSoulSightDefaults.add("harvestersnight:harvester");
        naturalSoulSightDefaults.add("defiledlands:the_mourner");


        angleDefaults.add("nex:ghast_queen, 90, 0");


        distanceDefaults.add("nex:ghast_queen, 100, 30");
    }
}
