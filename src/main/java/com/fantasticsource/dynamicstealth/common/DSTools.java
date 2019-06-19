package com.fantasticsource.dynamicstealth.common;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;

public class DSTools
{
    public static Vec3d[] entityCheckVectors(Entity target)
    {
        double halfWidth = target.width / 2;
        double halfHeight = target.height / 2;

        double x = target.posX;
        double y = target.posY + halfHeight;
        double z = target.posZ;

        return new Vec3d[]
                {
                        new Vec3d(x, y, z), //Center
                        new Vec3d(x, y + halfHeight, z), //+Y
                        new Vec3d(x, y - halfHeight, z), //-Y
                        new Vec3d(x + halfWidth, y, z), //+X
                        new Vec3d(x - halfWidth, y, z), //-X
                        new Vec3d(x, y, z + halfWidth), //+Z
                        new Vec3d(x, y, z - halfWidth) //-Z
                };
    }

    public static HashSet<BlockPos> entityCheckBlocks(Entity target)
    {
        HashSet<BlockPos> result = new HashSet<>();
        for (Vec3d vec : entityCheckVectors(target))
        {
            result.add(new BlockPos(vec));
        }
        System.out.println(result.size());
        return result;
    }
}
