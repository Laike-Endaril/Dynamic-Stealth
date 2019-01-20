package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.ai.edited.AITargetEdit;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
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

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.compat.Compat.cancelTasksRequiringAttackTarget;

public class AIStealthTargetingAndSearch extends EntityAIBase
{
    private static TrigLookupTable trigTable = DynamicStealth.TRIG_TABLE;
    private final EntityLiving searcher;
    private final PathNavigate navigator;
    public double speed;
    public Path path = null;
    public BlockPos lastKnownPosition = null;
    private int phase, timeAtPos;
    private boolean spinDirection;
    private Vec3d lastPos = null, nextPos = null;
    private double startAngle, angleDif, pathAngle;
    private int headTurnSpeed;


    public AIStealthTargetingAndSearch(EntityLiving living, double speedIn)
    {
        searcher = living;
        navigator = living.getNavigator();
        speed = speedIn;

        headTurnSpeed = EntityAIData.headTurnSpeed(searcher);

        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        if (Threat.bypassesThreat(searcher)) return false;
        if (Threat.isPassive(searcher))
        {
            searcher.setAttackTarget(null);
            return false;
        }

        Threat.ThreatData threatData = Threat.get(searcher);
        int threat = threatData.threatLevel;

        if (threat <= 0)
        {
            EntityLivingBase attackTarget = searcher.getAttackTarget();
            if (AITargetEdit.isSuitableTarget(searcher, attackTarget))
            {
                //Hopefully this always only means we've just noticed a new, valid target
                Threat.set(searcher, attackTarget, serverSettings.threat.targetSpottedThreat);
                lastKnownPosition = attackTarget.getPosition();
                clearSearchPath();
                return false;
            }

            //No suitable target, old or new, and threat is <= 0
            searcher.setAttackTarget(null);
            cancelTasksRequiringAttackTarget(searcher.tasks);
            return false;
        }

        //Threat > 0

        EntityLivingBase threatTarget = threatData.target;

        if (threatTarget == null)
        {
            EntityLivingBase attackTarget = searcher.getAttackTarget();
            if (AITargetEdit.isSuitableTarget(searcher, attackTarget))
            {
                //Hopefully this always only means we've just noticed a new, valid target
                Threat.set(searcher, attackTarget, serverSettings.threat.targetSpottedThreat);
                lastKnownPosition = attackTarget.getPosition();
                clearSearchPath();
                return false;
            }

            //No suitable target, old or new, but threat is > 0
            cancelTasksRequiringAttackTarget(searcher.tasks);
            return unseenTargetDegredation(threat);
        }

        //Threat > 0 and threatTarget != null...we have an existing target from before

        if (AITargetEdit.isSuitableTarget(searcher, threatTarget))
        {
            //Existing target's current position is known
            lastKnownPosition = threatTarget.getPosition();
            clearSearchPath();
            searcher.setAttackTarget(threatTarget);
            return false;
        }

        //Target's current position is unknown
        cancelTasksRequiringAttackTarget(searcher.tasks);
        return unseenTargetDegredation(threat);
    }

    private boolean unseenTargetDegredation(int threat)
    {
        searcher.setAttackTarget(null);

        int result = Math.max(0, threat - serverSettings.threat.unseenTargetDegredationRate);
        if (result < serverSettings.threat.unseenMinimumThreat)
        {
            result = 0;
            clearSearchPath();
        }

        Threat.setThreat(searcher, result);
        return result > 0;
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

            if (timeAtPos > 60 || lastKnownPosition == null || (searcher.onGround && navigator.noPath() && !newPath(lastKnownPosition)))
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

            if (spinDirection) angleDif += headTurnSpeed;
            else angleDif -= headTurnSpeed;

            double angleRad = Tools.degtorad(startAngle + angleDif);
            searcher.getLookHelper().setLookPosition(searcher.posX - trigTable.sin(angleRad), searcher.posY + searcher.getEyeHeight(), searcher.posZ + trigTable.cos(angleRad), headTurnSpeed, headTurnSpeed);

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
                searcher.getLookHelper().setLookPosition(nextPos.x, searcher.posY + searcher.getEyeHeight(), nextPos.z, headTurnSpeed, headTurnSpeed);
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
