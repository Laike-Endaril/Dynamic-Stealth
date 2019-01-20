package noppes.npcs.api.handler;

import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;

import java.util.List;

public interface IQuestHandler
{

    public List<IQuestCategory> categories();

    public IQuest get(int id);
}
