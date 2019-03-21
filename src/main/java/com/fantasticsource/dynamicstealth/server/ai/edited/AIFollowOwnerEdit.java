package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class AIFollowOwnerEdit extends EntityAIBase
{
    private final EntityTameable tameable;
    private final PathNavigate petPathfinder;
    World world;
    private float maxDist;
    private float minDist;
    private EntityLivingBase owner;
    private double followSpeed;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public AIFollowOwnerEdit(EntityTameable tameableIn, EntityAIFollowOwner oldAI)
    {
        tameable = tameableIn;
        world = tameableIn.world;
        petPathfinder = tameableIn.getNavigator();

        followSpeed = oldAI.followSpeed;
        minDist = oldAI.minDist;
        maxDist = oldAI.maxDist;

        setMutexBits(3);
    }

    public boolean shouldExecute()
    {
        owner = tameable.getOwner();

        if (owner == null || tameable.isSitting()) return false;
        if (owner instanceof EntityPlayer && ((EntityPlayer) owner).isSpectator()) return false;
        if (tameable.getDistanceSq(owner) < minDist * minDist) return false;

        return true;
    }

    public boolean shouldContinueExecuting()
    {
        return !petPathfinder.noPath() && tameable.getDistanceSq(owner) > (double) (maxDist * maxDist) && !tameable.isSitting();
    }

    public void startExecuting()
    {
        timeToRecalcPath = 0;
        oldWaterCost = tameable.getPathPriority(PathNodeType.WATER);
        tameable.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    public void resetTask()
    {
        owner = null;
        petPathfinder.clearPath();
        tameable.setPathPriority(PathNodeType.WATER, oldWaterCost);
    }

    public void updateTask()
    {
        tameable.getLookHelper().setLookPositionWithEntity(owner, 10, tameable.getVerticalFaceSpeed());

        if (!tameable.isSitting())
        {
            if (--timeToRecalcPath <= 0)
            {
                timeToRecalcPath = 10;

                if (!petPathfinder.tryMoveToEntityLiving(owner, followSpeed))
                {
                    if (!tameable.getLeashed() && !tameable.isRiding() && !serverSettings.ai.preventPetTeleport)
                    {
                        if (tameable.getDistanceSq(owner) >= 144)
                        {
                            int x = MathHelper.floor(owner.posX) - 2;
                            int z = MathHelper.floor(owner.posZ) - 2;
                            int y = MathHelper.floor(owner.getEntityBoundingBox().minY);

                            for (int xOffset = 0; xOffset <= 4; ++xOffset)
                            {
                                for (int zOffset = 0; zOffset <= 4; ++zOffset)
                                {
                                    if ((xOffset < 1 || zOffset < 1 || xOffset > 3 || zOffset > 3) && isTeleportFriendlyBlock(x + xOffset, y, z + zOffset))
                                    {
                                        tameable.setLocationAndAngles((double) (x + xOffset) + 0.5, y, (double) (z + zOffset) + 0.5, tameable.rotationYaw, tameable.rotationPitch);
                                        petPathfinder.clearPath();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean isTeleportFriendlyBlock(int x, int y, int z)
    {
        BlockPos blockpos = new BlockPos(x, y - 1, z);
        IBlockState iblockstate = world.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(tameable) && world.isAirBlock(blockpos.up()) && world.isAirBlock(blockpos.up(2));
    }
}