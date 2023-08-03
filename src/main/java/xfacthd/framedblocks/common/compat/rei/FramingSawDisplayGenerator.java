package xfacthd.framedblocks.common.compat.rei;

import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.*;

import java.util.*;
import java.util.stream.Stream;

public final class FramingSawDisplayGenerator implements DynamicDisplayGenerator<FramingSawDisplay>
{
    @Override
    public Optional<List<FramingSawDisplay>> getRecipeFor(EntryStack<?> entry)
    {
        if (!entry.isEmpty() && entry.getType() == VanillaEntryTypes.ITEM)
        {
            ItemStack input = new ItemStack(FBContent.BLOCK_FRAMED_CUBE.get());
            ItemStack output = entry.castValue();
            FramingSawRecipe recipe = FramingSawRecipeCache.get(true).findRecipeFor(output);
            if (recipe != null)
            {
                return Optional.of(List.of(makeDisplay(recipe, input)));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<FramingSawDisplay>> getUsageFor(EntryStack<?> entry)
    {
        if (!entry.isEmpty() && entry.getType() == VanillaEntryTypes.ITEM)
        {
            FramingSawRecipeCache cache = FramingSawRecipeCache.get(true);
            ItemStack input = entry.castValue();
            if (cache.getMaterialValue(input.getItem()) > -1)
            {
                List<FramingSawRecipe> recipes = cache.getRecipes();
                List<FramingSawDisplay> displays = new ArrayList<>(recipes.size());
                for (FramingSawRecipe recipe : recipes)
                {
                    displays.add(makeDisplay(recipe, input));
                }
                return Optional.of(displays);
            }
        }
        return Optional.empty();
    }

    @Override // TODO: is this even close to correct???
    public Optional<List<FramingSawDisplay>> generate(ViewSearchBuilder builder)
    {
        if (!builder.getCategories().contains(FramingSawRecipeCategory.SAW_CATEGORY))
        {
            return Optional.empty();
        }
        Set<CategoryIdentifier<?>> catFilter = builder.getFilteringCategories();
        if (!catFilter.isEmpty() && !catFilter.contains(FramingSawRecipeCategory.SAW_CATEGORY))
        {
            return Optional.empty();
        }

        List<FramingSawDisplay> displays = new ArrayList<>();
        builder.getRecipesFor().forEach(stack -> getRecipeFor(stack).ifPresent(displays::addAll));
        builder.getUsagesFor().forEach(stack -> getUsageFor(stack).ifPresent(displays::addAll));
        return Optional.of(displays);
    }



    private static FramingSawDisplay makeDisplay(FramingSawRecipe recipe, ItemStack input)
    {
        boolean inputWithAdditives = FramingSawRecipeCache.get(true).containsAdditive(input.getItem());

        List<FramingSawRecipeAdditive> additives = recipe.getAdditives();
        List<EntryIngredient> inputs = new ArrayList<>(additives.size() + 1);
        FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(
                new SimpleContainer(input), true
        );
        int outputCount = calc.getOutputCount();

        ItemStack inputStack = input.copy();
        inputStack.setCount(calc.getInputCount());
        inputs.add(EntryIngredient.of(EntryStacks.of(inputStack)));

        for (FramingSawRecipeAdditive additive : additives)
        {
            int addCount = additive.count() * (outputCount / recipe.getResult().getCount());
            List<EntryStack<ItemStack>> additiveStacks = Stream.of(additive.ingredient().getItems())
                    .map(ItemStack::copy)
                    .peek(s -> s.setCount(addCount))
                    .map(EntryStacks::of)
                    .toList();
            inputs.add(EntryIngredient.of(additiveStacks));
        }

        ItemStack result = recipe.getResult().copy();
        result.setCount(outputCount);
        EntryIngredient output = EntryIngredient.of(EntryStacks.of(result));

        return new FramingSawDisplay(recipe, inputs, output, inputWithAdditives);
    }
}
