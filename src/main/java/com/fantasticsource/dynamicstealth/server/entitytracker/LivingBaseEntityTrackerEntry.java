package com.fantasticsource.dynamicstealth.server.entitytracker;

import com.fantasticsource.dynamicstealth.server.Senses.Sight;
import com.google.common.collect.Sets;
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
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LivingBaseEntityTrackerEntry extends EntityTrackerEntry
{
    private static final Logger LOGGER = LogManager.getLogger();

    public final Set<EntityPlayerMP> trackingPlayers = Sets.newHashSet();

    private final Entity trackedEntity;
    private final boolean isLivingBase, isPlayer;
    private final EntityLivingBase livingBase;
    private final EntityPlayerMP player;

    private final int updateFrequency;
    private final boolean sendVelocityUpdates;
    public int updateCounter;
    private boolean updatedPlayerVisibility;
    private int ticksSinceLastForcedTeleport;

    private long encodedPosX;
    private long encodedPosY;
    private long encodedPosZ;

    private int encodedRotationYaw;
    private int encodedRotationPitch;

    private int lastHeadMotion;

    private double lastMotionX;
    private double lastMotionY;
    private double lastMotionZ;

    private double lastX;
    private double lastY;
    private double lastZ;

    private List<Entity> passengers = Collections.emptyList();
    private boolean ridingEntity;
    private boolean onGround;


    public LivingBaseEntityTrackerEntry(Entity entity, int maxRange, int currentRange, int updateFrequency, boolean sendVelocityUpdates)
    {
        super(entity, maxRange, currentRange, updateFrequency, sendVelocityUpdates);

        trackedEntity = entity;
        isLivingBase = trackedEntity instanceof EntityLivingBase;
        isPlayer = trackedEntity instanceof EntityPlayerMP;
        livingBase = isLivingBase ? (EntityLivingBase) trackedEntity : null;
        player = isPlayer ? (EntityPlayerMP) trackedEntity : null;

        this.updateFrequency = updateFrequency;
        this.sendVelocityUpdates = sendVelocityUpdates;

        encodedPosX = EntityTracker.getPositionLong(entity.posX);
        encodedPosY = EntityTracker.getPositionLong(entity.posY);
        encodedPosZ = EntityTracker.getPositionLong(entity.posZ);
        encodedRotationYaw = MathHelper.floor(entity.rotationYaw * 256 / 360);
        encodedRotationPitch = MathHelper.floor(entity.rotationPitch * 256 / 360);

        lastHeadMotion = MathHelper.floor(entity.getRotationYawHead() * 256 / 360);
        onGround = entity.onGround;
    }

    public boolean equals(Object entityTrackerEntry)
    {
        return entityTrackerEntry instanceof LivingBaseEntityTrackerEntry && ((LivingBaseEntityTrackerEntry) entityTrackerEntry).trackedEntity.getEntityId() == trackedEntity.getEntityId();
    }

    public int hashCode()
    {
        return trackedEntity.getEntityId();
    }

    public void updatePlayerList(List<EntityPlayer> players)
    {
        if (!updatedPlayerVisibility || trackedEntity.getDistanceSq(lastX, lastY, lastZ) > 16)
        {
            lastX = trackedEntity.posX;
            lastY = trackedEntity.posY;
            lastZ = trackedEntity.posZ;

            updatedPlayerVisibility = true;
        }

        List<Entity> list = trackedEntity.getPassengers();

        if (!list.equals(passengers))
        {
            passengers = list;
            sendPacketToTrackedPlayers(new SPacketSetPassengers(trackedEntity));
        }

        if (trackedEntity instanceof EntityItemFrame && updateCounter % 10 == 0)
        {
            EntityItemFrame entityitemframe = (EntityItemFrame) trackedEntity;
            ItemStack itemstack = entityitemframe.getDisplayedItem();

            if (itemstack.getItem() instanceof ItemMap)
            {
                MapData mapdata = ((ItemMap) itemstack.getItem()).getMapData(itemstack, trackedEntity.world);

                for (EntityPlayer entityplayer : players)
                {
                    EntityPlayerMP entityplayermp = (EntityPlayerMP) entityplayer;
                    mapdata.updateVisiblePlayers(entityplayermp, itemstack);
                    Packet<?> packet = ((ItemMap) itemstack.getItem()).createMapDataPacket(itemstack, trackedEntity.world, entityplayermp);

                    if (packet != null)
                    {
                        entityplayermp.connection.sendPacket(packet);
                    }
                }
            }

            sendMetadata();
        }

        if (updateCounter % updateFrequency == 0 || trackedEntity.isAirBorne || trackedEntity.getDataManager().isDirty())
        {
            if (trackedEntity.isRiding())
            {
                int j1 = MathHelper.floor(trackedEntity.rotationYaw * 256 / 360);
                int l1 = MathHelper.floor(trackedEntity.rotationPitch * 256 / 360);
                boolean flag3 = Math.abs(j1 - encodedRotationYaw) >= 1 || Math.abs(l1 - encodedRotationPitch) >= 1;

                if (flag3)
                {
                    sendPacketToTrackedPlayers(new SPacketEntity.S16PacketEntityLook(trackedEntity.getEntityId(), (byte) j1, (byte) l1, trackedEntity.onGround));
                    encodedRotationYaw = j1;
                    encodedRotationPitch = l1;
                }

                encodedPosX = EntityTracker.getPositionLong(trackedEntity.posX);
                encodedPosY = EntityTracker.getPositionLong(trackedEntity.posY);
                encodedPosZ = EntityTracker.getPositionLong(trackedEntity.posZ);
                sendMetadata();
                ridingEntity = true;
            }
            else
            {
                ++ticksSinceLastForcedTeleport;
                long i1 = EntityTracker.getPositionLong(trackedEntity.posX);
                long i2 = EntityTracker.getPositionLong(trackedEntity.posY);
                long j2 = EntityTracker.getPositionLong(trackedEntity.posZ);
                int k2 = MathHelper.floor(trackedEntity.rotationYaw * 256 / 360);
                int i = MathHelper.floor(trackedEntity.rotationPitch * 256 / 360);
                long j = i1 - encodedPosX;
                long k = i2 - encodedPosY;
                long l = j2 - encodedPosZ;
                Packet<?> packet1 = null;
                boolean flag = j * j + k * k + l * l >= 128 || updateCounter % 60 == 0;
                boolean flag1 = Math.abs(k2 - encodedRotationYaw) >= 1 || Math.abs(i - encodedRotationPitch) >= 1;

                if (updateCounter > 0 || trackedEntity instanceof EntityArrow)
                {
                    if (j >= -32768L && j < 32768L && k >= -32768L && k < 32768L && l >= -32768L && l < 32768L && ticksSinceLastForcedTeleport <= 400 && !ridingEntity && onGround == trackedEntity.onGround)
                    {
                        if ((!flag || !flag1) && !(trackedEntity instanceof EntityArrow))
                        {
                            if (flag)
                            {
                                packet1 = new SPacketEntity.S15PacketEntityRelMove(trackedEntity.getEntityId(), j, k, l, trackedEntity.onGround);
                            }
                            else if (flag1)
                            {
                                packet1 = new SPacketEntity.S16PacketEntityLook(trackedEntity.getEntityId(), (byte) k2, (byte) i, trackedEntity.onGround);
                            }
                        }
                        else
                        {
                            packet1 = new SPacketEntity.S17PacketEntityLookMove(trackedEntity.getEntityId(), j, k, l, (byte) k2, (byte) i, trackedEntity.onGround);
                        }
                    }
                    else
                    {
                        onGround = trackedEntity.onGround;
                        ticksSinceLastForcedTeleport = 0;
                        resetPlayerVisibility();
                        packet1 = new SPacketEntityTeleport(trackedEntity);
                    }
                }

                if (updateCounter > 0 && (sendVelocityUpdates || (isLivingBase && livingBase.isElytraFlying())))
                {
                    double distSquared = Math.pow(trackedEntity.motionX - lastMotionX, 2) + Math.pow(trackedEntity.motionY - lastMotionY, 2) + Math.pow(trackedEntity.motionZ - lastMotionZ, 2);

                    if (distSquared > .0004 || (distSquared > 0 && trackedEntity.motionX == 0 && trackedEntity.motionY == 0 && trackedEntity.motionZ == 0))
                    {
                        lastMotionX = trackedEntity.motionX;
                        lastMotionY = trackedEntity.motionY;
                        lastMotionZ = trackedEntity.motionZ;
                        sendPacketToTrackedPlayers(new SPacketEntityVelocity(trackedEntity.getEntityId(), lastMotionX, lastMotionY, lastMotionZ));
                    }
                }

                if (packet1 != null)
                {
                    sendPacketToTrackedPlayers(packet1);
                }

                sendMetadata();

                if (flag)
                {
                    encodedPosX = i1;
                    encodedPosY = i2;
                    encodedPosZ = j2;
                }

                if (flag1)
                {
                    encodedRotationYaw = k2;
                    encodedRotationPitch = i;
                }

                ridingEntity = false;
            }

            int k1 = MathHelper.floor(trackedEntity.getRotationYawHead() * 256 / 360);

            if (Math.abs(k1 - lastHeadMotion) >= 1)
            {
                sendPacketToTrackedPlayers(new SPacketEntityHeadLook(trackedEntity, (byte) k1));
                lastHeadMotion = k1;
            }

            trackedEntity.isAirBorne = false;
        }

        ++updateCounter;

        if (trackedEntity.velocityChanged)
        {
            sendToTrackingAndSelf(new SPacketEntityVelocity(trackedEntity));
            trackedEntity.velocityChanged = false;
        }
    }

    private void sendMetadata()
    {
        EntityDataManager entitydatamanager = trackedEntity.getDataManager();

        if (entitydatamanager.isDirty())
        {
            sendToTrackingAndSelf(new SPacketEntityMetadata(trackedEntity.getEntityId(), entitydatamanager, false));
        }

        if (isLivingBase)
        {
            AttributeMap attributemap = (AttributeMap) livingBase.getAttributeMap();
            Set<IAttributeInstance> set = attributemap.getDirtyInstances();

            if (!set.isEmpty())
            {
                sendToTrackingAndSelf(new SPacketEntityProperties(trackedEntity.getEntityId(), set));
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
            trackedEntity.removeTrackingPlayer(entityplayermp);
            entityplayermp.removeEntity(trackedEntity);
        }
    }

    public void removeFromTrackedPlayers(EntityPlayerMP playerMP)
    {
        if (trackingPlayers.contains(playerMP))
        {
            trackedEntity.removeTrackingPlayer(playerMP);
            playerMP.removeEntity(trackedEntity);
            trackingPlayers.remove(playerMP);
        }
    }

    public void updatePlayerEntity(EntityPlayerMP player)
    {
        //TODO this is not happening often enough for living entities
        //TODO this should happen exactly once every tick for every player
        if (trackedEntity != player)
        {
            if (isVisibleTo(player))
            {
                if (!trackingPlayers.contains(player))
                {
                    trackingPlayers.add(player);
                    Packet<?> packet = createSpawnPacket();
                    player.connection.sendPacket(packet);

                    if (!trackedEntity.getDataManager().isEmpty())
                    {
                        player.connection.sendPacket(new SPacketEntityMetadata(trackedEntity.getEntityId(), trackedEntity.getDataManager(), true));
                    }

                    boolean flag = sendVelocityUpdates;

                    if (isLivingBase)
                    {
                        AttributeMap attributemap = (AttributeMap) livingBase.getAttributeMap();
                        Collection<IAttributeInstance> collection = attributemap.getWatchedAttributes();

                        if (!collection.isEmpty())
                        {
                            player.connection.sendPacket(new SPacketEntityProperties(trackedEntity.getEntityId(), collection));
                        }

                        if (livingBase.isElytraFlying())
                        {
                            flag = true;
                        }
                    }

                    lastMotionX = trackedEntity.motionX;
                    lastMotionY = trackedEntity.motionY;
                    lastMotionZ = trackedEntity.motionZ;

                    if (flag && !(packet instanceof SPacketSpawnMob))
                    {
                        player.connection.sendPacket(new SPacketEntityVelocity(trackedEntity.getEntityId(), trackedEntity.motionX, trackedEntity.motionY, trackedEntity.motionZ));
                    }

                    if (isLivingBase)
                    {
                        for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values())
                        {
                            ItemStack itemstack = livingBase.getItemStackFromSlot(entityequipmentslot);

                            if (!itemstack.isEmpty())
                            {
                                player.connection.sendPacket(new SPacketEntityEquipment(trackedEntity.getEntityId(), entityequipmentslot, itemstack));
                            }
                        }
                    }

                    if (isPlayer && this.player.isPlayerSleeping())
                    {
                        player.connection.sendPacket(new SPacketUseBed(this.player, new BlockPos(trackedEntity)));
                    }

                    if (isLivingBase)
                    {
                        for (PotionEffect potioneffect : livingBase.getActivePotionEffects())
                        {
                            player.connection.sendPacket(new SPacketEntityEffect(trackedEntity.getEntityId(), potioneffect));
                        }
                    }

                    if (!trackedEntity.getPassengers().isEmpty())
                    {
                        player.connection.sendPacket(new SPacketSetPassengers(trackedEntity));
                    }

                    if (trackedEntity.isRiding())
                    {
                        player.connection.sendPacket(new SPacketSetPassengers(trackedEntity.getRidingEntity()));
                    }

                    trackedEntity.addTrackingPlayer(player);
                    player.addEntity(trackedEntity);
                    ForgeEventFactory.onStartEntityTracking(trackedEntity, player);
                }
            }
            else if (trackingPlayers.contains(player))
            {
                trackingPlayers.remove(player);
                trackedEntity.removeTrackingPlayer(player);
                player.removeEntity(trackedEntity);
                ForgeEventFactory.onStopEntityTracking(trackedEntity, player);
            }
        }
    }

    public boolean isVisibleTo(EntityPlayerMP playerMP)
    {
        return Sight.visualStealthLevel(playerMP, trackedEntity, true, true) <= 1;
    }

    public void updatePlayerEntities(List<EntityPlayer> players)
    {
        //Don't need this anymore since it happens every tick
    }

    private Packet<?> createSpawnPacket()
    {
        if (trackedEntity.isDead) LOGGER.warn("Fetching addPacket for removed entity");

        Packet pkt = FMLNetworkHandler.getEntitySpawningPacket(trackedEntity);
        if (pkt != null) return pkt;


        if (isPlayer) return new SPacketSpawnPlayer(player);
        if (trackedEntity instanceof EntityArmorStand) return new SPacketSpawnObject(trackedEntity, 78);

        if (trackedEntity instanceof IAnimals)
        {
            lastHeadMotion = MathHelper.floor(trackedEntity.getRotationYawHead() * 256 / 360);
            return new SPacketSpawnMob(livingBase);
        }

        throw new IllegalArgumentException("Don't know how to send packet for " + trackedEntity.getClass() + "!");
    }

    public void removeTrackedPlayerSymmetric(EntityPlayerMP playerMP)
    {
        if (trackingPlayers.contains(playerMP))
        {
            trackingPlayers.remove(playerMP);
            trackedEntity.removeTrackingPlayer(playerMP);
            playerMP.removeEntity(trackedEntity);
        }
    }

    public Entity getTrackedEntity()
    {
        return trackedEntity;
    }

    public void setMaxRange(int maxRangeIn)
    {
    }

    public void resetPlayerVisibility()
    {
        updatedPlayerVisibility = false;
    }
}