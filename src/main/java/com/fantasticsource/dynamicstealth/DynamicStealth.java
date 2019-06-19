package com.fantasticsource.dynamicstealth;

import com.fantasticsource.dynamicstealth.client.HUD;
import com.fantasticsource.dynamicstealth.client.RenderAlterer;
import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.common.Network;
import com.fantasticsource.dynamicstealth.common.potions.Potions;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.compat.CompatCNPC;
import com.fantasticsource.dynamicstealth.compat.CompatDissolution;
import com.fantasticsource.dynamicstealth.config.ConfigHandler;
import com.fantasticsource.dynamicstealth.server.*;
import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import com.fantasticsource.dynamicstealth.server.ai.EntityAIData;
import com.fantasticsource.dynamicstealth.server.ai.edited.*;
import com.fantasticsource.dynamicstealth.server.entitytracker.EntityTrackerEdit;
import com.fantasticsource.dynamicstealth.server.event.EventData;
import com.fantasticsource.dynamicstealth.server.event.attacks.AssassinationEvent;
import com.fantasticsource.dynamicstealth.server.event.attacks.AttackData;
import com.fantasticsource.dynamicstealth.server.event.attacks.StealthAttackEvent;
import com.fantasticsource.dynamicstealth.server.event.attacks.WeaponEntry;
import com.fantasticsource.dynamicstealth.server.senses.EntitySensesEdit;
import com.fantasticsource.dynamicstealth.server.senses.EntityTouchData;
import com.fantasticsource.dynamicstealth.server.senses.HidingData;
import com.fantasticsource.dynamicstealth.server.senses.hearing.Communication;
import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightData;
import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;

import java.util.Set;

import static com.fantasticsource.dynamicstealth.common.Network.WRAPPER;
import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.server.threat.Threat.THREAT_TYPE.*;

@Mod(modid = DynamicStealth.MODID, name = DynamicStealth.NAME, version = DynamicStealth.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.016,)")
public class DynamicStealth
{
    public static final String MODID = "dynamicstealth";
    public static final String NAME = "Dynamic Stealth";
    public static final String VERSION = "1.12.2.078";
    public static final String CONFIG_VERSION = "1.12.2.077"; //The lowest compatible config version

    public static final TrigLookupTable TRIG_TABLE = new TrigLookupTable(1024);

    private static EntityLivingBase victimThreatTarget = null;

    static
    {
        ConfigHandler.init();
    }


