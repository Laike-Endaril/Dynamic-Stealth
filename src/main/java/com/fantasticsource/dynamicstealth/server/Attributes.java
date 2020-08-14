package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class Attributes
{
    public static RangedAttribute THREATGEN = new RangedAttribute(null, DynamicStealth.MODID + ".threatGen", 1, 0, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_SPOTTED = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenTargetSpotted", 1, 0, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_ATTACK = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenAttackedBySame", 1, 0, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_DAMAGE_TAKEN = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenDamageDealt", 1, 0, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_WARNED_AGAINST = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenWarned", 1, 0, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_KILL = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenAllyKilled", 1, 0, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_VISIBLE = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenTargetVisible", 1, 0, Double.MAX_VALUE);

    public static RangedAttribute THREATDEG = new RangedAttribute(null, DynamicStealth.MODID + ".threatDeg", 1, 0, Double.MAX_VALUE);
    public static RangedAttribute THREATDEG_NOT_VISIBLE = new RangedAttribute(THREATDEG, DynamicStealth.MODID + ".threatDegTargetNotVisible", 1, 0, Double.MAX_VALUE);
    public static RangedAttribute THREATDEG_FLEE_FROM = new RangedAttribute(THREATDEG, DynamicStealth.MODID + ".threatDegFlee", 1, 0, Double.MAX_VALUE);


    public static RangedAttribute VISIBILITY_REDUCTION = new RangedAttribute(null, DynamicStealth.MODID + ".visibilityReduction", 100, 0, Double.MAX_VALUE);
    public static RangedAttribute SIGHT = new RangedAttribute(null, DynamicStealth.MODID + ".sight", 100, 0, Double.MAX_VALUE);

    public static RangedAttribute NOISE_REDUCTION = new RangedAttribute(null, DynamicStealth.MODID + ".noiseReduction", 100, 0, Double.MAX_VALUE);
    public static RangedAttribute HEARING = new RangedAttribute(null, DynamicStealth.MODID + ".hearing", 100, 0, Double.MAX_VALUE);

    public static RangedAttribute SCENT_REDUCTION = new RangedAttribute(null, DynamicStealth.MODID + ".scentReduction", 100, 0, Double.MAX_VALUE);
    public static RangedAttribute SMELLING = new RangedAttribute(null, DynamicStealth.MODID + ".smelling", 100, 0, Double.MAX_VALUE);


    public static void init()
    {
        //This method indirectly initializes the attributes defined above
    }


    public static void addAttributes(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return;

        //Add new attributes
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_SPOTTED);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_ATTACK);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_DAMAGE_TAKEN);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_WARNED_AGAINST);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_KILL);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_VISIBLE);

        livingBase.getAttributeMap().registerAttribute(Attributes.THREATDEG);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATDEG_NOT_VISIBLE);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATDEG_FLEE_FROM);


        livingBase.getAttributeMap().registerAttribute(Attributes.VISIBILITY_REDUCTION);
        livingBase.getAttributeMap().registerAttribute(Attributes.SIGHT);

        livingBase.getAttributeMap().registerAttribute(Attributes.NOISE_REDUCTION);
        livingBase.getAttributeMap().registerAttribute(Attributes.HEARING);

        livingBase.getAttributeMap().registerAttribute(Attributes.SCENT_REDUCTION);
        livingBase.getAttributeMap().registerAttribute(Attributes.SMELLING);
    }
}
