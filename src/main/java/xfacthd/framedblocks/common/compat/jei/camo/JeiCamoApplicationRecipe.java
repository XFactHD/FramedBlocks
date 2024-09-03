package xfacthd.framedblocks.common.compat.jei.camo;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import xfacthd.framedblocks.common.FBContent;

import java.util.Arrays;
import java.util.List;

public final class JeiCamoApplicationRecipe implements CraftingRecipe
{
    private final Ingredient frameStacks;
    private final Ingredient copyTool;
    private final Ingredient firstInputStacks;
    private final Ingredient secondInputStacks;
    private final List<ItemStack> results;

    public JeiCamoApplicationRecipe(
            Ingredient frameStacks,
            Ingredient copyTool,
            Ingredient firstInputStacks,
            Ingredient secondInputStacks,
            List<ItemStack> results
    )
    {
        this.frameStacks = frameStacks;
        this.copyTool = copyTool;
        this.firstInputStacks = firstInputStacks;
        this.secondInputStacks = secondInputStacks;
        this.results = results;
    }

    public Ingredient getFrameStacks()
    {
        return frameStacks;
    }

    public Ingredient getCopyTool()
    {
        return copyTool;
    }

    public Ingredient getFirstIngredient()
    {
        return firstInputStacks;
    }

    public Ingredient getSecondIngredient()
    {
        return secondInputStacks;
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

    public List<List<ItemStack>> getDisplayInputs(CamoCraftingHelper camoCraftingHelper)
    {
        return List.of(
                camoCraftingHelper.getEmptyFrameStacks(frameStacks),
                Arrays.asList(copyTool.getItems()),
                // pick a prime number count so that more combinations are shown over time
                camoCraftingHelper.getCamoExampleStacks(firstInputStacks, 97),
                // pick a lower number so that the blank ingredient is shown more often
                camoCraftingHelper.getDoubleCamoExampleStacks(secondInputStacks, 11)
        );
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FBContent.RECIPE_SERIALIZER_JEI_CAMO.value();
    }
}
