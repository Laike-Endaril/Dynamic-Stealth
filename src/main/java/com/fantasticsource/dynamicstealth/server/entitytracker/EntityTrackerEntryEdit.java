package com.fantasticsource.dynamicstealth.server.entitytracker;

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

public class EntityTrackerEntryEdit extends EntityTrackerEntry
{
    private static final Logger LOGGER = LogManager.getLogger();
    public final Set<EntityPlayerMP> trackingPlayers = Sets.newHashSet();

    private final Entity trackedEntity;
    private final boolean isLivingBase, isPlayer;
    private final EntityLivingBase livingBase;
    private final EntityPlayerMP player;

    private final int range;
    private final int updateFrequency;
    private final boolean sendVelocityUpdates;
    public int updateCounter;
    public boolean playerEntitiesUpdated;
    private int maxRange;
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
    private boolean updatedPlayerVisibility;
    private int ticksSinceLastForcedTeleport;
    private List<Entity> passengers = Collections.emptyList();
    private boolean ridingEntity;
    private boolean onGround;

    public EntityTrackerEntryEdit(Entity entityIn, int rangeIn, int maxRangeIn, int updateFrequencyIn, boolean sendVelocityUpdatesIn)
    {
        super(entityIn, rangeIn, maxRangeIn, updateFrequencyIn, sendVelocityUpdatesIn);

        trackedEntity = entityIn;
        isLivingBase = trackedEntity instanceof EntityLivingBase;
        isPlayer = trackedEntity instanceof EntityPlayerMP;
        livingBase = isLivingBase ? (EntityLivingBase) trackedEntity : null;
        player = isPlayer ? (EntityPlayerMP) trackedEntity : null;

        range = rangeIn;
        maxRange = maxRangeIn;
        updateFrequency = updateFrequencyIn;
        sendVelocityUpdates = sendVelocityUpdatesIn;

        encodedPosX = EntityTracker.getPositionLong(entityIn.posX);
        encodedPosY = EntityTracker.getPositionLong(entityIn.posY);
        encodedPosZ = EntityTracker.getPositionLong(entityIn.posZ);
        encodedRotationYaw = MathHelper.floor(entityIn.rotationYaw * 256 / 360);
        encodedRotationPitch = MathHelper.floor(entityIn.rotationPitch * 256 / 360);

        lastHeadMotion = MathHelper.floor(entityIn.getRotationYawHead() * 256 / 360);
        onGround = entityIn.onGround;
    }

    public boolean equals(Object entityTrackerEntry)
    {
        return entityTrackerEntry instanceof EntityTrackerEntryEdit && ((EntityTrackerEntryEdit) entityTrackerEntry).trackedEntity.getEntityId() == trackedEntity.getEntityId();
    }

    public int hashCode()
    {
        return trackedEntity.getEntityId();
    }

