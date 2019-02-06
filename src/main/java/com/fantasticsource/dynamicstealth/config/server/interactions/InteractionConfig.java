package com.fantasticsource.dynamicstealth.config.server.interactions;

import net.minecraftforge.common.config.Config;

public class InteractionConfig
{
    @Config.Name("Attack")
    @Config.Comment("When anything attacks anything else")
    public AttackConfig attack = new AttackConfig();

    @Config.Name("Stealth Attack")
    @Config.Comment("When something attacks an unaware victim")
    public StealthAttackConfig stealthAttack = new StealthAttackConfig();

    @Config.Name("Assassination")
    @Config.Comment("When something kills without being detected")
    public AssassinationConfig assassination = new AssassinationConfig();

    @Config.Name("Desperation")
    @Config.Comment("When something is cornered while fleeing")
    public DesperationConfig desperation = new DesperationConfig();

    @Config.Name("Rally")
    @Config.Comment("When something was fleeing, but regained health")
    public RallyConfig rally = new RallyConfig();

    @Config.Name("Calm Down")
    @Config.Comment("When something was fleeing, but ran out of threat")
    public CalmDownConfig calmDown = new CalmDownConfig();

    @Config.Name("Search")
    @Config.Comment("When something begins to actively search for a not-yet-seen target")
    public SearchConfig search = new SearchConfig();

    @Config.Name("Target Seen")
    @Config.Comment("When something spots a previously unseen target")
    public TargetSeenConfig targetSeen = new TargetSeenConfig();
}
