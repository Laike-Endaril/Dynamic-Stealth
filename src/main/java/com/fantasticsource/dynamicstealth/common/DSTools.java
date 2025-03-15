package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightData;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

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

    //How much the position is exposed to sky light; not actual sky light level, and certainly not exposure to weather as this goes through glass
    //Inherently a 0-15 value, as it pulls data from a nibble array
    public static int vanillaSkyLightExposure(World world, BlockPos pos)
    {
        if (world.isRemote) throw new IllegalStateException("Light levels should only be accessed from server-side!");

        Chunk chunk = world.getChunkFromBlockCoords(pos);
        int y = pos.getY();
        if (y > world.getHeight()) return 15;

        if (y < 0)
        {
            y = 0;
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
        }
        if (!world.isAreaLoaded(pos, 1)) return 0;

        ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[y >> 4];
        return extendedblockstorage.getSkyLight(pos.getX() & 15, y & 15, pos.getZ() & 15);
    }

    public static int adjustedSkyLightLevelTotal(World world, BlockPos pos)
    {
        if (!world.provider.hasSkyLight()) return 0;

        int dim = world.provider.getDimension();


        double sunlight = EntitySightData.dimensionSunlight(dim);
        if (sunlight == -1) sunlight = world.provider.hasSkyLight() ? 15 : 0;

        double maxMoonlight = EntitySightData.maximumDimensionMoonlight(dim);
        if (maxMoonlight == -1) maxMoonlight = world.provider.hasSkyLight() ? 7 : 0;

        double moonlight = EntitySightData.minimumDimensionMoonlight(dim);
        if (moonlight == -1) moonlight = world.provider.hasSkyLight() ? 2 : 0;
        moonlight = moonlight + world.getCurrentMoonPhaseFactor() * (maxMoonlight - moonlight);


        double sunRatio = world.provider.getSunBrightnessFactor(1);
        double skyLightRatio = sunlight * sunRatio + moonlight * (1d - sunRatio);
        skyLightRatio *= vanillaSkyLightExposure(world, pos) / 15d;

        int skyLight = (int) Math.round(skyLightRatio);
        if (skyLight < 0) skyLight = 0;
        if (skyLight > 15) skyLight = 15;
        return skyLight;
    }

    public static int vanillaBlockLightLevelTotal(World world, BlockPos pos)
    {
        if (world.isRemote) throw new IllegalStateException("Light levels should only be accessed from server-side!");

        if (!world.isAreaLoaded(pos, 1)) return 0;

        Chunk chunk = world.getChunkFromBlockCoords(pos);
        ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[pos.getY() >> 4];
        return extendedblockstorage.getBlockLight(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    public static int adjustedBlockLightLevelTotal(World world, BlockPos pos)
    {
        return vanillaBlockLightLevelTotal(world, pos);
    }

    public static int adjustedLightLevelTotal(World world, Vec3d vec)
    {
        return adjustedLightLevelTotal(world, new BlockPos(vec));
    }

    public static int adjustedLightLevelTotal(World world, BlockPos pos)
    {
        return Tools.max(adjustedSkyLightLevelTotal(world, pos), adjustedBlockLightLevelTotal(world, pos), EntitySightData.minimumDimensionLight(world.provider.getDimension()));
    }

    public static int entityLightLevel(Entity target)
    {
        if (target.world.isRemote) throw new IllegalStateException("Light levels should only be accessed from server-side!");

        if (EntitySightData.isBright(target)) return 15;

        int result = 0;
        for (BlockPos pos : entityCheckBlocks(target))
        {
            result = Tools.max(result, adjustedLightLevelTotal(target.world, pos));
            if (result == 15) return result;
        }
        return result;
    }
}
