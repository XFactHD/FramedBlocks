package xfacthd.framedblocks.common.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;

import java.util.List;
import java.util.Optional;

public final class FramingSawDisplay implements Display
{
    private final RecipeHolder<FramingSawRecipe> recipe;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<ResourceLocation> location;
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> output;
    private final boolean inputWithAdditives;

    public FramingSawDisplay(
            RecipeHolder<FramingSawRecipe> recipe,
            List<EntryIngredient> inputs,
            EntryIngredient output,
            boolean inputWithAdditives
    )
    {
        this.recipe = recipe;
        this.location = Optional.of(recipe.id());
        this.inputs = inputs;
        this.output = List.of(output);
        this.inputWithAdditives = inputWithAdditives;
    }

    @Override
    public List<EntryIngredient> getInputEntries()
    {
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries()
    {
        return output;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return FramingSawRecipeCategory.SAW_CATEGORY;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation()
    {
        return location;
    }

    public RecipeHolder<FramingSawRecipe> getRecipe()
    {
        return recipe;
    }

    public boolean hasInputWithAdditives()
    {
        return inputWithAdditives;
    }
}
