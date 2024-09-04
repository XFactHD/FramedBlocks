package xfacthd.framedblocks.common.compat.jei.camo;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;

public final class JeiCamoApplicationRecipe implements CraftingRecipe
{
    private final Ingredient frame;
    private final Ingredient copyTool;
    private final Ingredient camoOne;
    private final Ingredient camoTwo;
    private final List<ItemStack> results;

    public JeiCamoApplicationRecipe(
            Ingredient frame,
            Ingredient copyTool,
            Ingredient camoOne,
            Ingredient camoTwo,
            List<ItemStack> results
    )
    {
        this.frame = frame;
        this.copyTool = copyTool;
        this.camoOne = camoOne;
        this.camoTwo = camoTwo;
        this.results = results;
    }

    public Ingredient getFrame()
    {
        return frame;
    }

    public Ingredient getCopyTool()
    {
        return copyTool;
    }

    public Ingredient getCamoOne()
    {
        return camoOne;
    }

    public Ingredient getCamoTwo()
    {
        return camoTwo;
    }

    public List<ItemStack> getResults()
    {
        return results;
    }

    @Override
    public CraftingBookCategory category()
    {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level)
    {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1)
    {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider)
    {
        return results.isEmpty() ? ItemStack.EMPTY : results.getFirst();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FBContent.RECIPE_SERIALIZER_JEI_CAMO.value();
    }
}
