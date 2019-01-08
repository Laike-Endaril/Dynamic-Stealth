package com.fantasticsource.dynamicstealth.server.configdata;

import java.util.ArrayList;

public class EntityThreatDefaults
{
    public static ArrayList<String> threatBypassDefaults = new ArrayList<>();

    static
    {
        threatBypassDefaults.add("player");
        threatBypassDefaults.add("slime");
        threatBypassDefaults.add("ender_dragon");

        //Compat; these should be added absolutely, not conditionally
        threatBypassDefaults.add("rafradek_tf2_weapons:medic");
    }
}
