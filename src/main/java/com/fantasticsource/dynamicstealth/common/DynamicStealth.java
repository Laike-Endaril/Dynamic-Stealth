package com.fantasticsource.dynamicstealth.common;

import com.fantasticsource.dynamicstealth.client.HUD;
import com.fantasticsource.dynamicstealth.client.RenderAlterer;
import com.fantasticsource.dynamicstealth.common.potions.Potions;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.compat.CompatCNPC;
import com.fantasticsource.dynamicstealth.server.*;
import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import com.fantasticsource.dynamicstealth.server.ai.edited.*;
import com.fantasticsource.dynamicstealth.server.entitytracker.EntityTrackerEdit;
import com.fantasticsource.dynamicstealth.server.event.attacks.AssassinationEvent;
import com.fantasticsource.dynamicstealth.server.event.attacks.AttackData;
import com.fantasticsource.dynamicstealth.server.event.attacks.StealthAttackEvent;
import com.fantasticsource.dynamicstealth.server.event.attacks.WeaponEntry;
import com.fantasticsource.dynamicstealth.server.senses.EntitySensesEdit;
import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightData;
import com.fantasticsource.dynamicstealth.server.senses.EntityTouchData;
import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.dynamicstealth.server.senses.hearing.Communication;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.Speedometer;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

@Mod(modid = DynamicStealth.MODID, name = DynamicStealth.NAME, version = DynamicStealth.VERSION)
public class DynamicStealth
{
    public static final String MODID = "dynamicstealth";
    public static final String NAME = "Dynamic Stealth";
    public static final String VERSION = "1.12.2.047";

    public static final TrigLookupTable TRIG_TABLE = new TrigLookupTable(1024);

    private static Field sensesField, lookHelperField, abstractSkeletonAIArrowAttackField, abstractSkeletonAIAttackOnCollideField, worldServerEntityTrackerField;
    private static Class aiSlimeFaceRandomClass, aiEvilAttackClass, aiBearMeleeClass, aiSpiderAttackClass, aiSpiderTargetClass, aiBearAttackPlayerClass, aiLlamaDefendTarget,
            aiPigmanHurtByAggressorClass, aiLlamaHurtByTargetClass, aiPigmanTargetAggressorClass, aiVindicatorJohnnyAttackClass, aiBearHurtByTargetClass, aiGuardianAttackClass,
            aiBlazeFireballAttackClass, aiVexChargeAttackClass, aiShulkerAttackClass, aiShulkerAttackNearestClass, aiShulkerDefenseAttackClass;

    public DynamicStealth()
    {
        Attributes.init();

        MinecraftForge.EVENT_BUS.register(ServerTickTimer.class);
        MinecraftForge.EVENT_BUS.register(CombatTracker.class);
        MinecraftForge.EVENT_BUS.register(EntitySightData.class);
        MinecraftForge.EVENT_BUS.register(DynamicStealth.class);
        MinecraftForge.EVENT_BUS.register(Speedometer.class);
        MinecraftForge.EVENT_BUS.register(Network.class);
        MinecraftForge.EVENT_BUS.register(Threat.class);
        MinecraftForge.EVENT_BUS.register(Sight.class);
        MinecraftForge.EVENT_BUS.register(Communication.class);
        MinecraftForge.EVENT_BUS.register(Potions.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(HUD.class);
            MinecraftForge.EVENT_BUS.register(ClientData.class);
            MinecraftForge.EVENT_BUS.register(RenderAlterer.class);
        }
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void drawGUI(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE)
        {
            new HUD(Minecraft.getMinecraft());
        }
    }

    @SubscribeEvent
    public static void despawn(LivingSpawnEvent.AllowDespawn event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        Event.Result result = event.getResult();
        if (livingBase instanceof EntityLiving && result != Event.Result.DENY && result != Event.Result.DEFAULT)
        {
            Threat.remove(livingBase);
        }
    }

    @SubscribeEvent
    public static void chunkUnload(ChunkEvent.Unload event)
    {
        Chunk chunk = event.getChunk();
        Set<Entity>[] sets = chunk.getEntityLists();
        for (Set<Entity> set : sets)
        {
            for (Entity entity : set)
            {
                if (entity instanceof EntityLiving) Threat.remove((EntityLiving) entity);
            }
        }
    }

