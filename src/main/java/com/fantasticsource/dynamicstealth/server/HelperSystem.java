package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.scoreboard.Team;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IFaction;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;

public class HelperSystem
{
    public static boolean shouldHelp(EntityLivingBase helper, EntityLivingBase troubledOne, boolean checkWorldMatch, double maxDistSquared)
    {
        return helpPriority(helper, troubledOne, checkWorldMatch, maxDistSquared) > 0;
    }

    public static int helpPriority(EntityLivingBase helper, EntityLivingBase troubledOne, boolean checkWorldMatch, double maxDistSquared)
    {
        if (troubledOne == null || helper == null) return 0;
        if (Threat.getTarget(helper) == troubledOne) return 0;
        if (!checkWorldMatch && (troubledOne.world == null || troubledOne.world != helper.world)) return 0;
        if (troubledOne.getDistanceSq(helper) > maxDistSquared) return 0;


        //Ownership
        if (helper instanceof IEntityOwnable)
        {
            Entity helperOwner = ((IEntityOwnable) helper).getOwner();
            if (helperOwner != null)
            {
                //Owner needs help
                if (troubledOne == helperOwner)
                {
                    if (serverSettings.helperSystemSettings.ownership.helpOwner) return 100;
                }

                //Something with same owner needs help
                else if (troubledOne instanceof IEntityOwnable && ((IEntityOwnable) troubledOne).getOwner() == helperOwner)
                {
                    if (serverSettings.helperSystemSettings.ownership.helpOtherWithSameOwner) return 90;
                }

                //Something unrelated needs help.  If we're only dedicated to our owner, don't help them
                else if (serverSettings.helperSystemSettings.ownership.dedicated) return 0;
            }
        }
        if (troubledOne instanceof IEntityOwnable)
        {
            //Something we own needs help
            if (((IEntityOwnable) troubledOne).getOwner() == helper)
            {
                if (serverSettings.helperSystemSettings.ownership.helpOwned) return 80;
            }
        }


        //Teams
        Team troubledOneTeam = troubledOne.getTeam();
        if (troubledOneTeam != null)
        {
            //Same team
            if (troubledOneTeam.isSameTeam(helper.getTeam()))
            {
                if (serverSettings.helperSystemSettings.teams.helpSame) return 70;
            }

            //Different team
            else if (helper.getTeam() != null)
            {
                if (serverSettings.helperSystemSettings.teams.dontHelpOther) return 0;
            }
        }


        //CNPC factions
        IEntity cnpcEntityTroubled = Compat.customnpcs ? NpcAPI.Instance().getIEntity(troubledOne) : null;
        IEntity cnpcEntityHelper = Compat.customnpcs ? NpcAPI.Instance().getIEntity(helper) : null;
        if (cnpcEntityHelper != null && cnpcEntityTroubled != null)
        {
            int factionStatus = cnpcRep(cnpcEntityHelper, cnpcEntityTroubled);

            //Good rep
            if (factionStatus > 0)
            {
                if (serverSettings.helperSystemSettings.cnpcFactions.helpGoodRep) return 60;
            }

            //Bad rep
            else if (factionStatus < 0)
            {
                if (serverSettings.helperSystemSettings.cnpcFactions.dontHelpBadRep) return 0;
            }
        }


        //Entity types
        if (troubledOne.getClass() == helper.getClass() && !(cnpcEntityTroubled instanceof ICustomNpc))
        {
            if (serverSettings.helperSystemSettings.helpSameType) return 50;
        }

        return 0;
    }


    private static int cnpcRep(IEntity entity1, IEntity entity2)
    {
        if (entity1 instanceof ICustomNpc)
        {
            if (entity2 instanceof ICustomNpc) return cnpcRep((ICustomNpc) entity1, (ICustomNpc) entity2);
            if (entity2 instanceof IPlayer) return cnpcRep((ICustomNpc) entity1, (IPlayer) entity2);
        }
        else if (entity1 instanceof IPlayer)
        {
            if (entity2 instanceof ICustomNpc) return cnpcRep((ICustomNpc) entity2, (IPlayer) entity1);
        }

        return 0;
    }

    private static int cnpcRep(ICustomNpc entity1, ICustomNpc entity2)
    {
        IFaction faction1 = entity1.getFaction();
        boolean nonNull1 = faction1 != null;
        if (nonNull1 && faction1.hostileToNpc(entity2)) return -1;

        IFaction faction2 = entity2.getFaction();
        boolean nonNull2 = faction2 != null;
        if (nonNull2)
        {
            if (faction2.hostileToNpc(entity1)) return -1;

            if (nonNull1)
            {
                if (faction1 == faction2) return 1;

                if (faction1.hostileToFaction(faction2.getId()) || faction2.hostileToFaction(faction1.getId())) return -1;
            }
        }

        return 0;
    }

    private static int cnpcRep(ICustomNpc entity1, IPlayer entity2)
    {
        IFaction faction = entity1.getFaction();
        if (faction == null) return 0;
        return entity2.factionStatus(faction.getId());
    }
}
