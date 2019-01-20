package noppes.npcs.api.handler;

import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogCategory;

import java.util.List;

public interface IDialogHandler
{

    public List<IDialogCategory> categories();

    public IDialog get(int id);
}
