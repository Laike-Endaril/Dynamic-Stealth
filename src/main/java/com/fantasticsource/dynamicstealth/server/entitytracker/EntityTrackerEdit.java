package com.fantasticsource.dynamicstealth.server.entitytracker;

import com.fantasticsource.dynamicstealth.server.GlobalDefaultsAndData;
import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightData;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.*;
import net.minecraft.network.Packet;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.Set;

public class EntityTrackerEdit extends EntityTracker
{
    public EntityTrackerEdit(WorldServer worldIn)
    {
        super(worldIn);
        maxTrackingDistanceThreshold = Tools.min(worldIn.getMinecraftServer().getPlayerList().getEntityViewDistance(), EntitySightData.playerMaxSightDistance);
    }

    public void track(Entity entityIn)
    {
        if (EntityRegistry.instance().tryTrackingEntity(this, entityIn)) return;

        if (entityIn instanceof EntityPlayerMP)
        {
            track(entityIn, 512, 2);
            EntityPlayerMP entityplayermp = (EntityPlayerMP) entityIn;
            for (EntityTrackerEntry trackerEntry : entries.toArray(new EntityTrackerEntry[0])) trackerEntry.updatePlayerEntity(entityplayermp);
        }
        else if (entityIn instanceof EntityFishHook) track(entityIn, 64, 5, true);
        else if (entityIn instanceof EntityArrow) track(entityIn, 64, 20, false);
        else if (entityIn instanceof EntitySmallFireball) track(entityIn, 64, 10, false);
        else if (entityIn instanceof EntityFireball) track(entityIn, 64, 10, true);
        else if (entityIn instanceof EntitySnowball) track(entityIn, 64, 10, true);
        else if (entityIn instanceof EntityLlamaSpit) track(entityIn, 64, 10, false);
        else if (entityIn instanceof EntityEnderPearl) track(entityIn, 64, 10, true);
        else if (entityIn instanceof EntityEnderEye) track(entityIn, 64, 4, true);
        else if (entityIn instanceof EntityEgg) track(entityIn, 64, 10, true);
        else if (entityIn instanceof EntityPotion) track(entityIn, 64, 10, true);
        else if (entityIn instanceof EntityExpBottle) track(entityIn, 64, 10, true);
        else if (entityIn instanceof EntityFireworkRocket) track(entityIn, 64, 10, true);
        else if (entityIn instanceof EntityItem) track(entityIn, 64, 20, true);
        else if (entityIn instanceof EntityMinecart) track(entityIn, 80, 3, true);
        else if (entityIn instanceof EntityBoat) track(entityIn, 80, 3, true);
        else if (entityIn instanceof EntitySquid) track(entityIn, 64, 3, true);
        else if (entityIn instanceof EntityWither) track(entityIn, 80, 3, false);
        else if (entityIn instanceof EntityShulkerBullet) track(entityIn, 80, 3, true);
        else if (entityIn instanceof EntityBat) track(entityIn, 80, 3, false);
        else if (entityIn instanceof EntityDragon) track(entityIn, 160, 3, true);
        else if (entityIn instanceof IAnimals) track(entityIn, 80, 3, true); //This includes most vanilla mobs, hostile or not.  May not want to change ordering as it might include things above it in this list
        else if (entityIn instanceof EntityTNTPrimed) track(entityIn, 160, 10, true);
        else if (entityIn instanceof EntityFallingBlock) track(entityIn, 160, 20, true);
        else if (entityIn instanceof EntityHanging) track(entityIn, 160, Integer.MAX_VALUE, false);
        else if (entityIn instanceof EntityArmorStand) track(entityIn, 160, 3, true);
        else if (entityIn instanceof EntityXPOrb) track(entityIn, 160, 20, true);
        else if (entityIn instanceof EntityAreaEffectCloud) track(entityIn, 160, Integer.MAX_VALUE, true);
        else if (entityIn instanceof EntityEnderCrystal) track(entityIn, 256, Integer.MAX_VALUE, false);
        else if (entityIn instanceof EntityEvokerFangs) track(entityIn, 160, 2, false);
    }

    public void track(Entity entityIn, int trackingRange, int updateFrequency)
    {
        track(entityIn, trackingRange, updateFrequency, false);
    }

