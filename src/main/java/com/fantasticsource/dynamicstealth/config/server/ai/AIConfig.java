package com.fantasticsource.dynamicstealth.config.server.ai;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.dynamicstealth.DynamicStealth.MODID;

public class AIConfig
{
    @Config.Name("Head Turn Speed")
    @Config.LangKey(MODID + ".config.aiHeadSpeed")
    @Config.Comment({"How quickly entities' heads spin during eg. a search sequence"})
    @Config.RangeInt(min = 1, max = 180)
    public int headTurnSpeed = 3;

    @Config.Name("Entity-Specific Settings (Advanced)")
    @Config.LangKey(MODID + ".config.aiEntitySpecific")
    public SpecificAIConfig y_entityOverrides = new SpecificAIConfig();

    @Config.Name("Flee")
    @Config.LangKey(MODID + ".config.aiFlee")
    public FleeConfig flee = new FleeConfig();

    @Config.Name("Can't Reach")
    @Config.LangKey(MODID + ".config.aiCantReach")
    public CantReachTriggerConfig cantReach = new CantReachTriggerConfig();

    @Config.Name("CNPCs Reset in Water")
    @Config.LangKey(MODID + ".config.waterReset")
    @Config.Comment("If enabled, Custom NPCs reset when they touch water")
    public boolean cnpcsResetInWater = false;

    @Config.Name("Prevent Pet Teleport")
    @Config.LangKey(MODID + ".config.aiNoPetTeleport")
    @Config.Comment("If set to true, wolves, cats, and parrots do not teleport while following their owners")
    public boolean preventPetTeleport = true;

    @Config.Name("Add Null Checks to AI")
    @Config.LangKey(MODID + ".config.addNullChecksToAI")
    @Config.Comment(
            {
                    "AI classes to add null checks to (to prevent DS compat-related crashes with AI tasks that don't do null checks)",
                    "",
                    "Syntax is...",
                    "modid, PartialClassName",
                    "",
                    "modid is the exact modid of the mod which contains the crashing AI task class",
                    "",
                    "PartialClassName is at least part of the name of the crashing AI task class.  This is a 'contains' check, so any AI task class containing this string as part of its name will have null checks added to it"
            })
    @Config.RequiresMcRestart
    public String[] addNullChecksToAI = new String[]
            {
                    "lycanitesmobs, com.lycanitesmobs.core.entity.ai.EntityAIAttack",
                    "ancientwarfare, net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIAimVehicle",
                    "thermalfoundation, cofh.thermalfoundation.entity.monster",
                    "abyssalcraft, abyssalcraft.common.entity.ai",
                    "magma_monsters, EntityMagmaMonster",
                    "primitivemobs, AIFlameSpewAttack",
                    "primitivemobs, AIChargeAttack",
                    "emberroot, EntityAIAttackOnCollideAggressive",
                    "defiledlands, EntityScuttler$AISpiderAttack",
                    "defiledlands, EntityAIAttackMeleeStrafe",
                    "rwbym, AIChargeAttack",
                    "rwbym, AIPickAttack",
                    "rwbym, AISweepAttack",
                    "rwbym, EntityAIAttackRange"
            };
}
