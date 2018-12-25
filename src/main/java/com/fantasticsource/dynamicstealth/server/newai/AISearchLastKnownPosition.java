package com.fantasticsource.dynamicstealth.server.newai;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.tools.datastructures.Pair;
import com.fantasticsource.dynamicstealth.server.Threat;
import com.fantasticsource.dynamicstealth.ai.AITargetEdit;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.*;

public class AISearchLastKnownPosition extends EntityAIBase
{
    private final EntityLiving searcher;
    private final PathNavigate navigator;

    private int phase, timeAtPos;
    public double speed;
    private boolean spinDirection;
    public Path path = null;
    private Vec3d lastPos = null, nextPos = null;
    private double startAngle, angleDif, pathAngle;
    private static TrigLookupTable trigTable = DynamicStealth.TRIG_TABLE;
    public BlockPos lastKnownPosition = null;


    public AISearchLastKnownPosition(EntityLiving living, double speedIn)
    {
        searcher = living;
        navigator = living.getNavigator();
        speed = speedIn;

        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        //Sync attack/revenge target to threat target
        Pair<EntityLivingBase, Integer> threatProperties = Threat.get(searcher);
        EntityLivingBase threatTarget = threatProperties.getKey();
        int threat = threatProperties.getValue();

        if (threatTarget == null || threat == 0)
        {
            //No active threat target
            EntityLivingBase attackTarget = searcher.getAttackTarget();
            if (attackTarget != null)
            {
                //Hopefully this always only means we've just noticed a new, valid target
                Threat.set(searcher, attackTarget, serverSettings.threat.targetSpottedThreat);
                lastKnownPosition = attackTarget.getPosition();
            }
        }
        else
        {
            //Active threat target exists
        }

        if (AITargetEdit.isSuitableTarget(searcher, threatTarget))
        {
            searcher.setAttackTarget(threatTarget);
            lastKnownPosition = threatTarget.getPosition();

            clearSearchPath();

            return false;
        }

        searcher.setAttackTarget(null);
        if (Threat.getThreat(searcher) > serverSettings.threat.unseenMinimumThreat) return true;
        else
        {
            Threat.setThreat(searcher, threat - serverSettings.threat.unseenTargetDegredationRate);

            clearSearchPath();

            return false;
        }
    }

    private void clearSearchPath()
    {
        if (path != null && path.equals(navigator.getPath()))
        {
            navigator.clearPath();
            searcher.rotationYaw = searcher.rotationYawHead;
        }
        path = null;
    }

    @Override
    public void startExecuting()
    {
        if (lastKnownPosition != null)
        {
            phase = 0;

            timeAtPos = 0;
            lastPos = null;

            path = navigator.getPathToPos(lastKnownPosition);
            navigator.setPath(path, speed);
        }
        else
        {
            phase = 1;

            startAngle = searcher.rotationYawHead;
            spinDirection = searcher.getRNG().nextBoolean();
            angleDif = 0;
        }
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return shouldExecute();
    }

    @Override
    public void updateTask()
    {
        //Reach searchPos, or the nearest reachable position to it.  If we reach a position, reset the search timer
        if (phase == 0)
        {
            if (navigator.getPath() != path) navigator.setPath(path, speed);

            Vec3d currentPos = searcher.getPositionVector();
            if (lastPos != null && lastPos.squareDistanceTo(currentPos) < speed * 0.005) timeAtPos++;
            else timeAtPos = 0;

            lastPos = currentPos;

            if (timeAtPos > 60 || (searcher.onGround && navigator.noPath() && !newPath(lastKnownPosition)))
            {
                phase = 1;

                startAngle = searcher.rotationYawHead;
                spinDirection = searcher.getRNG().nextBoolean();
                angleDif = 0;
            }
        }

        //Do a 360 search-in-place, then choose a random spot to move to nearby
        if (phase == 1)
        {
            navigator.clearPath();

            if (spinDirection) angleDif += serverSettings.ai.headTurnSpeed;
            else angleDif -= serverSettings.ai.headTurnSpeed;

            double angleRad = Tools.degtorad(startAngle + angleDif);
            searcher.getLookHelper().setLookPosition(searcher.posX - trigTable.sin(angleRad), searcher.posY + searcher.getEyeHeight(), searcher.posZ + trigTable.cos(angleRad), serverSettings.ai.headTurnSpeed, serverSettings.ai.headTurnSpeed);

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
                searcher.getLookHelper().setLookPosition(nextPos.x, searcher.posY + searcher.getEyeHeight(), nextPos.z, serverSettings.ai.headTurnSpeed, serverSettings.ai.headTurnSpeed);
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

        Threat.setThreat(searcher, (Threat.getThreat(searcher) - serverSettings.threat.unseenTargetDegredationRate));
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
        for (int ix = 0; ix < xz * 2; ix++)
        {
            for (int iz = 0; iz < xz * 2; iz++)
            {
                for (int iy = 0; Math.abs(iy) <= yEnd; iy += yDir)
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
