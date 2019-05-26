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
        fearlessDefaults.add("blaze");


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
        fearlessDefaults.add("emberroot:rainbowslime");
        fearlessDefaults.add("emberroot:rainbow_golem");
        fearlessDefaults.add("emberroot:hero");
        fearlessDefaults.add("emberroot:creeper");
        fearlessDefaults.add("emberroot:slime");
        fearlessDefaults.add("emberroot:dire_wolf");
        fearlessDefaults.add("emberroot:withercat");
        fearlessDefaults.add("emberroot:enderminy");
        fearlessDefaults.add("emberroot:knight_fallen");
        fearlessDefaults.add("emberroot:fallenmount");
        fearlessDefaults.add("emberroot:rootsonespriteboss");
        fearlessDefaults.add("emberroot:skeleton_frozen");
        fearlessDefaults.add("endreborn:endguard");
        fearlessDefaults.add("endreborn:watcher");
        fearlessDefaults.add("endreborn:endlord");
        fearlessDefaults.add("endreborn:angry_enderman");
        fearlessDefaults.add("endreborn:chronologist");
        fearlessDefaults.add("thermalfoundation:blizz");
        fearlessDefaults.add("thermalfoundation:blitz");
        fearlessDefaults.add("thermalfoundation:basalz");
        fearlessDefaults.add("goblinencounter:goblinking");
        fearlessDefaults.add("nex:gold_golem");
        fearlessDefaults.add("nex:wight");
        fearlessDefaults.add("nex:spinout");
        fearlessDefaults.add("nex:spore_creeper");
        fearlessDefaults.add("nex:ghastling");
        fearlessDefaults.add("nex:bone_spider");
        fearlessDefaults.add("nex:ghast_queen");
        fearlessDefaults.add("nethergoldplus:zombiepigmanwarrior");
        fearlessDefaults.add("primitivemobs:treasure_slime");
        fearlessDefaults.add("primitivemobs:haunted_tool");
        fearlessDefaults.add("primitivemobs:bewitched_tome");
        fearlessDefaults.add("primitivemobs:brain_slime");
        fearlessDefaults.add("primitivemobs:rocket_creeper");
        fearlessDefaults.add("primitivemobs:festive_creeper");
        fearlessDefaults.add("primitivemobs:support_creeper");
        fearlessDefaults.add("primitivemobs:skeleton_warrior");
        fearlessDefaults.add("primitivemobs:blazing_juggernaut");
        fearlessDefaults.add("primitivemobs:void_eye");
    }
}
