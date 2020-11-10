package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
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
        AbstractAttributeMap attributeMap = livingBase.getAttributeMap();

        attributeMap.registerAttribute(THREATGEN);
        attributeMap.registerAttribute(THREATGEN_SPOTTED);
        attributeMap.registerAttribute(THREATGEN_ATTACK);
        attributeMap.registerAttribute(THREATGEN_DAMAGE_TAKEN);
        attributeMap.registerAttribute(THREATGEN_WARNED_AGAINST);
        attributeMap.registerAttribute(THREATGEN_KILL);
        attributeMap.registerAttribute(THREATGEN_VISIBLE);

        attributeMap.registerAttribute(THREATDEG);
        attributeMap.registerAttribute(THREATDEG_NOT_VISIBLE);
        attributeMap.registerAttribute(THREATDEG_FLEE_FROM);


        attributeMap.registerAttribute(VISIBILITY_REDUCTION);
        attributeMap.registerAttribute(SIGHT);

        attributeMap.registerAttribute(NOISE_REDUCTION);
        attributeMap.registerAttribute(HEARING);

        attributeMap.registerAttribute(SCENT_REDUCTION);
        attributeMap.registerAttribute(SMELLING);
    }
}
