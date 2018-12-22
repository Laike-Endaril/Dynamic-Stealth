package com.fantasticsource.dynamicstealth.newai;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.ai.AITargetEdit;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Date;

public class AISearchLastKnownPosition extends EntityAIBase
{
    private final static int HEAD_TURN_SPEED = 3;

    private final AIStoreKnownPosition knownPositionAI;
    private final EntityLiving searcher;
    private final PathNavigate navigator;

    private int phase, timer = 0, searchTicks, timeAtPos;
    public double speed;
    private boolean spinDirection;
    public Path path = null;
    private Vec3d lastPos = null, nextPos = null;
    private double startAngle, angleDif, pathAngle;
    private static TrigLookupTable trigTable = DynamicStealth.TRIG_TABLE;



    public AISearchLastKnownPosition(EntityLiving living, int searchTicksIn, double speedIn)
    {
        searcher = living;
        navigator = living.getNavigator();
        speed = speedIn;
        searchTicks = searchTicksIn;

        knownPositionAI = findKnownPositionAI();
        if (knownPositionAI == null) throw new IllegalArgumentException("AISearchLastKnownPosition may only be added to an entity that already has an AIStoreKnownPosition in its targetTasks");

        setMutexBits(3);
    }

    private AIStoreKnownPosition findKnownPositionAI()
    {
        for (EntityAITasks.EntityAITaskEntry entry : searcher.targetTasks.taskEntries)
        {
            if (entry.action instanceof AIStoreKnownPosition) return (AIStoreKnownPosition) entry.action;
        }
        return null;
    }



    @Override
    public boolean shouldExecute()
    {
        if (AITargetEdit.isSuitableTarget(searcher, knownPositionAI.target))
        {
            searcher.setAttackTarget(knownPositionAI.target);
            knownPositionAI.lastKnownPosition = knownPositionAI.target.getPosition();
            return false;
        }

        return (knownPositionAI.target != null && knownPositionAI.lastKnownPosition != null);
    }

    @Override
    public void startExecuting()
    {
        phase = 0;

        timer = searchTicks;

        timeAtPos = 0;
        lastPos = null;

        path = navigator.getPathToPos(knownPositionAI.lastKnownPosition);
        navigator.setPath(path, speed);
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return (shouldExecute() && timer > 0);
    }

    @Override
    public void updateTask()
    {
        timer--;

        //Reach searchPos, or the nearest reachable position to it.  If we reach a position, reset the search timer
        if (phase == 0)
        {
            if (navigator.getPath() != path) navigator.setPath(path, speed);

            Vec3d currentPos = searcher.getPositionVector();
            if (lastPos != null && lastPos.squareDistanceTo(currentPos) < speed * 0.005) timeAtPos++;
            else timeAtPos = 0;

            lastPos = currentPos;

            if (timeAtPos > 60 || (searcher.onGround && navigator.noPath() && !newPath(knownPositionAI.lastKnownPosition)))
            {
                phase = 1;

                timer = searchTicks - timeAtPos;

                startAngle = searcher.rotationYawHead;
                spinDirection = searcher.getRNG().nextBoolean();
                angleDif = 0;
            }
        }

        //Do a 360 search-in-place, then choose a random spot to move to nearby
        if (phase == 1)
        {
            navigator.clearPath();

            if (spinDirection) angleDif += HEAD_TURN_SPEED;
            else angleDif -= HEAD_TURN_SPEED;

            double angleRad = Tools.degtorad(startAngle + angleDif);
            searcher.getLookHelper().setLookPosition(searcher.posX - trigTable.sin(angleRad), searcher.posY + searcher.getEyeHeight(), searcher.posZ + trigTable.cos(angleRad), HEAD_TURN_SPEED, HEAD_TURN_SPEED);

            if (Math.abs(angleDif) >= 360)
            {
                if (randomPath(searcher.getPosition(), 4, 2) != null && !path.isFinished() && findPathAngle())
                {
                    phase = 2;
                }
                else
                {
                    startAngle = searcher.rotationYawHead;
                    spinDirection = searcher.getRNG().nextBoolean();
                    angleDif = 0;
                }
            }
        }

        //Gradually turn toward initial path direction before moving to new random point
        if (phase == 2)
        {
            navigator.clearPath();

            double head = Tools.mod(searcher.rotationYawHead, 360);
            if ((head > pathAngle && head - pathAngle <= 1) || (head <= pathAngle && pathAngle - head <= 1))
            {
                phase = 3;

                lastPos = null;
                timeAtPos = 0;
            }
            else
            {
                searcher.getLookHelper().setLookPosition(nextPos.x, searcher.posY + searcher.getEyeHeight(), nextPos.z, HEAD_TURN_SPEED, HEAD_TURN_SPEED);
            }
        }

        //Move along our path to our random point, then choose a new random point
        if (phase == 3)
        {
            if (navigator.getPath() != path) navigator.setPath(path, speed);

            Vec3d currentPos = searcher.getPositionVector();
            if (lastPos != null && lastPos.squareDistanceTo(currentPos) < speed * 0.005) timeAtPos++;
            else timeAtPos = 0;

            lastPos = currentPos;

            if (timeAtPos > 60 || (searcher.onGround && navigator.noPath() && !newPath(path)))
            {
                phase = 1;

                startAngle = searcher.rotationYawHead;
                spinDirection = searcher.getRNG().nextBoolean();
                angleDif = 0;
            }
        }
    }