    public DynamicStealth()
    {
        Attributes.init();

        MinecraftForge.EVENT_BUS.register(ServerTickTimer.class);
        MinecraftForge.EVENT_BUS.register(CombatTracker.class);
        MinecraftForge.EVENT_BUS.register(EntitySightData.class);
        MinecraftForge.EVENT_BUS.register(DynamicStealth.class);
        MinecraftForge.EVENT_BUS.register(Network.class);
        MinecraftForge.EVENT_BUS.register(Threat.class);
        MinecraftForge.EVENT_BUS.register(Sight.class);
        MinecraftForge.EVENT_BUS.register(Communication.class);
        MinecraftForge.EVENT_BUS.register(Potions.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
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
    public static void postConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        update();
    }

    public static void update()
    {
        AttackData.update();
        HUDData.update();
        EntityAIData.update();
        EntityTouchData.update();
        EntitySightData.update();
        EntityThreatData.update();
        EventData.update();

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null)
        {
            PlayerList playerList = server.getPlayerList();
            if (playerList != null)
            {
                for (EntityPlayerMP player : playerList.getPlayers())
                {
                    WRAPPER.sendTo(new Network.ClientInitPacket(player), player);
                }
            }
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
    public static void worldLoad(WorldEvent.Load event)
    {
        World world = event.getWorld();
        if (world instanceof WorldServer && serverSettings.senses.usePlayerSenses)
        {
            WorldServer worldServer = (WorldServer) world;
            worldServer.entityTracker = new EntityTrackerEdit(worldServer);
        }
    }


    @SubscribeEvent
    public static void playerLogon(PlayerEvent.PlayerLoggedInEvent event)
    {
        HidingData.load(event.player);
    }


    @SubscribeEvent
    public static void livingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        World world = livingBase.world;
        if (!world.isRemote && livingBase.isEntityAlive())
        {
            int type = livingBase instanceof EntityPlayerMP ? 1 : livingBase instanceof EntityLiving ? 2 : 0;

            if (type != 0 && serverSettings.senses.touch.touchEnabled && EntityTouchData.canFeelTouch(livingBase))
            {
                for (Entity felt : world.getEntitiesWithinAABBExcludingEntity(livingBase, livingBase.getEntityBoundingBox()))
                {
                    int feltType = felt instanceof EntityPlayerMP ? 1 : felt instanceof EntityLiving ? 2 : 0;

                    if (feltType != 0 && felt.isEntityAlive())
                    {
                        if (serverSettings.senses.touch.touchReveals)
                        {
                            if (type == 2 || !(livingBase instanceof FakePlayer)) livingBase.removePotionEffect(MobEffects.INVISIBILITY);
                            if (feltType == 2 || !(felt instanceof FakePlayer)) ((EntityLivingBase) felt).removePotionEffect(MobEffects.INVISIBILITY);
                        }

                        if (!(felt instanceof EntityBat) && !MCTools.isRidingOrRiddenBy(livingBase, felt))
                        {
                            switch (type)
                            {
                                case 1:
                                    //TODO add indicator for players
                                    break;
                                case 2:
                                    EntityLiving feelerLiving = (EntityLiving) livingBase;

                                    AIDynamicStealth ai = AIDynamicStealth.getStealthAI(feelerLiving);
                                    if (ai != null)
                                    {
                                        if (ai.isFleeing()) return;

                                        makeLivingLookTowardEntity(feelerLiving, felt);
                                        feelerLiving.getNavigator().clearPath();
                                        if (ai.getMode() != AIDynamicStealth.MODE_NONE) ai.restart(feelerLiving.getPosition());
                                    }
                                    else
                                    {
                                        makeLivingLookTowardEntity(feelerLiving, felt);
                                        feelerLiving.getNavigator().clearPath();
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            if (type == 2)
            {
                CombatTracker.pathReachesThreatTarget((EntityLiving) livingBase);
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
    public static void entityDeathPre(LivingDeathEvent event)
    {
        EntityLivingBase victim = event.getEntityLiving();

        DamageSource dmgSource = event.getSource();
        Entity source = dmgSource.getTrueSource();
        Entity immediate = dmgSource.getImmediateSource();
        boolean isMelee = source != null && source == immediate;
        if (source == null) source = dmgSource.getImmediateSource();

        Threat.set(victim, null, 0);

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

                    if (Threat.getTarget(witness) == victim)
                    {
                        if (Sight.canSee(witness, victim, true))
                        {
                            if (MCTools.isOwned(witness)) Threat.set(witness, null, 0);
                            else Threat.clearTarget(witness);
                            Communication.notifyDead(witness, victim);
                        }
                    }
                    else if (HelperSystem.isAlly(witness, victim))
                    {
                        if (Sight.canSee(witness, victim, false))
                        {
                            //Witness saw victim die
                            BlockPos threatPos;
                            if (Sight.canSee(witness, source, true))
                            {
                                //Witness saw everything
                                wasSeen = true;
                                threatPos = killer.getPosition();
                            }
                            else
                            {
                                //Witness saw ally die without seeing killer
                                threatPos = victim.getPosition();
                            }
                            Threat.apply(witness, killer, serverSettings.threat.allyKilledThreat, GEN_ALLY_KILLED, wasSeen);
                            Communication.warn(witness, killer, threatPos, wasSeen);

                            if (witness instanceof EntityLiving)
                            {
                                AIDynamicStealth stealthAI = AIDynamicStealth.getStealthAI((EntityLiving) witness);
                                if (stealthAI != null)
                                {
                                    stealthAI.fleeIfYouShould(0);
                                    if (stealthAI.isFleeing()) stealthAI.lastKnownPosition = threatPos;
                                }
                            }
                        }
                    }
                }
            }

            if (!wasSeen && !EntityThreatData.isPassive(victim))
            {
                //Was melee and target's friends didn't see
                if (!Sight.canSee(victim, source, true))
                {
                    //Target cannot see us
                    if (victimThreatTarget != source)
                    {
                        //Target is not searching for / fleeing from *us specifically*
                        if (!(killer instanceof FakePlayer) && !MinecraftForge.EVENT_BUS.post(new AssassinationEvent(killer, victim)))
                        {
                            //Assassinations
                            WeaponEntry weaponEntry = WeaponEntry.get(isMelee ? killer.getHeldItemMainhand() : null, WeaponEntry.TYPE_ASSASSINATION);


                            for (PotionEffect potionEffect : weaponEntry.attackerEffects)
                            {
                                killer.addPotionEffect(new PotionEffect(potionEffect));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void entityDeathPost(LivingDeathEvent event)
    {
        event.getEntityLiving().clearActivePotions();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void entityAttackedPre(LivingHurtEvent event)
    {
        EntityLivingBase victim = event.getEntityLiving();
        victimThreatTarget = Threat.getTarget(victim);

        DamageSource dmgSource = event.getSource();
        Entity source = dmgSource.getTrueSource();
        Entity immediate = dmgSource.getImmediateSource();
        boolean isMelee = source != null && source == immediate;
        if (source == null) source = immediate;

        if (source instanceof EntityLivingBase)
        {
            EntityLivingBase attacker = (EntityLivingBase) source;

            //Remove invisibility and blindness if set to do so
            if (isMelee)
            {
                if (serverSettings.interactions.attack.removeInvisibilityOnHit)
                {
                    if (!(attacker instanceof FakePlayer)) attacker.removePotionEffect(MobEffects.INVISIBILITY);
                    victim.removePotionEffect(MobEffects.INVISIBILITY);
                }
                if (serverSettings.interactions.attack.removeBlindnessOnHit)
                {
                    if (!(attacker instanceof FakePlayer)) attacker.removePotionEffect(MobEffects.BLINDNESS);
                    victim.removePotionEffect(MobEffects.BLINDNESS);
                }
            }
            else
            {
                if (serverSettings.interactions.rangedAttack.removeInvisibilityOnHit)
                {
                    if (!(attacker instanceof FakePlayer)) attacker.removePotionEffect(MobEffects.INVISIBILITY);
                    victim.removePotionEffect(MobEffects.INVISIBILITY);
                }
                if (serverSettings.interactions.rangedAttack.removeBlindnessOnHit)
                {
                    if (!(attacker instanceof FakePlayer)) attacker.removePotionEffect(MobEffects.BLINDNESS);
                    victim.removePotionEffect(MobEffects.BLINDNESS);
                }
            }

            if (isMelee && attacker.isEntityAlive() && !(attacker instanceof FakePlayer))
            {
                //Normal attacks (melee only)
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


                //Stealth attacks (melee only)
                if (!Sight.canSee(victim, attacker, true))
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
    public static void entityAttackedPost(LivingHurtEvent event)
    {
        if (event.isCanceled() && event.getResult() == Event.Result.DENY) return;

        EntityLivingBase targetBase = event.getEntityLiving();

        DamageSource damageSource = event.getSource();
        Entity source = damageSource.getTrueSource();
        if (source == null) source = damageSource.getImmediateSource();

        if (targetBase instanceof EntityLiving)
        {
            EntityLiving target = (EntityLiving) targetBase;

            if (source instanceof EntityLivingBase)
            {
                EntityLivingBase attacker = (EntityLivingBase) source;


                //Look toward damage, check sight, and set perceived position
                makeLivingLookTowardEntity(target, attacker);
                boolean canSee = Sight.canSee(target, attacker, true, false, false);
                BlockPos perceivedPos = attacker.getPosition();
                if (!canSee)
                {
                    int distance = (int) target.getDistance(attacker);
                    MCTools.randomPos(perceivedPos, Tools.min(distance >> 1, 7), Tools.min(distance >> 2, 4));
                }


                //Warn others (for both attacker and target)
                Communication.warn(target, attacker, perceivedPos, canSee);
                Communication.warn(attacker, target, target.getPosition(), true);


                //Threat, AI, and vanilla attack target
                AIDynamicStealth stealthAI = AIDynamicStealth.getStealthAI(target);
                boolean hasAI = stealthAI != null;

                Threat.ThreatData threatData = Threat.get(target);
                EntityLivingBase threatTarget = threatData.target;

                if (hasAI && (stealthAI.isFleeing() || stealthAI.getMode() == AIDynamicStealth.MODE_COWER))
                {
                    //Attacked while fleeing
                    if (serverSettings.ai.flee.increaseOnDamage) Threat.apply(target, attacker, event.getAmount(), GEN_ATTACKED_DURING_FLEE, canSee, damageSource);
                    target.setAttackTarget(null);
                    stealthAI.restart(perceivedPos);
                }
                else
                {
                    //Attacked while not fleeing
                    Threat.apply(target, attacker, event.getAmount(), GEN_ATTACKED, canSee, damageSource);
                    target.setAttackTarget(threatTarget);
                    if (hasAI) stealthAI.restart(perceivedPos);
                }


                //Flee if you should
                if (hasAI)
                {
                    if (event.isCanceled()) stealthAI.fleeIfYouShould(0);
                    else stealthAI.fleeIfYouShould(-event.getAmount());
                }
            }
        }

        //Threat for attacker
        if (source instanceof EntityLiving)
        {
            EntityLiving livingSource = (EntityLiving) source;
            Threat.apply(livingSource, targetBase, event.getAmount(), GEN_DAMAGE_DEALT, livingSource.senses.canSee(targetBase), damageSource);
        }
    }


    public static void makeLivingLookTowardEntity(EntityLiving living, Entity target)
    {
        if (Compat.testdummy && living.getClass().getName().equals("boni.dummy.EntityDummy")) return;

        Vec3d pos1 = living.getPositionVector().add(new Vec3d(0, living.getEyeHeight(), 0));
        Vec3d pos2 = target.getPositionVector().add(new Vec3d(0, target.height * 0.5, 0));

        float fYaw = (float) MCTools.getYawDeg(pos1, pos2, TRIG_TABLE);
        living.rotationYaw = fYaw;
        living.rotationYawHead = fYaw;

        living.rotationPitch = (float) MCTools.getPitchDeg(pos1, pos2, TRIG_TABLE);

        if (living instanceof EntitySlime)
        {
            //Look toward damage (slime)
            for (EntityAITasks.EntityAITaskEntry task : ((EntitySlime) living).tasks.taskEntries)
            {
                if (task.action instanceof AISlimeFaceRandomEdit)
                {
                    ((AISlimeFaceRandomEdit) task.action).setDirection(fYaw, true);
                    break;
                }
            }
        }

        living.lookHelper.setLookPositionWithEntity(target, 180, 180);
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
        if (entity instanceof EntityLiving)
        {
            if (!Compat.customnpcs || !(NpcAPI.Instance().getIEntity(entity) instanceof ICustomNpc)) livingJoinWorld((EntityLiving) entity);
        }
    }

    public static void livingJoinWorld(EntityLiving living)
    {
        //Set the new senses handler for all living entities (not including players)
        try
        {
            living.lookHelper = new EntityLookHelperEdit(living);

            if (!living.world.isRemote) //Server-side
            {
                living.senses = new EntitySensesEdit(living);

                if (living instanceof AbstractSkeleton)
                {
                    AbstractSkeleton abstractSkeleton = (AbstractSkeleton) living;
                    abstractSkeleton.aiArrowAttack = new AIAttackRangedBowEdit<AbstractSkeleton>(abstractSkeleton.aiArrowAttack);
                    abstractSkeleton.aiAttackOnCollide = new AIAttackMeleeEdit(abstractSkeleton.aiAttackOnCollide);
                }

                //Entity AI task replacements
                replaceTasks(living.tasks, living);
                replaceTasks(living.targetTasks, living);
                Compat.replaceNPEAttackTargetTasks(living);

                //Entity AI task additions
                addTasks(living.targetTasks, living.tasks, living);
            }
            else //Client-side
            {
                RenderAlterer.replaceLayers(living);
            }
        }
        catch (Exception e)
        {
            MCTools.crash(e, 137, false);
        }
    }

    private static void replaceTasks(EntityAITasks tasks, EntityLiving living)
    {
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

                //All done (one near end due to string comparison)
            else if (actionClass == EntityAIAttackMelee.class) replaceTask(tasks, task, new AIAttackMeleeEdit((EntityAIAttackMelee) task.action));
            else if (actionClass == EntityRabbit.AIEvilAttack.class) replaceTask(tasks, task, new AIAttackMeleeEdit((EntityAIAttackMelee) task.action));
            else if (actionClass == EntityPolarBear.AIMeleeAttack.class) replaceTask(tasks, task, new AIBearAttackEdit((EntityAIAttackMelee) task.action));
            else if (actionClass == EntitySpider.AISpiderAttack.class) replaceTask(tasks, task, new AISpiderAttackEdit((EntityAIAttackMelee) task.action));
            else if (actionClass == EntityAIZombieAttack.class) replaceTask(tasks, task, new AIZombieAttackEdit((EntityAIZombieAttack) task.action));

                //All done (except enderman stuff)
            else if (actionClass == EntityAINearestAttackableTarget.class) replaceTask(tasks, task, new AINearestAttackableTargetEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == EntityPolarBear.AIAttackPlayer.class) replaceTask(tasks, task, new AIBearAttackPlayerEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == EntitySpider.AISpiderTarget.class) replaceTask(tasks, task, new AISpiderTargetEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == EntityLlama.AIDefendTarget.class) replaceTask(tasks, task, new AILlamaDefendEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == EntityVindicator.AIJohnnyAttack.class) replaceTask(tasks, task, new AIJohnnyAttackEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == EntityPigZombie.AITargetAggressor.class) replaceTask(tasks, task, new AIPigmanTargetAggressorEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == EntityShulker.AIAttackNearest.class) replaceTask(tasks, task, new AIShulkerAttackNearestEdit((EntityAINearestAttackableTarget) task.action));
            else if (actionClass == EntityShulker.AIDefenseAttack.class) replaceTask(tasks, task, new AIShulkerDefenseAttackEdit((EntityAINearestAttackableTarget) task.action));

                //All done
            else if (actionClass == EntityAIHurtByTarget.class) replaceTask(tasks, task, new AIHurtByTargetEdit((EntityAIHurtByTarget) task.action));
            else if (actionClass == EntityPigZombie.AIHurtByAggressor.class) replaceTask(tasks, task, new AIPigmanHurtByAggressorEdit((EntityAIHurtByTarget) task.action));
            else if (actionClass == EntityLlama.AIHurtByTarget.class) replaceTask(tasks, task, new AILlamaHurtByTargetEdit((EntityAIHurtByTarget) task.action));
            else if (actionClass == EntityPolarBear.AIHurtByTarget.class) replaceTask(tasks, task, new AIBearHurtByTargetEdit((EntityAIHurtByTarget) task.action));

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
            else if (actionClass == EntitySlime.AISlimeFaceRandom.class) replaceTask(tasks, task, new AISlimeFaceRandomEdit((EntitySlime) living));
            else if (actionClass == EntityAIOcelotAttack.class) replaceTask(tasks, task, new AIOcelotAttackEdit(living));
            else if (actionClass == EntityGuardian.AIGuardianAttack.class) replaceTask(tasks, task, new AIGuardianAttackEdit((EntityGuardian) living));
            else if (actionClass == EntityBlaze.AIFireballAttack.class) replaceTask(tasks, task, new AIFireballAttackEdit((EntityBlaze) living));
            else if (actionClass == EntityVex.AIChargeAttack.class) replaceTask(tasks, task, new AIVexChargeAttackEdit((EntityVex) living));
            else if (actionClass == EntityShulker.AIAttack.class) replaceTask(tasks, task, new AIShulkerAttackEdit((EntityShulker) living));

                //Pet teleport prevention (depending on config)
            else if (actionClass == EntityAIFollowOwner.class) replaceTask(tasks, task, new AIFollowOwnerEdit((EntityTameable) living, (EntityAIFollowOwner) task.action));
            else if (actionClass == EntityAIFollowOwnerFlying.class) replaceTask(tasks, task, new AIFollowOwnerFlyingEdit((EntityTameable) living, (EntityAIFollowOwner) task.action));

            else if (actionClass.getName().equals("net.minecraft.entity.monster.AbstractSkeleton$1")) replaceTask(tasks, task, new AIAttackMeleeEdit((EntityAIAttackMelee) task.action));
            else if (actionClass.getName().equals("com.lycanitesmobs.core.entity.ai.EntityAIWatchClosest")) replaceTask(tasks, task, new AIWatchClosestEdit(living, EntityLivingBase.class, 0.02f));
            else if (actionClass.getName().equals("com.lycanitesmobs.core.entity.ai.EntityAILookIdle")) tasks.removeTask(task.action);
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
            tasks.addTask(-7777777, AIDynamicStealth.getInstance(living));
        }
    }

    @EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new Commands());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws Exception
    {
        ConfigHandler.update();

        Network.init();
//        Keys.init(event); TODO
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //Compat init
        if (Loader.isModLoaded("lycanitesmobs")) Compat.lycanites = true;
        if (Loader.isModLoaded("thermalfoundation")) Compat.thermalfoundation = true;
        if (Loader.isModLoaded("ancientwarfare")) Compat.ancientwarfare = true;
        if (Loader.isModLoaded("abyssalcraft")) Compat.abyssalcraft = true;
        if (Loader.isModLoaded("emberroot")) Compat.emberroot = true;
        if (Loader.isModLoaded("primitivemobs")) Compat.primitivemobs = true;
        if (Loader.isModLoaded("defiledlands")) Compat.defiledlands = true;
        if (Loader.isModLoaded("neat")) Compat.neat = true;
        if (Loader.isModLoaded("testdummy")) Compat.testdummy = true;
        if (Loader.isModLoaded("statues")) Compat.statues = true;
        if (Loader.isModLoaded("magma_monsters")) Compat.magma_monsters = true;
        if (Loader.isModLoaded("dissolution"))
        {
            Compat.dissolution = true;
            MinecraftForge.EVENT_BUS.register(CompatDissolution.class);
        }
        if (Loader.isModLoaded("customnpcs"))
        {
            Compat.customnpcs = true;
            MinecraftForge.EVENT_BUS.register(CompatCNPC.class);
        }

        update();
    }
}
