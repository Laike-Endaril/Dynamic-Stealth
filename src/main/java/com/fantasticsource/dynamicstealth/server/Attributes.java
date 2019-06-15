package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class Attributes
{
    public static RangedAttribute THREATGEN = new RangedAttribute(null, DynamicStealth.MODID + ".threatGen", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_TARGET_SPOTTED = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenTargetSpotted", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_ATTACKED_INITIAL = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenAttackedInitial", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_ATTACKED_BY_SAME = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenAttackedBySame", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_ATTACKED_BY_OTHER = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenAttackedByOther", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_DAMAGE_DEALT = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenDamageDealt", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_WARNED = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenWarned", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_ALLY_KILLED = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenAllyKilled", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static RangedAttribute THREATGEN_TARGET_VISIBLE = new RangedAttribute(THREATGEN, DynamicStealth.MODID + ".threatGenTargetVisible", 1, -Double.MAX_VALUE, Double.MAX_VALUE);

    public static RangedAttribute THREATDEG = new RangedAttribute(null, DynamicStealth.MODID + ".threatDeg", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static RangedAttribute THREATDEG_TARGET_NOT_VISIBLE = new RangedAttribute(THREATDEG, DynamicStealth.MODID + ".threatDegTargetNotVisible", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static RangedAttribute THREATDEG_FLEE = new RangedAttribute(THREATDEG, DynamicStealth.MODID + ".threatDegFlee", 1, -Double.MAX_VALUE, Double.MAX_VALUE);


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
        //Add new attributes
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_TARGET_SPOTTED);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_ATTACKED_INITIAL);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_ATTACKED_BY_SAME);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_ATTACKED_BY_OTHER);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_DAMAGE_DEALT);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_WARNED);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_ALLY_KILLED);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATGEN_TARGET_VISIBLE);

        livingBase.getAttributeMap().registerAttribute(Attributes.THREATDEG);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATDEG_TARGET_NOT_VISIBLE);
        livingBase.getAttributeMap().registerAttribute(Attributes.THREATDEG_FLEE);


        livingBase.getAttributeMap().registerAttribute(Attributes.VISIBILITY_REDUCTION);
        livingBase.getAttributeMap().registerAttribute(Attributes.SIGHT);

        livingBase.getAttributeMap().registerAttribute(Attributes.NOISE_REDUCTION);
        livingBase.getAttributeMap().registerAttribute(Attributes.HEARING);

        livingBase.getAttributeMap().registerAttribute(Attributes.SCENT_REDUCTION);
        livingBase.getAttributeMap().registerAttribute(Attributes.SMELLING);
    }
}