    @SubscribeEvent
    public static void worldUnload(WorldEvent.Unload event)
    {
        for (Entity entity : event.getWorld().loadedEntityList)
        {
            if (entity instanceof EntityLiving) Threat.remove((EntityLiving) entity);
        }
    }


    @SubscribeEvent
    public static void worldLoad(WorldEvent.Load event) throws IllegalAccessException
    {
        World world = event.getWorld();
        if (world instanceof WorldServer && serverSettings.senses.usePlayerSenses)
        {
            worldServerEntityTrackerField.set(world, new EntityTrackerEdit((WorldServer) world));
        }
    }


    @SubscribeEvent
    public static void entityCollision(TickEvent.WorldTickEvent event) throws InvocationTargetException, IllegalAccessException
    {
        World world = event.world;
        if (!MCTools.isClient(world) && event.phase == TickEvent.Phase.START)
        {
            if (serverSettings.senses.touch.touchEnabled)
            {
                for (Entity feeler : world.loadedEntityList)
                {
                    if (feeler instanceof EntityLivingBase && feeler.isEntityAlive() && !(feeler instanceof EntityArmorStand || feeler instanceof EntityBat) && EntityTouchData.canFeel(feeler))
                    {
                        for (Entity felt : world.getEntitiesWithinAABBExcludingEntity(feeler, feeler.getEntityBoundingBox()))
                        {
                            if (felt.isEntityAlive() && (felt instanceof EntityPlayer || (felt instanceof EntityLiving && !(felt instanceof EntityBat))))
                            {
                                if (feeler instanceof EntityPlayerMP)
                                {
                                    //TODO add indicator for players
                                }
                                else if (feeler instanceof EntityLiving)
                                {
                                    EntityLiving feelerLiving = (EntityLiving) feeler;
                                    makeLivingLookDirection(feelerLiving, (float) Tools.radtodeg(TRIG_TABLE.arctanFullcircle(feeler.posZ, feeler.posX, felt.posZ, felt.posX)));
                                    feelerLiving.getNavigator().clearPath();
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Entity entity : world.loadedEntityList)
        {
            if (entity instanceof EntityLiving && entity.isEntityAlive())
            {
                CombatTracker.pathReachesThreatTarget((EntityLiving) entity);
            }
        }
    }


    @SubscribeEvent
    public static void kamikazeDeath(ExplosionEvent event)
    {
        //Because creepers don't trigger the LivingDeathEvent when they explode
        //So I'm making sure they setDead() before the damage happens to prevent threat changes
        //Trying to remove them from threat system right here doesn't work due to the timing
        Explosion explosion = event.getExplosion();
        EntityLivingBase exploder = explosion.getExplosivePlacedBy();
        if (exploder instanceof EntityCreeper) exploder.setDead();
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void entityDeath(LivingDeathEvent event)
    {
        EntityLivingBase victim = event.getEntityLiving();

        Entity source = event.getSource().getTrueSource();
        if (source == null) source = event.getSource().getImmediateSource();

        if (source instanceof EntityLivingBase)
        {
            EntityLivingBase killer = (EntityLivingBase) source;

            boolean wasSeen = false;

            EntityLivingBase witness;
            for (Entity entity : victim.world.loadedEntityList)
            {
                if (entity instanceof EntityLivingBase)
                {
                    witness = (EntityLivingBase) entity;

                    if (HelperSystem.shouldHelp(witness, victim, true, EntitySightData.distanceFar(witness)))
                    {
                        if (Sight.canSee(witness, victim))
                        {
                            //Witness saw victim die
                            BlockPos dangerPos;

                            if (Sight.canSee(witness, source))
                            {
                                //Witness saw everything
                                wasSeen = true;

                                Threat.set(witness, killer, serverSettings.threat.allyKilledThreat);
                                dangerPos = killer.getPosition();
                            }
                            else
                            {
                                //Witness saw ally die without seeing killer
                                Threat.set(witness, null, serverSettings.threat.allyKilledThreat);
                                dangerPos = victim.getPosition();
                            }

                            Communication.warn(witness, dangerPos);
                            if (witness instanceof EntityLiving)
                            {
                                AIDynamicStealth stealthAI = AIDynamicStealth.getStealthAI((EntityLiving) witness);
                                if (stealthAI != null)
                                {
                                    stealthAI.fleeIfYouShould(0);

                                    if (stealthAI.isFleeing()) stealthAI.lastKnownPosition = killer.getPosition();
                                }
                            }
                        }
                    }
                }
            }

            if (!wasSeen && !EntityThreatData.isPassive(victim))
            {
                //Target's friends didn't see
                if (!Sight.canSee(victim, source))
                {
                    //Target cannot see us
                    if (Threat.getTarget(victim) != source)
                    {
                        //Target is not searching for *us*
                        if (!MinecraftForge.EVENT_BUS.post(new AssassinationEvent(killer, victim)))
                        {
                            //Assassinations
                            ItemStack itemStack = killer.getHeldItemMainhand();
                            WeaponEntry weaponEntry = WeaponEntry.get(itemStack, WeaponEntry.TYPE_ASSASSINATION);

                            for (PotionEffect potionEffect : weaponEntry.attackerEffects)
                            {
                                killer.addPotionEffect(new PotionEffect(potionEffect));
                            }
                        }
                    }
                }
            }
        }


        //Remove from threat data
        Threat.removeTargetFromAll(victim);
        if (victim instanceof EntityLiving) Threat.remove(victim);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void entityAttackedPre(LivingHurtEvent event)
    {
        EntityLivingBase victim = event.getEntityLiving();

        DamageSource dmgSource = event.getSource();
        Entity source = dmgSource.getTrueSource();
        if (source == null) source = dmgSource.getImmediateSource();

        if (source instanceof EntityLivingBase)
        {
            EntityLivingBase attacker = (EntityLivingBase) source;

            //Remove invisibility and blindness if set to do so
            if (serverSettings.interactions.attack.removeInvisibilityOnHit)
            {
                attacker.removePotionEffect(MobEffects.INVISIBILITY);
                victim.removePotionEffect(MobEffects.INVISIBILITY);
            }
            if (serverSettings.interactions.attack.removeBlindnessOnHit)
            {
                attacker.removePotionEffect(MobEffects.BLINDNESS);
                victim.removePotionEffect(MobEffects.BLINDNESS);
            }

            if (attacker.isEntityAlive())
            {
                //Normal attacks
                ItemStack itemStack = attacker.getHeldItemMainhand();
                WeaponEntry weaponEntry = WeaponEntry.get(itemStack, WeaponEntry.TYPE_NORMAL);

                if (weaponEntry.armorPenetration) dmgSource.setDamageBypassesArmor();
                event.setAmount((float) (event.getAmount() * weaponEntry.damageMultiplier));

                for (PotionEffect potionEffect : weaponEntry.attackerEffects)
                {
                    attacker.addPotionEffect(new PotionEffect(potionEffect));
                }
                for (PotionEffect potionEffect : weaponEntry.victimEffects)
                {
                    victim.addPotionEffect(new PotionEffect(potionEffect));
                }

                if (weaponEntry.consumeItem && !(attacker instanceof EntityPlayer && ((EntityPlayer) attacker).capabilities.isCreativeMode) && !itemStack.getItem().equals(Items.AIR)) itemStack.grow(-1);


                //Stealth attacks
                if (!Sight.canSee(victim, attacker))
                {
                    if (!MinecraftForge.EVENT_BUS.post(new StealthAttackEvent(victim, dmgSource, event.getAmount())))
                    {
                        itemStack = attacker.getHeldItemMainhand();
                        weaponEntry = WeaponEntry.get(itemStack, WeaponEntry.TYPE_STEALTH);

                        if (weaponEntry.armorPenetration) dmgSource.setDamageBypassesArmor();
                        event.setAmount((float) (event.getAmount() * weaponEntry.damageMultiplier));

                        for (PotionEffect potionEffect : weaponEntry.attackerEffects)
                        {
                            attacker.addPotionEffect(new PotionEffect(potionEffect));
                        }
                        for (PotionEffect potionEffect : weaponEntry.victimEffects)
                        {
                            victim.addPotionEffect(new PotionEffect(potionEffect));
                        }

                        if (weaponEntry.consumeItem && !(attacker instanceof EntityPlayer && ((EntityPlayer) attacker).capabilities.isCreativeMode) && !itemStack.getItem().equals(Items.AIR)) itemStack.grow(-1);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void entityAttackedPost(LivingHurtEvent event) throws InvocationTargetException, IllegalAccessException
    {
        if (event.isCanceled() && event.getResult() == Event.Result.DENY) return;

        EntityLivingBase target = event.getEntityLiving();

        Entity source = event.getSource().getTrueSource();
        if (source == null) source = event.getSource().getImmediateSource();

        if (target instanceof EntityLiving)
        {
            EntityLiving livingTarget = (EntityLiving) target;

            if (source instanceof EntityLivingBase)
            {
                EntityLivingBase livingBaseSource = (EntityLivingBase) source;

                boolean updateTarget = true;
                boolean newThreatTarget = false;


                //Flee if you should
                AIDynamicStealth stealthAI = AIDynamicStealth.getStealthAI(livingTarget);
                if (stealthAI != null)
                {
                    if (event.isCanceled()) stealthAI.fleeIfYouShould(0);
                    else stealthAI.fleeIfYouShould(-event.getAmount());

                    if (stealthAI.isFleeing()) stealthAI.lastKnownPosition = source.getPosition();
                }


                //Threat
                Threat.ThreatData threatData = Threat.get(livingTarget);
                EntityLivingBase threatTarget = threatData.target;
                int threat = threatData.threatLevel;

                if (threatTarget == null || threat == 0)
                {
                    //Hit by entity when no target is set; this includes...
                    //...getting hit while out-of-combat
                    //...getting hit after previous target has been killed
                    //...getting hit when no target has been seen so far
                    Threat.set(livingTarget, livingBaseSource, threat + (int) (Tools.max(event.getAmount(), 1) * serverSettings.threat.attackedThreatMultiplierInitial / livingTarget.getMaxHealth()));
                    newThreatTarget = true;
                }
                else if (stealthAI != null && stealthAI.isFleeing())
                {
                    //Be brave, Sir Robin
                    if (serverSettings.ai.flee.increaseOnDamage) Threat.setThreat(livingTarget, threat + (int) (event.getAmount() * serverSettings.threat.attackedThreatMultiplierTarget / livingTarget.getMaxHealth()));
                }
                else if (threatTarget != source)
                {
                    //In combat (not fleeing), and hit by an entity besides our threat target
                    double threatChangeFactor = event.getAmount() / livingTarget.getMaxHealth();
                    threat -= threatChangeFactor * serverSettings.threat.attackedThreatMultiplierOther;
                    if (threat <= 0)
                    {
                        //Switching targets
                        Threat.set(livingTarget, livingBaseSource, (int) (threatChangeFactor * serverSettings.threat.attackedThreatMultiplierInitial));
                        newThreatTarget = true;
                    }
                    else
                    {
                        Threat.setThreat(livingTarget, threat);
                        updateTarget = false;
                    }
                }
                else
                {
                    //In combat (not fleeing), and hit by threat target
                    Threat.setThreat(livingTarget, threat + (int) (event.getAmount() * serverSettings.threat.attackedThreatMultiplierTarget / livingTarget.getMaxHealth()));
                }

                if (updateTarget)
                {
                    //Threat targeting already updated

                    //Update vanilla targeting
                    livingTarget.setAttackTarget(livingBaseSource);

                    //Look toward damage
                    float newYaw = (float) (TRIG_TABLE.arctanFullcircle(target.posZ, target.posX, source.posZ, source.posX) / Math.PI * 180);
                    makeLivingLookDirection(livingTarget, newYaw);

                    if (!(livingTarget instanceof EntitySlime))
                    {
                        //This is mostly for setting/resetting things when eg. you hit an entity that is in the middle of a task, and they don't see you (even after you hit them)

                        livingTarget.getNavigator().clearPath();

                        if (stealthAI != null)
                        {
                            int distance = (int) Math.sqrt(source.getDistanceSq(livingTarget));
                            stealthAI.restart(MCTools.randomPos(source.getPosition(), Tools.min(distance >> 1, 7), Tools.min(distance >> 2, 4)));
                        }
                    }
                }

                if (newThreatTarget && !Sight.canSee(livingTarget, livingBaseSource, false)) Threat.setTarget(livingTarget, null);


                //Warn others
                BlockPos warnPos = null;
                if (stealthAI != null) warnPos = stealthAI.lastKnownPosition;
                if (warnPos == null) warnPos = livingTarget.getPosition();
                Communication.warn(livingTarget, warnPos);
            }
        }
    }

    public static void makeLivingLookDirection(EntityLiving living, float directionDegrees) throws InvocationTargetException, IllegalAccessException
    {
        living.rotationYaw = directionDegrees;
        living.prevRotationYaw = directionDegrees;
        living.rotationYawHead = directionDegrees;
        living.prevRotationYawHead = directionDegrees;

        if (living instanceof EntitySlime)
        {
            //Look toward damage (slime)
            for (EntityAITasks.EntityAITaskEntry task : ((EntitySlime) living).tasks.taskEntries)
            {
                if (task.action instanceof AISlimeFaceRandomEdit)
                {
                    ((AISlimeFaceRandomEdit) task.action).setDirection(directionDegrees, true);
                    break;
                }
            }
        }
    }


    @SubscribeEvent
    public static void entityConstructing(EntityEvent.EntityConstructing event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLivingBase)
        {
            //Add new stealth-related attributes
            Attributes.addAttributes((EntityLivingBase) entity);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void entityJoin(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLivingBase && !MCTools.isClient(entity.world))
        {
            if (entity instanceof EntityLiving)
            {
                EntityLiving living = (EntityLiving) entity;
                if (!Compat.customnpcs || !(NpcAPI.Instance().getIEntity(living) instanceof ICustomNpc)) livingJoinWorld(living);
            }
        }
    }

    public static void livingJoinWorld(EntityLiving living)
    {
        //Set the new senses handler for all living entities (not including players)
        try
        {
            sensesField.set(living, new EntitySensesEdit(living));
            lookHelperField.set(living, new EntityLookHelperEdit(living));

            if (living instanceof AbstractSkeleton)
            {
                abstractSkeletonAIArrowAttackField.set(living, new AIAttackRangedBowEdit<AbstractSkeleton>((EntityAIAttackRangedBow) abstractSkeletonAIArrowAttackField.get(living)));
                abstractSkeletonAIAttackOnCollideField.set(living, new AIAttackMeleeEdit((EntityAIAttackMelee) abstractSkeletonAIAttackOnCollideField.get(living)));
            }

            //Entity AI task replacements
            replaceTasks(living.tasks, living);
            replaceTasks(living.targetTasks, living);

            //Entity AI task additions
            if (!EntityThreatData.bypassesThreat(living)) addTasks(living.targetTasks, living.tasks, living);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(137, false);
        }
    }

    private static void replaceTasks(EntityAITasks tasks, EntityLiving living) throws Exception
    {
        Compat.replaceNPEAttackTargetTasks(living);

        Set<EntityAITasks.EntityAITaskEntry> taskSet = tasks.taskEntries;
        EntityAITasks.EntityAITaskEntry[] taskArray = new EntityAITasks.EntityAITaskEntry[taskSet.size()];
        taskSet.toArray(taskArray);
        for (EntityAITasks.EntityAITaskEntry task : taskArray)
        {
            Class actionClass = task.action.getClass();

            if (actionClass == EntityAILookIdle.class) tasks.removeTask(task.action);

                //All done (excluded EntityAIVillagerInteract)
                //EntityAIVillagerInteract is fine as-is, because "they can talk to each other to find each other when not visible"
            else if (actionClass == EntityAIWatchClosest.class) replaceTask(tasks, task, new AIWatchClosestEdit((EntityAIWatchClosest) task.action));
            else if (actionClass == EntityAIWatchClosest2.class) replaceTask(tasks, task, new AIWatchClosestEdit((EntityAIWatchClosest) task.action, true));

                //All done
            else if (actionClass == EntityAIAttackMelee.class) replaceTask(tasks, task, new AIAttackMeleeEdit((EntityAIAttackMelee) task.action));
            else if (actionClass.getName().equals("net.minecraft.entity.monster.AbstractSkeleton$1")) replaceTask(tasks, task, new AIAttackMeleeEdit((EntityAIAttackMelee) task.action));
            else if (actionClass == aiEvilAttackClass) replaceTask(tasks, task, new AIAttackMeleeEdit((EntityAIAttackMelee) task.action));
            else if (actionClass == aiBearMeleeClass) replaceTask(tasks, task, new AIBearAttackEdit((EntityAIAttackMelee) task.action));
            else if (actionClass == aiSpiderAttackClass) replaceTask(tasks, task, new AISpiderAttackEdit((EntityAIAttackMelee) task.action));
            else if (actionClass == EntityAIZombieAttack.class) replaceTask(tasks, task, new AIZombieAttackEdit((EntityAIZombieAttack) task.action));

                //All done (except enderman stuff)
            else if (actionClass == EntityAINearestAttackableTarget.class) replaceTask(tasks, task, new AINearestAttackableTargetEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == aiBearAttackPlayerClass) replaceTask(tasks, task, new AIBearAttackPlayerEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == aiSpiderTargetClass) replaceTask(tasks, task, new AISpiderTargetEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == aiLlamaDefendTarget) replaceTask(tasks, task, new AILlamaDefendEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == aiVindicatorJohnnyAttackClass) replaceTask(tasks, task, new AIJohnnyAttackEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == aiPigmanTargetAggressorClass) replaceTask(tasks, task, new AIPigmanTargetAggressorEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == aiShulkerAttackNearestClass) replaceTask(tasks, task, new AIShulkerAttackNearestEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == aiShulkerDefenseAttackClass) replaceTask(tasks, task, new AIShulkerDefenseAttackEdit((EntityAINearestAttackableTarget) task.action));

                //All done
            else if (actionClass == EntityAIHurtByTarget.class) replaceTask(tasks, task, new AIHurtByTargetEdit((EntityAIHurtByTarget) task.action));
            else if (actionClass == aiPigmanHurtByAggressorClass) replaceTask(tasks, task, new AIPigmanHurtByAggressorEdit((EntityAIHurtByTarget) task.action));
            else if (actionClass == aiLlamaHurtByTargetClass) replaceTask(tasks, task, new AILlamaHurtByTargetEdit((EntityAIHurtByTarget) task.action));
            else if (actionClass == aiBearHurtByTargetClass) replaceTask(tasks, task, new AIBearHurtByTargetEdit((EntityAIHurtByTarget) task.action));

                //Random section
            else if (actionClass == EntityAIAttackRanged.class) replaceTask(tasks, task, new AIAttackRangedEdit((EntityAIAttackRanged) task.action));
            else if (actionClass == EntityAIAttackRangedBow.class) replaceTask(tasks, task, new AIAttackRangedBowEdit<>((EntityAIAttackRangedBow) task.action));
            else if (actionClass == EntityAIFindEntityNearestPlayer.class) replaceTask(tasks, task, new AIFindEntityNearestPlayerEdit((EntityAIFindEntityNearestPlayer) task.action));
            else if (actionClass == EntityAIFindEntityNearest.class) replaceTask(tasks, task, new AIFindEntityNearestEdit((EntityAIFindEntityNearest) task.action));
            else if (actionClass == EntityAITargetNonTamed.class) replaceTask(tasks, task, new AITargetNonTamedEdit((EntityAITargetNonTamed) task.action));
            else if (actionClass == EntityAIFollow.class) replaceTask(tasks, task, new AIParrotFollowEdit((EntityAIFollow) task.action));
            else if (actionClass == EntityAIDefendVillage.class) replaceTask(tasks, task, new AIDefendVillageEdit((EntityAIDefendVillage) task.action));
            else if (actionClass == EntityAIOwnerHurtByTarget.class) replaceTask(tasks, task, new AIOwnerHurtByTargetEdit((EntityAIOwnerHurtByTarget) task.action));
            else if (actionClass == EntityAIOwnerHurtTarget.class) replaceTask(tasks, task, new AIOwnerHurtTargetEdit((EntityAIOwnerHurtTarget) task.action));
            else if (actionClass == EntityAICreeperSwell.class) replaceTask(tasks, task, new AICreeperSwellEdit((EntityCreeper) living));
            else if (actionClass == aiSlimeFaceRandomClass) replaceTask(tasks, task, new AISlimeFaceRandomEdit((EntitySlime) living));
            else if (actionClass == EntityAIOcelotAttack.class) replaceTask(tasks, task, new AIOcelotAttackEdit(living));
            else if (actionClass == aiGuardianAttackClass) replaceTask(tasks, task, new AIGuardianAttackEdit((EntityGuardian) living));
            else if (actionClass == aiBlazeFireballAttackClass) replaceTask(tasks, task, new AIFireballAttackEdit((EntityBlaze) living));
            else if (actionClass == aiVexChargeAttackClass) replaceTask(tasks, task, new AIVexChargeAttackEdit((EntityVex) living));
            else if (actionClass == aiShulkerAttackClass) replaceTask(tasks, task, new AIShulkerAttackEdit((EntityShulker) living));
        }
    }

    private static void replaceTask(EntityAITasks tasks, EntityAITasks.EntityAITaskEntry oldTask, EntityAIBase newTask)
    {
        tasks.addTask(oldTask.priority, newTask);
        tasks.removeTask(oldTask.action);
    }

    private static void addTasks(EntityAITasks targetTasks, EntityAITasks tasks, EntityLiving living)
    {
        if (!EntityThreatData.bypassesThreat(living))
        {
            tasks.addTask(-7777777, new AIDynamicStealth(living, 1));
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws NoSuchFieldException, IllegalAccessException
    {
        Network.init();

        sensesField = ReflectionTool.getField(EntityLiving.class, "field_70723_bA", "senses");
        lookHelperField = ReflectionTool.getField(EntityLiving.class, "field_70749_g", "lookHelper");
        worldServerEntityTrackerField = ReflectionTool.getField(WorldServer.class, "field_73062_L", "entityTracker");

        abstractSkeletonAIArrowAttackField = ReflectionTool.getField(AbstractSkeleton.class, "field_85037_d", "aiArrowAttack");
        abstractSkeletonAIAttackOnCollideField = ReflectionTool.getField(AbstractSkeleton.class, "field_85038_e", "aiAttackOnCollide");

        aiSlimeFaceRandomClass = ReflectionTool.getInternalClass(EntitySlime.class, "AISlimeFaceRandom");
        aiEvilAttackClass = ReflectionTool.getInternalClass(EntityRabbit.class, "AIEvilAttack");
        aiBearMeleeClass = ReflectionTool.getInternalClass(EntityPolarBear.class, "AIMeleeAttack");
        aiSpiderAttackClass = ReflectionTool.getInternalClass(EntitySpider.class, "AISpiderAttack");
        aiSpiderTargetClass = ReflectionTool.getInternalClass(EntitySpider.class, "AISpiderTarget");
        aiBearAttackPlayerClass = ReflectionTool.getInternalClass(EntityPolarBear.class, "AIAttackPlayer");
        aiLlamaDefendTarget = ReflectionTool.getInternalClass(EntityLlama.class, "AIDefendTarget");
        aiPigmanHurtByAggressorClass = ReflectionTool.getInternalClass(EntityPigZombie.class, "AIHurtByAggressor");
        aiLlamaHurtByTargetClass = ReflectionTool.getInternalClass(EntityLlama.class, "AIHurtByTarget");
        aiPigmanTargetAggressorClass = ReflectionTool.getInternalClass(EntityPigZombie.class, "AITargetAggressor");
        aiVindicatorJohnnyAttackClass = ReflectionTool.getInternalClass(EntityVindicator.class, "AIJohnnyAttack");
        aiBearHurtByTargetClass = ReflectionTool.getInternalClass(EntityPolarBear.class, "AIHurtByTarget");
        aiGuardianAttackClass = ReflectionTool.getInternalClass(EntityGuardian.class, "AIGuardianAttack");
        aiBlazeFireballAttackClass = ReflectionTool.getInternalClass(EntityBlaze.class, "AIFireballAttack");
        aiVexChargeAttackClass = ReflectionTool.getInternalClass(EntityVex.class, "AIChargeAttack");
        aiShulkerAttackClass = ReflectionTool.getInternalClass(EntityShulker.class, "AIAttack");
        aiShulkerAttackNearestClass = ReflectionTool.getInternalClass(EntityShulker.class, "AIAttackNearest");
        aiShulkerDefenseAttackClass = ReflectionTool.getInternalClass(EntityShulker.class, "AIDefenseAttack");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //Compat init
        if (Loader.isModLoaded("lycanitesmobs")) Compat.lycanites = true;
        if (Loader.isModLoaded("ancientwarfare")) Compat.ancientwarfare = true;
        if (Loader.isModLoaded("neat")) Compat.neat = true;
        if (Loader.isModLoaded("statues")) Compat.statues = true;
        if (Loader.isModLoaded("customnpcs"))
        {
            Compat.customnpcs = true;
            MinecraftForge.EVENT_BUS.register(CompatCNPC.class);
        }

        AttackData.init();
    }
}
