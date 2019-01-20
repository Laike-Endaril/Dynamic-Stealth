package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.INPCJob;

public interface IJobFollower extends INPCJob
{

    public String getFollowing();

    public boolean isFollowing();

    public void setFollowing(String name);

    public ICustomNpc getFollowingNpc();
}