    private boolean findPathAngle()
    {
        int length = path.getCurrentPathLength();
        if (length < 2) return false;

        int i = 1;
        Vec3d pos = searcher.getPositionVector();
        nextPos = path.getVectorFromIndex(searcher, i);

        while ((new BlockPos(pos)).distanceSq(new BlockPos(nextPos)) < 2)
        {
            if (length < i + 2) return false;

            i++;
            nextPos = path.getVectorFromIndex(searcher, i);
        }

        pathAngle = 360 - Tools.radtodeg(DynamicStealth.TRIG_TABLE.arctanFullcircle(pos.z, -pos.x, nextPos.z, -nextPos.x));

        return true;
    }

    private boolean newPath(Path pathIn)
    {
        if (pathIn == null) return false;

        PathPoint finalPoint = pathIn.getFinalPathPoint();
        return finalPoint != null && newPath(new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z));
    }

    private boolean newPath(BlockPos targetPos)
    {
        Path newPath = navigator.getPathToPos(targetPos);
        if (newPath == null) return false;

        PathPoint finalPoint = newPath.getFinalPathPoint();
        if (finalPoint == null || Math.pow(finalPoint.x - searcher.posX, 2) + Math.pow(finalPoint.y - searcher.posY, 2) + Math.pow(finalPoint.z - searcher.posZ, 2) < 1) return false;

        path = newPath;
        navigator.setPath(path, speed);
        return true;
    }

    @Override
    public void resetTask()
    {
        if (!AITargetEdit.isSuitableTarget(searcher, knownPositionAI.target))
        {
            knownPositionAI.lastKnownPosition = null;
            knownPositionAI.target = null;
        }

        if (path != null && path.equals(navigator.getPath())) navigator.clearPath();
        path = null;

        searcher.rotationYaw = searcher.rotationYawHead;
    }

    public BlockPos randomPath(BlockPos position, int xz, int y)
    {
        if (xz < 0) xz = -xz;
        if (y < 0) y = -y;

        int x = xz > 0 ? searcher.getRNG().nextInt(xz * 2) : 0;
        int z = xz > 0 ? searcher.getRNG().nextInt(xz * 2) : 0;

        int yDir = searcher.getRNG().nextBoolean() ? 1 : -1;
        int yEnd = yDir == 1 ? y * 2 : 0;

        int xCheck, yCheck, zCheck;
        BlockPos checkPos;
        for(int ix = 0; ix < xz * 2; ix++)
        {
            for(int iz = 0; iz < xz * 2; iz++)
            {
                for(int iy = 0; Math.abs(iy) <= yEnd; iy += yDir)
                {
                    if (xz > 0)
                    {
                        xCheck = (x + ix) % (xz * 2) - xz + position.getX();
                        zCheck = (z + iz) % (xz * 2) - xz + position.getZ();
                    }
                    else
                    {
                        xCheck = position.getX();
                        zCheck = position.getZ();
                    }
                    yCheck = y > 0 ? (y + iy) % (y * 2) - y + position.getY() : position.getY();

                    checkPos = new BlockPos(xCheck, yCheck, zCheck);
                    if (newPath(checkPos)) return checkPos;
                }
            }
        }

        return null;
    }
}
