package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class Attributes
{
    public static RangedAttribute THREATGEN = (RangedAttribute) new RangedAttribute(null, DynamicStealth.MODID + ".threatGen", 1, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute THREATGEN_SPOTTED = (RangedAttribute) new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenTargetSpotted", 1, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute THREATGEN_ATTACK = (RangedAttribute) new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenAttackedBySame", 1, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute THREATGEN_DAMAGE_TAKEN = (RangedAttribute) new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenDamageDealt", 1, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute THREATGEN_WARNED_AGAINST = (RangedAttribute) new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenWarned", 1, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute THREATGEN_KILL = (RangedAttribute) new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenAllyKilled", 1, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute THREATGEN_VISIBLE = (RangedAttribute) new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenTargetVisible", 1, 0, Double.MAX_VALUE).setShouldWatch(true);

    public static RangedAttribute THREATDEG = (RangedAttribute) new RangedAttribute(null, DynamicStealth.MODID + ".threatDeg", 1, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute THREATDEG_NOT_VISIBLE = (RangedAttribute) new RangedAttribute(THREATDEG, DynamicStealth.MODID + ".threatDegTargetNotVisible", 1, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute THREATDEG_FLEE_FROM = (RangedAttribute) new RangedAttribute(THREATDEG, DynamicStealth.MODID + ".threatDegFlee", 1, 0, Double.MAX_VALUE).setShouldWatch(true);


    public static RangedAttribute VISIBILITY_REDUCTION = (RangedAttribute) new RangedAttribute(null, DynamicStealth.MODID + ".visibilityReduction", 100, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute SIGHT = (RangedAttribute) new RangedAttribute(null, DynamicStealth.MODID + ".sight", 100, 0, Double.MAX_VALUE).setShouldWatch(true);

    public static RangedAttribute NOISE_REDUCTION = (RangedAttribute) new RangedAttribute(null, DynamicStealth.MODID + ".noiseReduction", 100, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute HEARING = (RangedAttribute) new RangedAttribute(null, DynamicStealth.MODID + ".hearing", 100, 0, Double.MAX_VALUE).setShouldWatch(true);

    public static RangedAttribute SCENT_REDUCTION = (RangedAttribute) new RangedAttribute(null, DynamicStealth.MODID + ".scentReduction", 100, 0, Double.MAX_VALUE).setShouldWatch(true);
    public static RangedAttribute SMELLING = (RangedAttribute) new RangedAttribute(null, DynamicStealth.MODID + ".smelling", 100, 0, Double.MAX_VALUE).setShouldWatch(true);


    public static void init()
    {
        //This method indirectly initializes the attributes defined above
    }


    public static void addAttributes(EntityLivingBase livingBase)
    {
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
