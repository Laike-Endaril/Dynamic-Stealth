package com.fantasticsource.dynamicstealth.server.entitytracker;

import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

public class LivingBaseEntityTrackerEntry extends EntityTrackerEntry
{
    private static final Logger LOGGER = LogManager.getLogger();

    public final Set<EntityPlayerMP> trackingPlayers = Sets.newHashSet();

    private final EntityLivingBase livingBase;
    private final boolean isPlayer;
    private final EntityPlayerMP player;
    private final int updateFrequency;
    private final boolean sendVelocityUpdates;
    public int updateCounter;
    private int ticksSinceLastForcedTeleport;
    private boolean onGround;
    private boolean ridingEntity;
    private boolean updatedPlayerVisibility;

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


    public LivingBaseEntityTrackerEntry(Entity entity, int maxRange, int currentRange, int updateFrequency, boolean sendVelocityUpdates)
    {
        super(entity, maxRange, currentRange, updateFrequency, sendVelocityUpdates);

        livingBase = (EntityLivingBase) entity;
        isPlayer = livingBase instanceof EntityPlayerMP;
        player = isPlayer ? (EntityPlayerMP) livingBase : null;

        this.updateFrequency = updateFrequency;
        this.sendVelocityUpdates = sendVelocityUpdates;
        onGround = entity.onGround;

        encodedPosX = EntityTracker.getPositionLong(entity.posX);
        encodedPosY = EntityTracker.getPositionLong(entity.posY);
        encodedPosZ = EntityTracker.getPositionLong(entity.posZ);
        encodedRotationYaw = MathHelper.floor(entity.rotationYaw * 256 / 360);
        encodedRotationPitch = MathHelper.floor(entity.rotationPitch * 256 / 360);

        lastHeadMotion = MathHelper.floor(entity.getRotationYawHead() * 256 / 360);
    }

    public boolean equals(Object entityTrackerEntry)
    {
        return entityTrackerEntry instanceof LivingBaseEntityTrackerEntry && ((LivingBaseEntityTrackerEntry) entityTrackerEntry).livingBase.getEntityId() == livingBase.getEntityId();
    }

    public int hashCode()
    {
        return livingBase.getEntityId();
    }

