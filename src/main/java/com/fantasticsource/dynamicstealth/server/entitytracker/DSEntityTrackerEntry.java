package com.fantasticsource.dynamicstealth.server.entitytracker;

import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.tools.Tools;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DSEntityTrackerEntry extends EntityTrackerEntry
{
    protected static final Logger LOGGER = LogManager.getLogger();

    protected final Entity entity;
    protected final boolean isLivingBase, isPlayer;
    protected final EntityLivingBase livingBase;
    protected final EntityPlayerMP player;
    protected final int updateFrequency;
    protected final boolean sendVelocityUpdates;

    protected int encodedRotationYaw, encodedRotationPitch, lastEncodedHeadMotion;
    protected double lastMotionX, lastMotionY, lastMotionZ;

    private List<Entity> passengers = Collections.emptyList();


    public DSEntityTrackerEntry(Entity entity, int maxRange, int currentRange, int updateFrequency, boolean sendVelocityUpdates)
    {
        super(entity, maxRange, currentRange, updateFrequency, sendVelocityUpdates);

        this.entity = entity;
        isLivingBase = entity instanceof EntityLivingBase;
        livingBase = isLivingBase ? (EntityLivingBase) entity : null;
        isPlayer = entity instanceof EntityPlayerMP;
        player = isPlayer ? (EntityPlayerMP) entity : null;

        this.updateFrequency = updateFrequency;
        this.sendVelocityUpdates = sendVelocityUpdates;

        encodedRotationYaw = MathHelper.floor(entity.rotationYaw * 256 / 360);
        encodedRotationPitch = MathHelper.floor(entity.rotationPitch * 256 / 360);

        lastEncodedHeadMotion = MathHelper.floor(entity.getRotationYawHead() * 256 / 360);
    }

    public boolean equals(Object entityTrackerEntry)
    {
        return entityTrackerEntry instanceof DSEntityTrackerEntry && ((DSEntityTrackerEntry) entityTrackerEntry).entity.getEntityId() == entity.getEntityId();
    }

    public int hashCode()
    {
        return entity.getEntityId();
    }

    public void updatePlayerList(List<EntityPlayer> players)
    {
        List<Entity> newPassengers = entity.getPassengers();
        if (!newPassengers.equals(passengers))
        {
            passengers = newPassengers;
            sendPacketToTrackedPlayers(new SPacketSetPassengers(entity));
        }

        if (updateCounter % updateFrequency == 0 || entity.isAirBorne || entity.getDataManager().isDirty())
        {
            if (updateCounter > 0)
            {
                if (sendVelocityUpdates || (isLivingBase && livingBase.isElytraFlying()))
                {
                    if (lastMotionX != entity.motionX || lastMotionY != entity.motionY || lastMotionZ != entity.motionZ)
                    {
                        lastMotionX = entity.motionX;
                        lastMotionY = entity.motionY;
                        lastMotionZ = entity.motionZ;
                        sendPacketToTrackedPlayers(new SPacketEntityVelocity(entity.getEntityId(), entity.motionX, entity.motionY, entity.motionZ));
                    }
                }

                sendPacketToTrackedPlayers(new SPacketEntityTeleport(entity));
            }

            entity.rotationYaw = Tools.posMod(entity.rotationYaw, 360);
            entity.rotationPitch = Tools.posMod(entity.rotationPitch, 360);
            encodedRotationYaw = MathHelper.floor(entity.rotationYaw * 256 / 360);
            encodedRotationPitch = MathHelper.floor(entity.rotationPitch * 256 / 360);

            sendMetadata();

            int newEncodedHeadRotation = MathHelper.floor(entity.getRotationYawHead() * 256 / 360);
            if (Math.abs(newEncodedHeadRotation - lastEncodedHeadMotion) >= 1)
            {
                sendPacketToTrackedPlayers(new SPacketEntityHeadLook(entity, (byte) newEncodedHeadRotation));
                lastEncodedHeadMotion = newEncodedHeadRotation;
            }

            entity.isAirBorne = false;
        }

        ++updateCounter;

        if (entity.velocityChanged)
        {
            sendToTrackingAndSelf(new SPacketEntityVelocity(entity));
            entity.velocityChanged = false;
        }
    }

    private void sendMetadata()
    {
        EntityDataManager entitydatamanager = entity.getDataManager();

        if (entitydatamanager.isDirty())
        {
            sendToTrackingAndSelf(new SPacketEntityMetadata(entity.getEntityId(), entitydatamanager, false));
        }

        if (isLivingBase)
        {
            AttributeMap attributemap = (AttributeMap) livingBase.getAttributeMap();
            Set<IAttributeInstance> set = attributemap.getDirtyInstances();

            if (!set.isEmpty())
            {
                sendToTrackingAndSelf(new SPacketEntityProperties(entity.getEntityId(), set));
            }

            set.clear();
        }
    }

    public void sendPacketToTrackedPlayers(Packet<?> packetIn)
    {
        for (EntityPlayerMP entityplayermp : trackingPlayers)
        {
            entityplayermp.connection.sendPacket(packetIn);
        }
    }

    public void sendToTrackingAndSelf(Packet<?> packetIn)
    {
        sendPacketToTrackedPlayers(packetIn);

        if (isPlayer)
        {
            player.connection.sendPacket(packetIn);
        }
    }

    public void sendDestroyEntityPacketToTrackedPlayers()
    {
        for (EntityPlayerMP entityplayermp : trackingPlayers)
        {
            entity.removeTrackingPlayer(entityplayermp);
            entityplayermp.removeEntity(entity);
        }
    }

    public void removeFromTrackedPlayers(EntityPlayerMP playerMP)
    {
        if (trackingPlayers.contains(playerMP))
        {
            entity.removeTrackingPlayer(playerMP);
            playerMP.removeEntity(entity);
            trackingPlayers.remove(playerMP);
        }
    }

    public void updatePlayerEntity(EntityPlayerMP player)
    {
        if (player != entity)
        {
            if (isVisibleTo(player)) makePlayerTrackThis(player);
            else if (trackingPlayers.contains(player))
            {
                //Internal data alterations
                trackingPlayers.remove(player);

                //External data alterations
                entity.removeTrackingPlayer(player);
                player.removeEntity(entity);

                //Fire forge event
                ForgeEventFactory.onStopEntityTracking(entity, player);
            }
        }
    }

    public boolean isVisibleTo(EntityPlayerMP playerMP)
    {
        return Sight.canSee(playerMP, entity, true);
    }

    public void makePlayerTrackThis(EntityPlayerMP player)
    {
        if (!trackingPlayers.contains(player))
        {
            trackingPlayers.add(player);
            Packet<?> packet = createSpawnPacket();
            player.connection.sendPacket(packet);

            if (!entity.getDataManager().isEmpty())
            {
                player.connection.sendPacket(new SPacketEntityMetadata(entity.getEntityId(), entity.getDataManager(), true));
            }

            if (isLivingBase)
            {
                AttributeMap attributemap = (AttributeMap) livingBase.getAttributeMap();
                Collection<IAttributeInstance> collection = attributemap.getWatchedAttributes();

                if (!collection.isEmpty())
                {
                    player.connection.sendPacket(new SPacketEntityProperties(entity.getEntityId(), collection));
                }
            }

            lastMotionX = entity.motionX;
            lastMotionY = entity.motionY;
            lastMotionZ = entity.motionZ;

            //Send velocity; SPacketSpawnMob already contains velocities
            if (!(packet instanceof SPacketSpawnMob) && (sendVelocityUpdates || (isLivingBase && livingBase.isElytraFlying())))
            {
                player.connection.sendPacket(new SPacketEntityVelocity(entity.getEntityId(), entity.motionX, entity.motionY, entity.motionZ));
            }

            //Send equipment
            if (isLivingBase)
            {
                for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values())
                {
                    ItemStack itemstack = livingBase.getItemStackFromSlot(entityequipmentslot);

                    if (!itemstack.isEmpty())
                    {
                        player.connection.sendPacket(new SPacketEntityEquipment(entity.getEntityId(), entityequipmentslot, itemstack));
                    }
                }
            }

            //Send packet to set entity to display as sleeping in a bed
            if (isPlayer && this.player.isPlayerSleeping())
            {
                player.connection.sendPacket(new SPacketUseBed(this.player, new BlockPos(entity)));
            }

            //Send visual potion effects
            if (isLivingBase)
            {
                for (PotionEffect potioneffect : livingBase.getActivePotionEffects())
                {
                    player.connection.sendPacket(new SPacketEntityEffect(entity.getEntityId(), potioneffect));
                }
            }

            //Send riding entities
            if (!entity.getPassengers().isEmpty())
            {
                player.connection.sendPacket(new SPacketSetPassengers(entity));
            }

            //Send ridden entity
            if (entity.isRiding())
            {
                player.connection.sendPacket(new SPacketSetPassengers(entity.getRidingEntity())); //getRidingEntity DOES NOT GET THE RIDING ENTITY!  It gets the RIDDEN entity (these are opposites, ppl...)
            }

            //External data alterations
            entity.addTrackingPlayer(player);
            player.addEntity(entity);

            //Fire forge event
            ForgeEventFactory.onStartEntityTracking(entity, player);
        }
    }

    public void updatePlayerEntities(List<EntityPlayer> players)
    {
        for (EntityPlayer player : players)
        {
            updatePlayerEntity((EntityPlayerMP) player);
        }
    }

    private Packet<?> createSpawnPacket()
    {
        if (entity.isDead) LOGGER.warn("Fetching addPacket for removed entity");

        Packet pkt = FMLNetworkHandler.getEntitySpawningPacket(entity);
        if (pkt != null) return pkt;


        if (isPlayer) return new SPacketSpawnPlayer(player);
        if (isLivingBase && entity instanceof IAnimals)
        {
            lastEncodedHeadMotion = MathHelper.floor(entity.getRotationYawHead() * 256 / 360);
            return new SPacketSpawnMob(livingBase);
        }
        if (entity instanceof EntityArmorStand) return new SPacketSpawnObject(entity, 78);
        if (entity instanceof EntityPainting) return new SPacketSpawnPainting((EntityPainting) entity);
        if (entity instanceof EntityItem) return new SPacketSpawnObject(entity, 2, 1);
        if (entity instanceof EntityMinecart) return new SPacketSpawnObject(entity, 10, ((EntityMinecart) entity).getType().getId());
        if (entity instanceof EntityBoat) return new SPacketSpawnObject(entity, 1);
        if (entity instanceof EntityXPOrb) return new SPacketSpawnExperienceOrb((EntityXPOrb) entity);
        if (entity instanceof EntityFishHook)
        {
            Entity angler = ((EntityFishHook) entity).getAngler();
            return new SPacketSpawnObject(entity, 90, angler == null ? entity.getEntityId() : angler.getEntityId());
        }
        if (entity instanceof EntitySpectralArrow)
        {
            Entity archer = ((EntitySpectralArrow) entity).shootingEntity;
            return new SPacketSpawnObject(entity, 91, 1 + (archer == null ? entity.getEntityId() : archer.getEntityId()));
        }
        if (entity instanceof EntityTippedArrow)
        {
            Entity archer = ((EntityArrow) entity).shootingEntity;
            return new SPacketSpawnObject(entity, 60, 1 + (archer == null ? entity.getEntityId() : archer.getEntityId()));
        }
        if (entity instanceof EntitySnowball) return new SPacketSpawnObject(entity, 61);
        if (entity instanceof EntityLlamaSpit) return new SPacketSpawnObject(entity, 68);
        if (entity instanceof EntityPotion) return new SPacketSpawnObject(entity, 73);
        if (entity instanceof EntityExpBottle) return new SPacketSpawnObject(entity, 75);
        if (entity instanceof EntityEnderPearl) return new SPacketSpawnObject(entity, 65);
        if (entity instanceof EntityEnderEye) return new SPacketSpawnObject(entity, 72);
        if (entity instanceof EntityFireworkRocket) return new SPacketSpawnObject(entity, 76);
        if (entity instanceof EntityFireball)
        {
            EntityFireball entityfireball = (EntityFireball) entity;
            int i = 63;

            if (entity instanceof EntitySmallFireball)
            {
                i = 64;
            }
            else if (entity instanceof EntityDragonFireball)
            {
                i = 93;
            }
            else if (entity instanceof EntityWitherSkull)
            {
                i = 66;
            }

            SPacketSpawnObject spacketspawnobject;
            if (entityfireball.shootingEntity != null)
            {
                spacketspawnobject = new SPacketSpawnObject(entity, i, ((EntityFireball) entity).shootingEntity.getEntityId());
            }
            else
            {
                spacketspawnobject = new SPacketSpawnObject(entity, i, 0);
            }

            spacketspawnobject.setSpeedX((int) (entityfireball.accelerationX * 8000.0D));
            spacketspawnobject.setSpeedY((int) (entityfireball.accelerationY * 8000.0D));
            spacketspawnobject.setSpeedZ((int) (entityfireball.accelerationZ * 8000.0D));
            return spacketspawnobject;
        }
        if (entity instanceof EntityShulkerBullet)
        {
            SPacketSpawnObject spacketspawnobject1 = new SPacketSpawnObject(entity, 67, 0);
            spacketspawnobject1.setSpeedX((int) (entity.motionX * 8000.0D));
            spacketspawnobject1.setSpeedY((int) (entity.motionY * 8000.0D));
            spacketspawnobject1.setSpeedZ((int) (entity.motionZ * 8000.0D));
            return spacketspawnobject1;
        }
        if (entity instanceof EntityEgg) return new SPacketSpawnObject(entity, 62);
        if (entity instanceof EntityEvokerFangs) return new SPacketSpawnObject(entity, 79);
        if (entity instanceof EntityTNTPrimed) return new SPacketSpawnObject(entity, 50);
        if (entity instanceof EntityEnderCrystal) return new SPacketSpawnObject(entity, 51);
        if (entity instanceof EntityFallingBlock) return new SPacketSpawnObject(entity, 70, Block.getStateId(((EntityFallingBlock) entity).getBlock()));
        if (entity instanceof EntityItemFrame)
        {
            EntityItemFrame entityitemframe = (EntityItemFrame) entity;
            return new SPacketSpawnObject(entity, 71, entityitemframe.facingDirection.getHorizontalIndex(), entityitemframe.getHangingPosition());
        }
        if (entity instanceof EntityLeashKnot) return new SPacketSpawnObject(entity, 77, 0, ((EntityLeashKnot) entity).getHangingPosition());
        if (entity instanceof EntityAreaEffectCloud) return new SPacketSpawnObject(entity, 3);


        throw new IllegalArgumentException("Don't know how to send packet for " + entity.getClass() + "!");
    }

    public void removeTrackedPlayerSymmetric(EntityPlayerMP playerMP)
    {
        if (trackingPlayers.contains(playerMP))
        {
            trackingPlayers.remove(playerMP);
            entity.removeTrackingPlayer(playerMP);
            playerMP.removeEntity(entity);
        }
    }

    public Entity getTrackedEntity()
    {
        return entity;
    }

    public void setMaxRange(int maxRangeIn)
    {
    }
}