package noppes.npcs.api.handler;

import net.minecraft.item.ItemStack;
import noppes.npcs.api.handler.data.IRecipe;

import java.util.List;

public interface IRecipeHandler
{

    public List<IRecipe> getGlobalList();

    public List<IRecipe> getCarpentryList();

    public IRecipe addRecipe(String name, boolean global, ItemStack result, Object... objects);

    public IRecipe addRecipe(String name, boolean global, ItemStack result, int width, int height, ItemStack... recipe);

    public IRecipe delete(int id);
}
