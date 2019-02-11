package com.fantasticsource.dynamicstealth.server.senses.sight;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LOS
{
    private static final int MAX_ITERATIONS = 200;

    //Fixing this so it doesn't return null when going through diagonal walls in the +X direction (the original version of this in World.java is the reason zombies can hit you through diagonal walls in vanilla)
    //The current version of this should only ever be used as a boolean return (not to return the last block or w/e) because it reverses the vector direction in half of the cases
    //Yes I'm lazy, I know
    public static boolean rayTraceBlocks(World world, Vec3d vec, Vec3d vecEnd, boolean collideOnAllSolids)
    {
        if (vec.x < vecEnd.x)
        {
            Vec3d swap = vec;
            vec = vecEnd;
            vecEnd = swap;
        }

        int x = MathHelper.floor(vec.x);
        int y = MathHelper.floor(vec.y);
        int z = MathHelper.floor(vec.z);

        int endX = MathHelper.floor(vecEnd.x);
        int endY = MathHelper.floor(vecEnd.y);
        int endZ = MathHelper.floor(vecEnd.z);

        //Check starting block
        BlockPos blockPos = new BlockPos(x, y, z);
        if (!world.isBlockLoaded(blockPos)) return false;


        IBlockState blockState = world.getBlockState(blockPos);
        if ((collideOnAllSolids || !canSeeThrough(blockState)) && blockState.getCollisionBoundingBox(world, blockPos) != Block.NULL_AABB)
        {
            if (blockState.collisionRayTrace(world, blockPos, vec, vecEnd) != null) return false;
        }

        //Iterate through all non-starting blocks and check them
        for (int i = 1; i <= MAX_ITERATIONS; i++)
        {
            if (x == endX && y == endY && z == endZ) return true;

            boolean xMotion = true;
            boolean yMotion = true;
            boolean zMotion = true;

            double x2 = 999;
            double y2 = 999;
            double z2 = 999;

            if (endX > x) x2 = x + 1;
            else if (endX < x) x2 = x;
            else xMotion = false;

            if (endY > y) y2 = y + 1;
            else if (endY < y) y2 = y;
            else yMotion = false;

            if (endZ > z) z2 = z + 1;
            else if (endZ < z) z2 = z;
            else zMotion = false;

            double x3 = 999;
            double y3 = 999;
            double z3 = 999;

            double xDif = vecEnd.x - vec.x;
            double yDif = vecEnd.y - vec.y;
            double zDif = vecEnd.z - vec.z;

            if (xMotion) x3 = (x2 - vec.x) / xDif;
            if (yMotion) y3 = (y2 - vec.y) / yDif;
            if (zMotion) z3 = (z2 - vec.z) / zDif;

            //Make sure they wanted to use -0.0001 here (they were using -1.0E-4D, which is -0.0001)
            if (x3 == 0) x3 = -0.0001;
            if (y3 == 0) y3 = -0.0001;
            if (z3 == 0) z3 = -0.0001;

            EnumFacing enumfacing;

            if (x3 < y3 && x3 < z3)
            {
                enumfacing = endX > x ? EnumFacing.WEST : EnumFacing.EAST;
                vec = new Vec3d(x2, vec.y + yDif * x3, vec.z + zDif * x3);
            }
            else if (y3 < z3)
            {
                enumfacing = endY > y ? EnumFacing.DOWN : EnumFacing.UP;
                vec = new Vec3d(vec.x + xDif * y3, y2, vec.z + zDif * y3);
            }
            else
            {
                enumfacing = endZ > z ? EnumFacing.NORTH : EnumFacing.SOUTH;
                vec = new Vec3d(vec.x + xDif * z3, vec.y + yDif * z3, z2);
            }

            x = MathHelper.floor(vec.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            y = MathHelper.floor(vec.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
            z = MathHelper.floor(vec.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);

            blockPos = new BlockPos(x, y, z);
            if (!world.isBlockLoaded(blockPos)) return false;

            blockState = world.getBlockState(blockPos);
            if ((collideOnAllSolids || !canSeeThrough(blockState)) && blockState.getCollisionBoundingBox(world, blockPos) != Block.NULL_AABB)
            {
                if (blockState.collisionRayTrace(world, blockPos, vec, vecEnd) != null) return false;
            }
        }

        return false; //Too far to see
    }

    public static boolean canSeeThrough(IBlockState blockState)
    {
        Material material = blockState.getMaterial();

        if (material == Material.LEAVES) return true;
        if (material == Material.GLASS) return true;
        if (material == Material.ICE) return true;

        //These don't usually matter due to the ignoreBlockWithoutBoundingBox thing, but here they are anyway, just in case
        if (material == Material.AIR) return true;
        if (material == Material.WATER) return true;
        if (material == Material.FIRE) return true;
        if (material == Material.PORTAL) return true;
        if (material == Material.BARRIER) return true;
        if (material == Material.PLANTS) return true;
        if (material == Material.WEB) return true;
        if (material == Material.VINE) return true;


        Block block = blockState.getBlock();

        //Special blocks types that don't follow the rules
        if (block instanceof BlockSlime) return true;
        if (block instanceof BlockTrapDoor) return true;
        if (block instanceof BlockFence) return true;
        if (block instanceof BlockFenceGate) return true;

        //Special blocks that don't follow the rules
        if (block == Blocks.ACACIA_DOOR) return true;
        if (block == Blocks.JUNGLE_DOOR) return true;

        //Honeybadger blocks :/
        if (block == Blocks.OAK_DOOR || block == Blocks.IRON_DOOR)
        {
            if ((block.getMetaFromState(blockState) & 8) != 0) return true;
        }

        return false;
    }
}