    public void updatePlayerList(List<EntityPlayer> players)
    {
        playerEntitiesUpdated = false;

        if (!updatedPlayerVisibility || trackedEntity.getDistanceSq(lastX, lastY, lastZ) > 16)
        {
            lastX = trackedEntity.posX;
            lastY = trackedEntity.posY;
            lastZ = trackedEntity.posZ;

            updatedPlayerVisibility = true;
            playerEntitiesUpdated = true;

            updatePlayerEntities(players);
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

    public void updatePlayerEntity(EntityPlayerMP playerMP)
    {
        if (playerMP != trackedEntity)
        {
            if (isVisibleTo(playerMP))
            {
                if (!trackingPlayers.contains(playerMP) && (isPlayerWatchingThisChunk(playerMP) || trackedEntity.forceSpawn))
                {
                    trackingPlayers.add(playerMP);
                    Packet<?> packet = createSpawnPacket();
                    playerMP.connection.sendPacket(packet);

                    if (!trackedEntity.getDataManager().isEmpty())
                    {
                        playerMP.connection.sendPacket(new SPacketEntityMetadata(trackedEntity.getEntityId(), trackedEntity.getDataManager(), true));
                    }

                    boolean flag = sendVelocityUpdates;

                    if (isLivingBase)
                    {
                        AttributeMap attributemap = (AttributeMap) livingBase.getAttributeMap();
                        Collection<IAttributeInstance> collection = attributemap.getWatchedAttributes();

                        if (!collection.isEmpty())
                        {
                            playerMP.connection.sendPacket(new SPacketEntityProperties(trackedEntity.getEntityId(), collection));
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
                        playerMP.connection.sendPacket(new SPacketEntityVelocity(trackedEntity.getEntityId(), trackedEntity.motionX, trackedEntity.motionY, trackedEntity.motionZ));
                    }

                    if (isLivingBase)
                    {
                        for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values())
                        {
                            ItemStack itemstack = livingBase.getItemStackFromSlot(entityequipmentslot);

                            if (!itemstack.isEmpty())
                            {
                                playerMP.connection.sendPacket(new SPacketEntityEquipment(trackedEntity.getEntityId(), entityequipmentslot, itemstack));
                            }
                        }
                    }

                    if (isPlayer && player.isPlayerSleeping())
                    {
                        playerMP.connection.sendPacket(new SPacketUseBed(player, new BlockPos(trackedEntity)));
                    }

                    if (isLivingBase)
                    {
                        for (PotionEffect potioneffect : livingBase.getActivePotionEffects())
                        {
                            playerMP.connection.sendPacket(new SPacketEntityEffect(trackedEntity.getEntityId(), potioneffect));
                        }
                    }

                    if (!trackedEntity.getPassengers().isEmpty())
                    {
                        playerMP.connection.sendPacket(new SPacketSetPassengers(trackedEntity));
                    }

                    if (trackedEntity.isRiding())
                    {
                        playerMP.connection.sendPacket(new SPacketSetPassengers(trackedEntity.getRidingEntity()));
                    }

                    trackedEntity.addTrackingPlayer(playerMP);
                    playerMP.addEntity(trackedEntity);
                    ForgeEventFactory.onStartEntityTracking(trackedEntity, playerMP);
                }
            }
            else if (trackingPlayers.contains(playerMP))
            {
                trackingPlayers.remove(playerMP);
                trackedEntity.removeTrackingPlayer(playerMP);
                playerMP.removeEntity(trackedEntity);
                ForgeEventFactory.onStopEntityTracking(trackedEntity, playerMP);
            }
        }
    }

    public boolean isVisibleTo(EntityPlayerMP playerMP)
    {
        double d0 = playerMP.posX - (double) encodedPosX / 4096;
        double d1 = playerMP.posZ - (double) encodedPosZ / 4096;
        int i = Math.min(range, maxRange);
        return d0 >= (double) (-i) && d0 <= (double) i && d1 >= (double) (-i) && d1 <= (double) i && trackedEntity.isSpectatedByPlayer(playerMP);
    }

    private boolean isPlayerWatchingThisChunk(EntityPlayerMP playerMP)
    {
        return playerMP.getServerWorld().getPlayerChunkMap().isPlayerWatchingChunk(playerMP, trackedEntity.chunkCoordX, trackedEntity.chunkCoordZ);
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
        if (trackedEntity.isDead)
        {
            LOGGER.warn("Fetching addPacket for removed entity");
        }

        Packet pkt = FMLNetworkHandler.getEntitySpawningPacket(trackedEntity);
        if (pkt != null) return pkt;

        if (isPlayer)
        {
            return new SPacketSpawnPlayer(player);
        }
        else if (trackedEntity instanceof IAnimals)
        {
            lastHeadMotion = MathHelper.floor(trackedEntity.getRotationYawHead() * 256 / 360);
            return new SPacketSpawnMob(livingBase);
        }
        else if (trackedEntity instanceof EntityPainting)
        {
            return new SPacketSpawnPainting((EntityPainting) trackedEntity);
        }
        else if (trackedEntity instanceof EntityItem)
        {
            return new SPacketSpawnObject(trackedEntity, 2, 1);
        }
        else if (trackedEntity instanceof EntityMinecart)
        {
            EntityMinecart entityminecart = (EntityMinecart) trackedEntity;
            return new SPacketSpawnObject(trackedEntity, 10, entityminecart.getType().getId());
        }
        else if (trackedEntity instanceof EntityBoat)
        {
            return new SPacketSpawnObject(trackedEntity, 1);
        }
        else if (trackedEntity instanceof EntityXPOrb)
        {
            return new SPacketSpawnExperienceOrb((EntityXPOrb) trackedEntity);
        }
        else if (trackedEntity instanceof EntityFishHook)
        {
            Entity entity2 = ((EntityFishHook) trackedEntity).getAngler();
            return new SPacketSpawnObject(trackedEntity, 90, entity2.getEntityId());
        }
        else if (trackedEntity instanceof EntitySpectralArrow)
        {
            Entity entity1 = ((EntitySpectralArrow) trackedEntity).shootingEntity;
            return new SPacketSpawnObject(trackedEntity, 91, 1 + (entity1 == null ? trackedEntity.getEntityId() : entity1.getEntityId()));
        }
        else if (trackedEntity instanceof EntityTippedArrow)
        {
            Entity entity = ((EntityArrow) trackedEntity).shootingEntity;
            return new SPacketSpawnObject(trackedEntity, 60, 1 + (entity == null ? trackedEntity.getEntityId() : entity.getEntityId()));
        }
        else if (trackedEntity instanceof EntitySnowball)
        {
            return new SPacketSpawnObject(trackedEntity, 61);
        }
        else if (trackedEntity instanceof EntityLlamaSpit)
        {
            return new SPacketSpawnObject(trackedEntity, 68);
        }
        else if (trackedEntity instanceof EntityPotion)
        {
            return new SPacketSpawnObject(trackedEntity, 73);
        }
        else if (trackedEntity instanceof EntityExpBottle)
        {
            return new SPacketSpawnObject(trackedEntity, 75);
        }
        else if (trackedEntity instanceof EntityEnderPearl)
        {
            return new SPacketSpawnObject(trackedEntity, 65);
        }
        else if (trackedEntity instanceof EntityEnderEye)
        {
            return new SPacketSpawnObject(trackedEntity, 72);
        }
        else if (trackedEntity instanceof EntityFireworkRocket)
        {
            return new SPacketSpawnObject(trackedEntity, 76);
        }
        else if (trackedEntity instanceof EntityFireball)
        {
            EntityFireball entityfireball = (EntityFireball) trackedEntity;
            SPacketSpawnObject spacketspawnobject;
            int i = 63;

            if (trackedEntity instanceof EntitySmallFireball)
            {
                i = 64;
            }
            else if (trackedEntity instanceof EntityDragonFireball)
            {
                i = 93;
            }
            else if (trackedEntity instanceof EntityWitherSkull)
            {
                i = 66;
            }

            if (entityfireball.shootingEntity != null)
            {
                spacketspawnobject = new SPacketSpawnObject(trackedEntity, i, ((EntityFireball) trackedEntity).shootingEntity.getEntityId());
            }
            else
            {
                spacketspawnobject = new SPacketSpawnObject(trackedEntity, i, 0);
            }

            spacketspawnobject.setSpeedX((int) (entityfireball.accelerationX * 8000));
            spacketspawnobject.setSpeedY((int) (entityfireball.accelerationY * 8000));
            spacketspawnobject.setSpeedZ((int) (entityfireball.accelerationZ * 8000));
            return spacketspawnobject;
        }
        else if (trackedEntity instanceof EntityShulkerBullet)
        {
            SPacketSpawnObject spacketspawnobject1 = new SPacketSpawnObject(trackedEntity, 67, 0);
            spacketspawnobject1.setSpeedX((int) (trackedEntity.motionX * 8000));
            spacketspawnobject1.setSpeedY((int) (trackedEntity.motionY * 8000));
            spacketspawnobject1.setSpeedZ((int) (trackedEntity.motionZ * 8000));
            return spacketspawnobject1;
        }
        else if (trackedEntity instanceof EntityEgg)
        {
            return new SPacketSpawnObject(trackedEntity, 62);
        }
        else if (trackedEntity instanceof EntityEvokerFangs)
        {
            return new SPacketSpawnObject(trackedEntity, 79);
        }
        else if (trackedEntity instanceof EntityTNTPrimed)
        {
            return new SPacketSpawnObject(trackedEntity, 50);
        }
        else if (trackedEntity instanceof EntityEnderCrystal)
        {
            return new SPacketSpawnObject(trackedEntity, 51);
        }
        else if (trackedEntity instanceof EntityFallingBlock)
        {
            EntityFallingBlock entityfallingblock = (EntityFallingBlock) trackedEntity;
            return new SPacketSpawnObject(trackedEntity, 70, Block.getStateId(entityfallingblock.getBlock()));
        }
        else if (trackedEntity instanceof EntityArmorStand)
        {
            return new SPacketSpawnObject(trackedEntity, 78);
        }
        else if (trackedEntity instanceof EntityItemFrame)
        {
            EntityItemFrame entityitemframe = (EntityItemFrame) trackedEntity;
            return new SPacketSpawnObject(trackedEntity, 71, entityitemframe.facingDirection.getHorizontalIndex(), entityitemframe.getHangingPosition());
        }
        else if (trackedEntity instanceof EntityLeashKnot)
        {
            EntityLeashKnot entityleashknot = (EntityLeashKnot) trackedEntity;
            return new SPacketSpawnObject(trackedEntity, 77, 0, entityleashknot.getHangingPosition());
        }
        else if (trackedEntity instanceof EntityAreaEffectCloud)
        {
            return new SPacketSpawnObject(trackedEntity, 3);
        }
        else
        {
            throw new IllegalArgumentException("Don't know how to add " + trackedEntity.getClass() + "!");
        }
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
        maxRange = maxRangeIn;
    }

    public void resetPlayerVisibility()
    {
        updatedPlayerVisibility = false;
    }
}