    public void updatePlayerList(List<EntityPlayer> players)
    {
        if (!updatedPlayerVisibility || livingBase.getDistanceSq(lastX, lastY, lastZ) > 16)
        {
            lastX = livingBase.posX;
            lastY = livingBase.posY;
            lastZ = livingBase.posZ;

            updatedPlayerVisibility = true;
        }

        List<Entity> list = livingBase.getPassengers();

        if (!list.equals(passengers))
        {
            passengers = list;
            sendPacketToTrackedPlayers(new SPacketSetPassengers(livingBase));
        }

        if (updateCounter % updateFrequency == 0 || livingBase.isAirBorne || livingBase.getDataManager().isDirty())
        {
            if (livingBase.isRiding())
            {
                int j1 = MathHelper.floor(livingBase.rotationYaw * 256 / 360);
                int l1 = MathHelper.floor(livingBase.rotationPitch * 256 / 360);
                boolean flag3 = Math.abs(j1 - encodedRotationYaw) >= 1 || Math.abs(l1 - encodedRotationPitch) >= 1;

                if (flag3)
                {
                    sendPacketToTrackedPlayers(new SPacketEntity.S16PacketEntityLook(livingBase.getEntityId(), (byte) j1, (byte) l1, livingBase.onGround));
                    encodedRotationYaw = j1;
                    encodedRotationPitch = l1;
                }

                encodedPosX = EntityTracker.getPositionLong(livingBase.posX);
                encodedPosY = EntityTracker.getPositionLong(livingBase.posY);
                encodedPosZ = EntityTracker.getPositionLong(livingBase.posZ);
                sendMetadata();
                ridingEntity = true;
            }
            else
            {
                ++this.ticksSinceLastForcedTeleport;
                long i1 = EntityTracker.getPositionLong(livingBase.posX);
                long i2 = EntityTracker.getPositionLong(livingBase.posY);
                long j2 = EntityTracker.getPositionLong(livingBase.posZ);
                int k2 = MathHelper.floor(livingBase.rotationYaw * 256 / 360);
                int i = MathHelper.floor(livingBase.rotationPitch * 256 / 360);
                long j = i1 - encodedPosX;
                long k = i2 - encodedPosY;
                long l = j2 - encodedPosZ;
                Packet<?> packet1 = null;
                boolean flag = j * j + k * k + l * l >= 128 || updateCounter % 60 == 0;
                boolean flag1 = Math.abs(k2 - encodedRotationYaw) >= 1 || Math.abs(i - encodedRotationPitch) >= 1;

                if (updateCounter > 0)
                {
                    if (j >= -32768L && j < 32768L && k >= -32768L && k < 32768L && l >= -32768L && l < 32768L && ticksSinceLastForcedTeleport <= 400 && !ridingEntity && onGround == livingBase.onGround)
                    {
                        if ((!flag || !flag1))
                        {
                            if (flag)
                            {
                                packet1 = new SPacketEntity.S15PacketEntityRelMove(livingBase.getEntityId(), j, k, l, livingBase.onGround);
                            }
                            else if (flag1)
                            {
                                packet1 = new SPacketEntity.S16PacketEntityLook(livingBase.getEntityId(), (byte) k2, (byte) i, livingBase.onGround);
                            }
                        }
                        else
                        {
                            packet1 = new SPacketEntity.S17PacketEntityLookMove(livingBase.getEntityId(), j, k, l, (byte) k2, (byte) i, livingBase.onGround);
                        }
                    }
                    else
                    {
                        onGround = livingBase.onGround;
                        ticksSinceLastForcedTeleport = 0;
                        resetPlayerVisibility();
                        packet1 = new SPacketEntityTeleport(livingBase);
                    }
                }

                if (updateCounter > 0 && (sendVelocityUpdates || livingBase.isElytraFlying()))
                {
                    if (lastMotionX != livingBase.motionX || lastMotionY != livingBase.motionY || lastMotionZ != livingBase.motionZ)
                    {
                        lastMotionX = livingBase.motionX;
                        lastMotionY = livingBase.motionY;
                        lastMotionZ = livingBase.motionZ;
                        sendPacketToTrackedPlayers(new SPacketEntityVelocity(livingBase.getEntityId(), livingBase.motionX, livingBase.motionY, livingBase.motionZ));
                    }
                }

                if (packet1 != null)
                {
                    this.sendPacketToTrackedPlayers(packet1);
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

            int k1 = MathHelper.floor(livingBase.getRotationYawHead() * 256 / 360);

            if (Math.abs(k1 - lastHeadMotion) >= 1)
            {
                sendPacketToTrackedPlayers(new SPacketEntityHeadLook(livingBase, (byte) k1));
                lastHeadMotion = k1;
            }

            livingBase.isAirBorne = false;
        }

        ++updateCounter;

        if (livingBase.velocityChanged)
        {
            sendToTrackingAndSelf(new SPacketEntityVelocity(livingBase));
            livingBase.velocityChanged = false;
        }
    }

    private void sendMetadata()
    {
        EntityDataManager entitydatamanager = livingBase.getDataManager();

        if (entitydatamanager.isDirty())
        {
            sendToTrackingAndSelf(new SPacketEntityMetadata(livingBase.getEntityId(), entitydatamanager, false));
        }

        AttributeMap attributemap = (AttributeMap) livingBase.getAttributeMap();
        Set<IAttributeInstance> set = attributemap.getDirtyInstances();

        if (!set.isEmpty())
        {
            sendToTrackingAndSelf(new SPacketEntityProperties(livingBase.getEntityId(), set));
        }

        set.clear();
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
            livingBase.removeTrackingPlayer(entityplayermp);
            entityplayermp.removeEntity(livingBase);
        }
    }

    public void removeFromTrackedPlayers(EntityPlayerMP playerMP)
    {
        if (trackingPlayers.contains(playerMP))
        {
            livingBase.removeTrackingPlayer(playerMP);
            playerMP.removeEntity(livingBase);
            trackingPlayers.remove(playerMP);
        }
    }

    public void updatePlayerEntity(EntityPlayerMP player)
    {
        if (player != livingBase)
        {
            if (isVisibleTo(player))
            {
                if (!trackingPlayers.contains(player))
                {
                    trackingPlayers.add(player);
                    Packet<?> packet = createSpawnPacket();
                    player.connection.sendPacket(packet);

                    if (!livingBase.getDataManager().isEmpty())
                    {
                        player.connection.sendPacket(new SPacketEntityMetadata(livingBase.getEntityId(), livingBase.getDataManager(), true));
                    }

                    AttributeMap attributemap = (AttributeMap) livingBase.getAttributeMap();
                    Collection<IAttributeInstance> collection = attributemap.getWatchedAttributes();

                    if (!collection.isEmpty())
                    {
                        player.connection.sendPacket(new SPacketEntityProperties(livingBase.getEntityId(), collection));
                    }

                    lastMotionX = livingBase.motionX;
                    lastMotionY = livingBase.motionY;
                    lastMotionZ = livingBase.motionZ;

                    //Send velocity; SPacketSpawnMob already contains velocities
                    if (!(packet instanceof SPacketSpawnMob) && (sendVelocityUpdates || livingBase.isElytraFlying()))
                    {
                        player.connection.sendPacket(new SPacketEntityVelocity(livingBase.getEntityId(), livingBase.motionX, livingBase.motionY, livingBase.motionZ));
                    }

                    //Send equipment
                    for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values())
                    {
                        ItemStack itemstack = livingBase.getItemStackFromSlot(entityequipmentslot);

                        if (!itemstack.isEmpty())
                        {
                            player.connection.sendPacket(new SPacketEntityEquipment(livingBase.getEntityId(), entityequipmentslot, itemstack));
                        }
                    }

                    //Send packet to set entity to display as sleeping in a bed
                    if (isPlayer && this.player.isPlayerSleeping())
                    {
                        player.connection.sendPacket(new SPacketUseBed(this.player, new BlockPos(livingBase)));
                    }

                    //Send visual potion effects
                    for (PotionEffect potioneffect : livingBase.getActivePotionEffects())
                    {
                        player.connection.sendPacket(new SPacketEntityEffect(livingBase.getEntityId(), potioneffect));
                    }

                    //Send riding entities
                    if (!livingBase.getPassengers().isEmpty())
                    {
                        player.connection.sendPacket(new SPacketSetPassengers(livingBase));
                    }

                    //Send ridden entity
                    if (livingBase.isRiding())
                    {
                        player.connection.sendPacket(new SPacketSetPassengers(livingBase.getRidingEntity())); //getRidingEntity DOES NOT GET THE RIDING ENTITY!  It gets the RIDDEN entity (these are opposites, ppl...)
                    }

                    //External data alterations
                    livingBase.addTrackingPlayer(player);
                    player.addEntity(livingBase);

                    //Fire forge event
                    ForgeEventFactory.onStartEntityTracking(livingBase, player);
                }
            }
            else if (trackingPlayers.contains(player))
            {
                //Internal data alterations
                trackingPlayers.remove(player);

                //External data alterations
                livingBase.removeTrackingPlayer(player);
                player.removeEntity(livingBase);

                //Fire forge event
                ForgeEventFactory.onStopEntityTracking(livingBase, player);
            }
        }
    }

