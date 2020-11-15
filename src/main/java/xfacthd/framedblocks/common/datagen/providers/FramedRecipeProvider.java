package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.*;
import xfacthd.framedblocks.FramedBlocks;

import java.util.function.Consumer;

public class FramedRecipeProvider extends RecipeProvider
{
    public FramedRecipeProvider(DataGenerator gen) { super(gen); }

    @Override
    public String getName() { return super.getName() + ": " + FramedBlocks.MODID; }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {

    }
}