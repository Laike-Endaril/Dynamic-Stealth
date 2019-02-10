package com.fantasticsource.dynamicstealth.config.server.interactions;

import net.minecraftforge.common.config.Config;

public class InteractionConfig
{
    @Config.Name("Attack")
    @Config.Comment("When anything attacks anything else")
    public NormalAttackConfig attack = new NormalAttackConfig();

    @Config.Name("Stealth Attack")
    @Config.Comment("When something attacks an unaware victim")
    public StealthAttackConfig stealthAttack = new StealthAttackConfig();

    @Config.Name("Assassination")
    @Config.Comment("When something kills without being detected")
    public AssassinationConfig assassination = new AssassinationConfig();

    @Config.Name("Calm Down")
    @Config.Comment("When something was fleeing, but ran out of threat")
    public CalmDownConfig calmDown = new CalmDownConfig();

    @Config.Name("Rally")
    @Config.Comment("When something was fleeing, but regained health")
    public RallyConfig rally = new RallyConfig();

    @Config.Name("Desperation")
    @Config.Comment("When something is cornered while fleeing")
    public DesperationConfig desperation = new DesperationConfig();

    @Config.Name("Can't Reach")
    @Config.Comment("When something cannot reach its target")
    public CantReachConfig cantReach = new CantReachConfig();
}
