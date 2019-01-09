package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class Attributes
{
    public static final RangedAttribute VISIBILITY = new RangedAttribute(null, DynamicStealth.MODID + ".visibility", 100, 0, Double.MAX_VALUE);
    public static final RangedAttribute SIGHT = new RangedAttribute(null, DynamicStealth.MODID + ".sight", 100, 0, Double.MAX_VALUE);

    public static final RangedAttribute NOISE = new RangedAttribute(null, DynamicStealth.MODID + ".noise", 100, 0, Double.MAX_VALUE);
    public static final RangedAttribute HEARING = new RangedAttribute(null, DynamicStealth.MODID + ".hearing", 100, 0, Double.MAX_VALUE);

    public static final RangedAttribute SCENT = new RangedAttribute(null, DynamicStealth.MODID + ".scent", 100, 0, Double.MAX_VALUE);
    public static final RangedAttribute SMELLING = new RangedAttribute(null, DynamicStealth.MODID + ".smelling", 100, 0, Double.MAX_VALUE);

    public static final RangedAttribute FEEL = new RangedAttribute(null, DynamicStealth.MODID + ".feel", 100, 0, Double.MAX_VALUE);

    public static void init()
    {
        //This method indirectly initializes the attributes defined above
    }

    public static void addAttributes(EntityLivingBase livingBase)
    {
        //Add new attributes
        livingBase.getAttributeMap().registerAttribute(Attributes.VISIBILITY);
        livingBase.getAttributeMap().registerAttribute(Attributes.SIGHT);

        livingBase.getAttributeMap().registerAttribute(Attributes.NOISE);
        livingBase.getAttributeMap().registerAttribute(Attributes.HEARING);

        livingBase.getAttributeMap().registerAttribute(Attributes.SCENT);
        livingBase.getAttributeMap().registerAttribute(Attributes.SMELLING);

        livingBase.getAttributeMap().registerAttribute(Attributes.FEEL);
    }
}
