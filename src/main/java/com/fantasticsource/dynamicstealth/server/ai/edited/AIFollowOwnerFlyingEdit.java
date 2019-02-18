package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.BlockPos;

public class AIFollowOwnerFlyingEdit extends AIFollowOwnerEdit
{
    public AIFollowOwnerFlyingEdit(EntityTameable tameable, EntityAIFollowOwner oldAI)
    {
        super(tameable, oldAI);
    }

    protected boolean isTeleportFriendlyBlock(int x, int y, int z)
    {
        IBlockState iblockstate = world.getBlockState(new BlockPos(x, y - 1, z));
        return (iblockstate.isTopSolid() || iblockstate.getMaterial() == Material.LEAVES) && world.isAirBlock(new BlockPos(x, y, z)) && world.isAirBlock(new BlockPos(x, y + 1, z));
    }
}