    public boolean isVisibleTo(EntityPlayerMP playerMP)
    {
        return Sight.canSee(playerMP, livingBase, true);
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
        if (livingBase.isDead) LOGGER.warn("Fetching addPacket for removed entity");

        Packet pkt = FMLNetworkHandler.getEntitySpawningPacket(livingBase);
        if (pkt != null) return pkt;


        if (isPlayer) return new SPacketSpawnPlayer(player);
        if (livingBase instanceof EntityArmorStand) return new SPacketSpawnObject(livingBase, 78);

        if (livingBase instanceof IAnimals)
        {
            lastHeadMotion = MathHelper.floor(livingBase.getRotationYawHead() * 256 / 360);
            return new SPacketSpawnMob(livingBase);
        }

        throw new IllegalArgumentException("Don't know how to send packet for " + livingBase.getClass() + "!");
    }

    public void removeTrackedPlayerSymmetric(EntityPlayerMP playerMP)
    {
        if (trackingPlayers.contains(playerMP))
        {
            trackingPlayers.remove(playerMP);
            livingBase.removeTrackingPlayer(playerMP);
            playerMP.removeEntity(livingBase);
        }
    }

    public Entity getTrackedEntity()
    {
        return livingBase;
    }

    public void setMaxRange(int maxRangeIn)
    {
    }

    public void resetPlayerVisibility()
    {
        updatedPlayerVisibility = false;
    }
}