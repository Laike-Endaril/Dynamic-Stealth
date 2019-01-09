package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class Attributes
{
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
        livingBase.getAttributeMap().registerAttribute(Attributes.VISIBILITY_REDUCTION);
        livingBase.getAttributeMap().registerAttribute(Attributes.SIGHT);

        livingBase.getAttributeMap().registerAttribute(Attributes.NOISE_REDUCTION);
        livingBase.getAttributeMap().registerAttribute(Attributes.HEARING);

        livingBase.getAttributeMap().registerAttribute(Attributes.SCENT_REDUCTION);
        livingBase.getAttributeMap().registerAttribute(Attributes.SMELLING);
    }
}
