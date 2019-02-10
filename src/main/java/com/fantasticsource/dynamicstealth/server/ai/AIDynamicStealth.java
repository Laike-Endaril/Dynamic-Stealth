package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.server.CombatTracker;
import com.fantasticsource.dynamicstealth.server.senses.hearing.Communication;
import com.fantasticsource.dynamicstealth.server.ai.edited.AITargetEdit;
import com.fantasticsource.dynamicstealth.server.event.BasicEvent;
import com.fantasticsource.dynamicstealth.server.event.EventData;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.fantasticsource.dynamicstealth.compat.Compat.cancelTasksRequiringAttackTarget;
import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class AIDynamicStealth extends EntityAIBase
{
    public static final int
            MODE_NONE = 0,
            MODE_FIND_PATH = 1,
            MODE_FOLLOW_PATH = 2,
            MODE_SPIN = 3,
            MODE_FIND_RANDOM_PATH = 4,
            MODE_FACE_RANDOM_PATH = 5,
            MODE_FOLLOW_RANDOM_PATH = 6,
            MODE_FLEE = -1;

    public static final int
            FLEE_NONE = 0,
            FLEE_PASSIVE = 1,
            FLEE_CANTREACH = 2,
            FLEE_HP = 3;

    private static Method navigatorCanNavigateMethod;
    private static Field aiPanicSpeedField;
    private static TrigLookupTable trigTable = DynamicStealth.TRIG_TABLE;

    static
    {
        initReflections();
    }

    private final EntityLiving searcher;
    private final PathNavigate navigator;
    public double speed;
    public Path path = null;
    public BlockPos lastKnownPosition = null, fleeToPos = null;
    public boolean triedTriggerCantReach = false;
    public int fleeReason;
    private int mode, timeAtPos; //Don't replace timeAtPos with a ServerTickTimer reference, because this ai does not run every tick
    private boolean spinDirection;
    private Vec3d lastPos = null, nextPos = null;
    private double startAngle, angleDif, pathAngle;
    private int headTurnSpeed;
    private boolean isCNPC;


    public AIDynamicStealth(EntityLiving living, double speedIn)
    {
        searcher = living;
        navigator = living.getNavigator();
        speed = speedIn;

        headTurnSpeed = EntityAIData.headTurnSpeed(searcher);
        isCNPC = Compat.customnpcs && NpcAPI.Instance().getIEntity(searcher) instanceof ICustomNpc;

        setMutexBits(3);
    }

    public static AIDynamicStealth getStealthAI(EntityLiving living)
    {
        for (EntityAITasks.EntityAITaskEntry task : living.tasks.taskEntries)
        {
            if (task.action instanceof AIDynamicStealth) return (AIDynamicStealth) task.action;
        }
        return null;
    }

    private static void initReflections()
    {
        try
        {
            aiPanicSpeedField = ReflectionTool.getField(EntityAIPanic.class, "field_75265_b", "speed");
            navigatorCanNavigateMethod = ReflectionTool.getMethod(PathNavigate.class, "func_75485_k", "canNavigate");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(148, true);
        }
    }

    public boolean isFleeing()
    {
        return fleeReason != FLEE_NONE;
    }

    public void fleeIfYouShould(float hpOffset)
    {
        if (!EntityThreatData.bypassesThreat(searcher) && (fleeReason == FLEE_CANTREACH || !EntityThreatData.isFearless(searcher)))
        {
            if (EntityThreatData.isPassive(searcher))
            {
                fleeReason = FLEE_PASSIVE;
                mode = MODE_NONE;
            }
            else if ((int) ((searcher.getHealth() + hpOffset) / searcher.getMaxHealth() * 100) <= serverSettings.ai.flee.threshold)
            {
                fleeReason = FLEE_HP;
                mode = MODE_NONE;
            }
        }
    }

    private boolean canReachTarget()
    {
        if (CombatTracker.timeSinceLastIdle(searcher) >= serverSettings.ai.cantReach.lastIdleThreshold
                && CombatTracker.timeSinceLastSuccessfulAttack(searcher) >= serverSettings.ai.cantReach.lastAttackThreshold
                && CombatTracker.timeSinceLastSuccessfulPath(searcher) >= serverSettings.ai.cantReach.lastPathThreshold
                && CombatTracker.timeSinceLastNoTarget(searcher) >= serverSettings.ai.cantReach.lastNoTargetThreshold)
        {
            EntityLivingBase target = Threat.getTarget(searcher);
            if (target == null) return false;

            Path newPath = navigator.getPathToEntityLiving(target);
            if (newPath == null || newPath.isSamePath(navigator.getPath())) return false;

            return CombatTracker.pathReachesThreatTarget(searcher, newPath);
        }

        return true;
    }

    @Override
    public boolean shouldExecute()
    {
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
                clearAIPath();
                Communication.warn(searcher, lastKnownPosition);
                MinecraftForge.EVENT_BUS.post(new BasicEvent.TargetSeenEvent(searcher));
                return false;
            }

            //No suitable target, old or new, and threat is <= 0
            searcher.setAttackTarget(null);
            cancelTasksRequiringAttackTarget(searcher.tasks);
            return false;
        }


        //Threat > 0

        if (fleeReason != FLEE_NONE) return true;

        EntityLivingBase threatTarget = threatData.target;

        if (threatTarget == null)
        {
            EntityLivingBase attackTarget = searcher.getAttackTarget();
            if (AITargetEdit.isSuitableTarget(searcher, attackTarget))
            {
                //Hopefully this always only means we've just noticed a new, valid target
                Threat.set(searcher, attackTarget, serverSettings.threat.targetSpottedThreat);
                lastKnownPosition = attackTarget.getPosition();
                clearAIPath();
                Communication.warn(searcher, lastKnownPosition);
                MinecraftForge.EVENT_BUS.post(new BasicEvent.TargetSeenEvent(searcher));
                return false;
            }

            //No suitable target, old or new, but threat is > 0
            cancelTasksRequiringAttackTarget(searcher.tasks);
            return unseenTargetDegredation(threat);
        }


        //Threat > 0 and threatTarget != null...we have an existing target from before

        if (!canReachTarget())
        {
            if (!triedTriggerCantReach && !MinecraftForge.EVENT_BUS.post(new BasicEvent.CantReachEvent(searcher)))
            {
                Communication.warn(searcher, lastKnownPosition);
                for (PotionEffect potionEffect : EventData.desperationPotions)
                {
                    searcher.addPotionEffect(new PotionEffect(potionEffect));
                }

                if (serverSettings.ai.cantReach.flee)
                {
                    fleeReason = FLEE_CANTREACH;
                    return true;
                }
            }
            triedTriggerCantReach = true;
        }
        else triedTriggerCantReach = false;

        if (AITargetEdit.isSuitableTarget(searcher, threatTarget))
        {
            //Existing target's current position is known
            lastKnownPosition = threatTarget.getPosition();
            clearAIPath();
            searcher.setAttackTarget(threatTarget);
            return false;
        }

        //Target's current position is unknown
        cancelTasksRequiringAttackTarget(searcher.tasks);
        return unseenTargetDegredation(threat);
    }

    private boolean unseenTargetDegredation(int threat)
    {
        if (fleeReason != FLEE_NONE) return threat > 0; //Flee degredation handled elsewhere
        {
            searcher.setAttackTarget(null);

            threat = Math.max(0, threat - serverSettings.threat.unseenTargetDegredationRate);
            if (threat <= serverSettings.threat.unseenMinimumThreat)
            {
                threat = 0;
                clearAIPath();
            }

            Threat.setThreat(searcher, threat);
            return threat > 0;
        }
    }

    private void clearAIPath()
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
        lastPos = null;
        timeAtPos = 0;

        if (fleeReason == FLEE_NONE)
        {
            if (lastKnownPosition == null) mode(MODE_SPIN);
            else mode(MODE_FIND_PATH);

            MinecraftForge.EVENT_BUS.post(new BasicEvent.SearchEvent(searcher));
        }
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return shouldExecute();
    }

    private void mode(int newMode)
    {
        if (mode == MODE_SPIN && newMode != MODE_SPIN) timeAtPos = 0;
        else if (mode == MODE_FLEE && newMode != MODE_FLEE) fleeReason = FLEE_NONE;

        if (newMode == MODE_NONE)
        {
            fleeReason = FLEE_NONE;
        }
        else if (newMode == MODE_SPIN)
        {
            startAngle = searcher.rotationYawHead;
            spinDirection = searcher.getRNG().nextBoolean();
            angleDif = 0;
        }
        else if (newMode == MODE_FOLLOW_PATH || newMode == MODE_FOLLOW_RANDOM_PATH)
        {
            timeAtPos = 0;
            searcher.rotationYaw = searcher.rotationYawHead;
        }
        else if (newMode == MODE_FLEE) throw new IllegalArgumentException();
        else if (newMode == MODE_FIND_RANDOM_PATH)
        {
            lastKnownPosition = MCTools.randomPos(searcher.getPosition(), (int) (navigator.getPathSearchRange() * 0.5), (int) (navigator.getPathSearchRange() * 0.25));
        }

        mode = newMode;
    }

    @Override
    public void updateTask()
    {
        //Calc movement data
        Vec3d currentPos = searcher.getPositionVector();
        if (lastPos != null && lastPos.squareDistanceTo(currentPos) < speed * 0.001) timeAtPos++;
        else timeAtPos = 0;
        lastPos = currentPos;


        //Last second mode changes

        if (fleeReason != FLEE_NONE && mode != MODE_FLEE)
        {
            //Flee (do not use mode() method, to prevent accidentally using it from other places; fleeing should be started by setting "fleeing" to true)
            mode = MODE_FLEE;
            clearAIPath();
            fleeToPos = null;
            timeAtPos = 0;
            MinecraftForge.EVENT_BUS.post(new BasicEvent.FleeEvent(searcher, fleeReason));
        }


        //Find the next waypoint toward lastKnownPosition and a path toward it
        //Success -> MODE_FOLLOW_PATH
        //Failure -> MODE_SPIN
        if (mode == MODE_FIND_PATH)
        {
            double distSquared = lastKnownPosition.distanceSq(searcher.getPosition());
            if (distSquared < 1 || timeAtPos > 60) mode(MODE_SPIN);
            else try
            {
                if (!(boolean) navigatorCanNavigateMethod.invoke(navigator)) return;
                else
                {
                    //We can navigate, and have not reached lastKnownPosition
                    Path newPath;
                    if (distSquared < Math.pow(navigator.getPathSearchRange() - 2, 2))
                    {
                        //Position in range
                        newPath = navigator.getPathToPos(lastKnownPosition);
                    }
                    else
                    {
                        //Position out of range
                        BlockPos startPos = searcher.getPosition();
                        BlockPos dif = lastKnownPosition.subtract(startPos);
                        double ratio = navigator.getPathSearchRange() * 0.75 / Math.sqrt(dif.distanceSq(0, 0, 0));
                        newPath = navigator.getPathToPos(startPos.add(new BlockPos(dif.getX() * ratio, dif.getY() * ratio, dif.getZ() * ratio)));
                    }

                    if (newPath == null || newPath.isSamePath(path)) mode(MODE_SPIN);
                    else
                    {
                        path = newPath;
                        mode(MODE_FOLLOW_PATH);
                    }
                }
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
                FMLCommonHandler.instance().exitJava(150, false);
            }
        }


        //Reach our waypoint, or the nearest reachable position to it.
        //Done -> MODE_FIND_PATH
        if (mode == MODE_FOLLOW_PATH)
        {
            if (timeAtPos > 5 || path == null) mode(MODE_FIND_PATH);
            else if (navigator.getPath() != path) navigator.setPath(path, speed);
        }


        //Do a 360 search-in-place
        //Done -> MODE_FIND_RANDOM_PATH
        if (mode == MODE_SPIN)
        {
            navigator.clearPath();

            if (spinDirection) angleDif += headTurnSpeed;
            else angleDif -= headTurnSpeed;

            double angleRad = Tools.degtorad(startAngle + angleDif);
            searcher.getLookHelper().setLookPosition(searcher.posX - trigTable.sin(angleRad), searcher.posY + searcher.getEyeHeight(), searcher.posZ + trigTable.cos(angleRad), headTurnSpeed, headTurnSpeed);

            if (Math.abs(angleDif) >= 360) mode(MODE_FIND_RANDOM_PATH);
        }


        //Find a random path
        //Success -> MODE_FACE_RANDOM_PATH
        //Failure -> MODE_SPIN
        if (mode == MODE_FIND_RANDOM_PATH)
        {
            double distSquared = lastKnownPosition.distanceSq(searcher.getPosition());
            if (distSquared < 1 || timeAtPos > 60) mode(MODE_SPIN);
            else try
            {
                if (!(boolean) navigatorCanNavigateMethod.invoke(navigator)) return;
                else
                {
                    //We can navigate, and have not reached lastKnownPosition
                    //Position in range, because we calced it in range inside mode() method

                    Path newPath = navigator.getPathToPos(lastKnownPosition);
                    if (newPath == null || newPath.isSamePath(path)) mode(MODE_SPIN);
                    else
                    {
                        path = newPath;
                        if (findPathAngle()) mode(MODE_FACE_RANDOM_PATH);
                        else mode(MODE_SPIN);
                    }
                }
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
                FMLCommonHandler.instance().exitJava(150, false);
            }
        }


        //Gradually turn toward first point in random path before moving to it
        //Done -> MODE_FOLLOW_RANDOM_PATH
        if (mode == MODE_FACE_RANDOM_PATH)
        {
            navigator.clearPath();

            double headYaw = Tools.mod(searcher.rotationYawHead, 360);
            if ((headYaw > pathAngle && headYaw - pathAngle <= 1) || (headYaw <= pathAngle && pathAngle - headYaw <= 1))
            {
                mode(MODE_FOLLOW_RANDOM_PATH);
            }
            else
            {
                searcher.getLookHelper().setLookPosition(nextPos.x, searcher.posY + searcher.getEyeHeight(), nextPos.z, headTurnSpeed, headTurnSpeed);
            }
        }


        //Reach our waypoint, or the nearest reachable position to it.
        //Done -> MODE_SPIN
        if (mode == MODE_FOLLOW_RANDOM_PATH)
        {
            if (timeAtPos > 5 || path == null) mode(MODE_SPIN);
            else if (navigator.getPath() != path) navigator.setPath(path, speed);
        }


        //Flee from lastKnownPosition
        if (mode == MODE_FLEE)
        {
            //Threat calc
            Threat.ThreatData data = Threat.get(searcher);
            int threat = Math.max(0, data.threatLevel - serverSettings.ai.flee.degredationRate);
            Threat.setThreat(searcher, threat);


            //Flee interrupts
            int oldReason = fleeReason;
            if (threat <= 0)
            {
                mode(MODE_NONE);
                if (serverSettings.interactions.calmDown.fullHPRecovery) searcher.setHealth(searcher.getMaxHealth());
                if (isCNPC && serverSettings.interactions.calmDown.cnpcsWarpHome)
                {
                    ICustomNpc cnpc = (ICustomNpc) NpcAPI.Instance().getIEntity(searcher);
                    MCTools.teleport(searcher, cnpc.getHomeX() + 0.5, cnpc.getHomeY() + 0.5, cnpc.getHomeZ() + 0.5, false, 0);
                }

                if (!MinecraftForge.EVENT_BUS.post(new BasicEvent.CalmDownEvent(searcher, oldReason)))
                {
                    for (PotionEffect potionEffect : EventData.calmDownPotions)
                    {
                        searcher.addPotionEffect(new PotionEffect(potionEffect));
                    }
                }
            }
            else
            {
                if (fleeReason == FLEE_CANTREACH && canReachTarget())
                {
                    mode(MODE_NONE);
                    fleeIfYouShould(0);
                    if (fleeReason == FLEE_NONE && !MinecraftForge.EVENT_BUS.post(new BasicEvent.RallyEvent(searcher, oldReason)))
                    {
                        Communication.warn(searcher, lastKnownPosition);
                        for (PotionEffect potionEffect : EventData.rallyPotions)
                        {
                            searcher.addPotionEffect(new PotionEffect(potionEffect));
                        }
                    }
                }

                oldReason = fleeReason;
                if (fleeReason == FLEE_HP && (int) (searcher.getHealth() / searcher.getMaxHealth() * 100) > serverSettings.ai.flee.threshold)
                {
                    mode(MODE_NONE);
                    fleeIfYouShould(0);
                    if (fleeReason == FLEE_NONE && !MinecraftForge.EVENT_BUS.post(new BasicEvent.RallyEvent(searcher, oldReason)))
                    {
                        Communication.warn(searcher, lastKnownPosition);
                        for (PotionEffect potionEffect : EventData.rallyPotions)
                        {
                            searcher.addPotionEffect(new PotionEffect(potionEffect));
                        }
                    }
                }
            }

            if (fleeReason == FLEE_NONE)
            {
                clearAIPath();
                if (Threat.getThreat(searcher) > 0) restart(lastKnownPosition);
                return;
            }


            //Ensure lastKnownPosition is non-null
            if (lastKnownPosition == null) lastKnownPosition = MCTools.randomPos(searcher.getPosition(), 5, 0);


            if (isCNPC && serverSettings.ai.flee.cnpcsRunHome)
            {
                //Set flee position
                ICustomNpc cnpc = (ICustomNpc) NpcAPI.Instance().getIEntity(searcher);
                fleeToPos = new BlockPos(cnpc.getHomeX(), cnpc.getHomeY(), cnpc.getHomeZ());

                //Set path
                if ((fleeToPos.getX() != searcher.getPosition().getX() || fleeToPos.getZ() != searcher.getPosition().getZ()) && (path == null || path.isFinished()))
                {
                    path = navigator.getPathToPos(fleeToPos);
                    navigator.setPath(path, getFleeSpeed(speed));
                }
                else if (navigator.getPath() != path) navigator.setPath(path, getFleeSpeed(speed));
            }
            else
            {
                //Set flee position
                BlockPos oldFleePos = fleeToPos;

                if (fleeToPos == null || searcher.getPosition().distanceSq(fleeToPos) < 5 || (path != null && path.isFinished()) || timeAtPos > 2)
                {
                    if (timeAtPos <= 3) fleeToPos = new BlockPos(searcher.getPositionVector().add(searcher.getPositionVector().subtract(new Vec3d(lastKnownPosition)).normalize().scale(10)));
                    else if (timeAtPos == 4) findShortRangeGoalPos();
                    else if (fleeReason == FLEE_HP && !MinecraftForge.EVENT_BUS.post(new BasicEvent.DesperationEvent(searcher)))
                    {
                        mode(MODE_NONE);
                        restart(lastKnownPosition);

                        Communication.warn(searcher, lastKnownPosition);
                        for (PotionEffect potionEffect : EventData.desperationPotions)
                        {
                            searcher.addPotionEffect(new PotionEffect(potionEffect));
                        }

                        return;
                    }
                    else timeAtPos = 0;
                }

                //Set path
                if (fleeToPos != oldFleePos || path == null)
                {
                    path = navigator.getPathToPos(fleeToPos);
                    navigator.setPath(path, getFleeSpeed(speed));
                }
                else if (navigator.getPath() != path) navigator.setPath(path, getFleeSpeed(speed));
            }
        }
    }

    private void findShortRangeGoalPos()
    {
        BlockPos searcherPos = searcher.getPosition();

        int xFactor = searcherPos.getX() - lastKnownPosition.getX();
        int zFactor = searcherPos.getZ() - lastKnownPosition.getZ();
        int maxFactor = Tools.max(Math.abs(xFactor), Math.abs(zFactor));
        if (maxFactor == 0)
        {
            maxFactor = 5;
            if (Math.random() < 0.5)
            {
                xFactor = 0;
                zFactor = Math.random() < 0.5 ? 5 : -5;
            }
            else
            {
                xFactor = Math.random() < 0.5 ? 5 : -5;
                zFactor = 0;
            }
        }
        xFactor = (int) ((double) xFactor * 5 / maxFactor);
        zFactor = (int) ((double) zFactor * 5 / maxFactor);
        int xStep = xFactor < 0 ? -1 : 1;
        int zStep = zFactor < 0 ? -1 : 1;

        BlockPos testPos;
        boolean found = false;
        for (int iy = 1; iy > -4 && !found; iy--)
        {
            for (int ix = xFactor; ix != -xStep && !found; ix -= xStep)
            {
                for (int iz = zFactor; iz != -zStep && !found; iz -= zStep)
                {
                    testPos = searcherPos.add(ix, iy, iz);
                    if ((ix != 0 || iy != 0 || iz != 0) && (navigator.canEntityStandOnPos(testPos) && !searcher.world.getBlockState(testPos).getMaterial().blocksMovement()))
                    {
                        fleeToPos = testPos;
                        found = true;
                    }
                }
            }
        }
    }

    public void restart(BlockPos newPos) //This is NOT the same as resetTask(); this is just a proxy for me to remember how to reset this correctly from outside the task system
    {
        lastKnownPosition = newPos;
        startExecuting();
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

    private double getFleeSpeed(double normalSpeed)
    {
        for (EntityAITasks.EntityAITaskEntry task : searcher.tasks.taskEntries)
        {
            if (task.action instanceof EntityAIPanic)
            {
                try
                {
                    normalSpeed = (double) aiPanicSpeedField.get(task.action);
                    return normalSpeed <= 0 ? 1.25 : normalSpeed;
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                    FMLCommonHandler.instance().exitJava(149, false);
                }
            }
        }

        return normalSpeed <= 0 ? 1.25 : normalSpeed * 1.25;
    }
}
