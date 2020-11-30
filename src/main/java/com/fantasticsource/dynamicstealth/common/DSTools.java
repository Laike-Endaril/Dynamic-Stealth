package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightData;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;

public class DSTools
{
    public static Vec3d[] entityCheckVectors(Entity target)
    {
        //Not exactly half because MC doesn't bound entities correctly when colliding with solid blocks... :/
        double halfWidth = target.width * 0.499;
        double halfHeight = target.height * 0.499;

        double x = target.posX;
        double y = target.posY + target.height / 2;
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
        return result;
    }

    public static int lightLevelTotal(World world, Vec3d vec)
    {
        return lightLevelTotal(world, new BlockPos(vec));
    }

    public static int entityLightLevel(Entity target)
    {
        if (target.world.isRemote) throw new IllegalStateException("Light levels should only be accessed from server-side!");

        if (target.isBurning() && DynamicStealthConfig.serverSettings.senses.sight.g_absolutes.seeBurning) return 15;

        int result = 0;
        for (BlockPos pos : entityCheckBlocks(target))
        {
            result = Tools.max(result, lightLevelTotal(target.world, pos));
            if (result == 15) return result;
        }
        return result;
    }

    public static int lightLevelTotal(World world, BlockPos pos)
    {
        if (world.isRemote) throw new IllegalStateException("Light levels should only be accessed from server-side!");

        if (!world.isAreaLoaded(pos, 1)) return 0;

        return Tools.max(world.getLightFromNeighbors(pos), EntitySightData.minimumDimensionLight(world.provider.getDimension()));
    }
}