    public void track(Entity entityIn, int trackingRange, final int updateFrequency, boolean sendVelocityUpdates)
    {
        if (trackedEntityHashTable.containsItem(entityIn.getEntityId()))
        {
            System.err.println("Entity is already tracked: " + entityIn);
            Tools.printStackTrace();
        }
        else
        {
            EntityTrackerEntry entityEntry = !GlobalDefaultsAndData.isFullBypass(entityIn) ? new DSEntityTrackerEntry(entityIn, trackingRange, maxTrackingDistanceThreshold, updateFrequency, sendVelocityUpdates) : new EntityTrackerEntry(entityIn, trackingRange, maxTrackingDistanceThreshold, updateFrequency, sendVelocityUpdates);
            entries.add(entityEntry);
            trackedEntityHashTable.addKey(entityIn.getEntityId(), entityEntry);
            entityEntry.updatePlayerEntities(world.playerEntities);
        }
    }

    public void untrack(Entity entityIn)
    {
        if (entityIn instanceof EntityPlayerMP)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) entityIn;

            for (EntityTrackerEntry entitytrackerentry : entries.toArray(new EntityTrackerEntry[0]))
            {
                entitytrackerentry.removeFromTrackedPlayers(entityplayermp);
            }
        }

        EntityTrackerEntry entitytrackerentry = trackedEntityHashTable.removeObject(entityIn.getEntityId());

        if (entitytrackerentry != null)
        {
            entries.remove(entitytrackerentry);
            entitytrackerentry.sendDestroyEntityPacketToTrackedPlayers();
        }
    }

    public void tick()
    {
        for (EntityTrackerEntry trackerEntry : entries.toArray(new EntityTrackerEntry[0]))
        {
            trackerEntry.updatePlayerList(world.playerEntities);
            for (EntityPlayer player : world.playerEntities) trackerEntry.updatePlayerEntity((EntityPlayerMP) player);
        }
    }

    public void updateVisibility(EntityPlayerMP player)
    {
        //Only called from updatePotionMetadata(), and I'm updating this stuff every tick, so don't need it
    }

    public void sendToTracking(Entity entityIn, Packet<?> packetIn)
    {
        EntityTrackerEntry entitytrackerentry = trackedEntityHashTable.lookup(entityIn.getEntityId());

        if (entitytrackerentry != null)
        {
            entitytrackerentry.sendPacketToTrackedPlayers(packetIn);
        }
    }

    public Set<? extends EntityPlayer> getTrackingPlayers(Entity entity)
    {
        EntityTrackerEntry entry = trackedEntityHashTable.lookup(entity.getEntityId());
        if (entry == null) return java.util.Collections.emptySet();
        else return java.util.Collections.unmodifiableSet(entry.trackingPlayers);
    }

    public void sendToTrackingAndSelf(Entity entityIn, Packet<?> packetIn)
    {
        EntityTrackerEntry entitytrackerentry = trackedEntityHashTable.lookup(entityIn.getEntityId());

        if (entitytrackerentry != null)
        {
            entitytrackerentry.sendToTrackingAndSelf(packetIn);
        }
    }

    public void removePlayerFromTrackers(EntityPlayerMP player)
    {
        for (EntityTrackerEntry entitytrackerentry : entries.toArray(new EntityTrackerEntry[0]))
        {
            entitytrackerentry.removeTrackedPlayerSymmetric(player);
        }
    }

    public void sendLeashedEntitiesInChunk(EntityPlayerMP player, Chunk chunkIn)
    {
        for (EntityTrackerEntry trackerEntry : entries.toArray(new EntityTrackerEntry[0]))
        {
            Entity entity = trackerEntry.getTrackedEntity();

            if (entity != player && entity.chunkCoordX == chunkIn.x && entity.chunkCoordZ == chunkIn.z)
            {
                trackerEntry.updatePlayerEntity(player);
            }
        }
    }

    public void setViewDistance(int distance)
    {
        maxTrackingDistanceThreshold = Tools.min((distance - 1) * 16, EntitySightData.playerMaxSightDistance);

        for (EntityTrackerEntry entitytrackerentry : entries.toArray(new EntityTrackerEntry[0]))
        {
            entitytrackerentry.setMaxRange(maxTrackingDistanceThreshold);
        }
    }
}