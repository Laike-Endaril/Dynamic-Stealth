package com.fantasticsource.dynamicstealth.server.senses;

import java.util.ArrayList;

public class EntitySightDefaults
{
    public static ArrayList<String> naturallyBrightDefaults = new ArrayList<>();

    static
    {
        naturallyBrightDefaults.add("blaze");
        naturallyBrightDefaults.add("magma_cube");

        //Compat; these should be added absolutely, not conditionally
        //TODO add glowy lycanite stuff, like cinders, wisps, etc...
    }
}
