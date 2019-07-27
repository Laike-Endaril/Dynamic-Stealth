package com.fantasticsource.dynamicstealth.config.server.threat;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class ThreatConfig
{
    @Config.Name("Entity-Specific Settings (Advanced)")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatEntitySpecific")
    public SpecificThreatConfig y_entityOverrides = new SpecificThreatConfig();

    @Config.Name("CNPC Threat Settings")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatCNPC")
    public CNPCThreatConfig cnpcThreatConfig = new CNPCThreatConfig();

    @Config.Name("000 Bypass Threat System (Global)")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatBypassGlobal")
    @Config.Comment({"If enabled, all entities should bypass the threat system"})
    public boolean bypassThreatSystem = false;

    @Config.Name("010 'Start of Combat' Threat")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatCombatStart")
    @Config.Comment(
            {
                    "When an out-of-combat entity first enters combat, its threat is set to this",
                    "This can happen from an entity seeing a valid target to attack or from an entity taking damage while out of combat"
            })
    @Config.RangeDouble(min = 0)
    public double combatStart = 30;

    @Config.Name("015 Initial Attack Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatInitialAttack")
    @Config.Comment({"When an out-of-combat entity is attacked, its threat is set to the damage taken times this, divided by its max HP"})
    @Config.RangeDouble(min = 0)
    public double attackedInitialMultiplier = 400;

    @Config.Name("020 Dealt Damage Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatDealtDamage")
    @Config.Comment({"When an in-combat entity damages its current target, its threat is increased by the damage dealt times this, divided by its target's max HP"})
    @Config.RangeDouble(min = 0)
    public double damageDealtMultiplier = 200;

    @Config.Name("025 'Attacked By Same' Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatAttackedBySame")
    @Config.Comment({"When an in-combat entity is attacked by its current target, its threat is increased by the damage taken times this, divided by its max HP"})
    @Config.RangeDouble(min = 0)
    public double attackedBySameMultiplier = 400;

    @Config.Name("030 'Attacked By Other' Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatAttackedByOther")
    @Config.Comment({"When an in-combat entity is attacked by something that is *not* its current target, its threat is decreased by damage taken times this, divided by its max HP"})
    @Config.RangeDouble(min = 0)
    public double attackedByOtherMultiplier = 400;

    @Config.Name("035 'Warned' Threat")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatWarned")
    @Config.Comment("When an entity heeds a warning from another entity, its threat is set to this (if less than this)")
    @Config.RangeDouble(min = 0)
    public double warnedThreat = 30;

    @Config.Name("040 'Ally Killed' Threat")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatAllyKilled")
    @Config.Comment({"When an entity sees an ally die, increase threat"})
    @Config.RangeDouble(min = 0)
    public double allyKilledThreat = 100;

    @Config.Name("060 Seen Target Threat Rate")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatSeenRate")
    @Config.Comment({"Every time an entity updates and their target is visible and reachable, this is added to their threat"})
    @Config.RangeDouble(min = 0)
    public double seenTargetThreatRate = 0.1;

    @Config.Name("065 Unseen Target Degredation Rate")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatUnseenRate")
    @Config.Comment({"Every time an entity updates and their target's position is unknown, this is subtracted from their threat"})
    @Config.RangeDouble(min = 0)
    public double unseenTargetDegredationRate = 0.1;

    @Config.Name("070 Flee Degredation Rate")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatFleeRate")
    @Config.Comment("How fast the threat gauge decreases while fleeing; lower number means they flee for a longer time")
    @Config.RangeDouble(min = 0)
    public double fleeDegredationRate = 0.3;

    @Config.Name("075 Owned Can't Reach Degredation Rate")
    @Config.LangKey(DynamicStealth.MODID + ".config.threatCantReachRate")
    @Config.Comment({"Every time an owned entity updates and can't reach their target, this is subtracted from their threat"})
    @Config.RangeDouble(min = 0)
    public double ownedCantReachDegredationRate = 0.5;
}
