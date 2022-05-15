package xfacthd.framedblocks.tests;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import xfacthd.framedblocks.FramedBlocks;

import java.util.*;

@GameTestHolder(FramedBlocks.MODID)
@PrefixGameTestTemplate(value = false)
public final class NonWorldTests
{
    //static { GlobalTestReporter.replaceWith(new VerboseLogTestReporter()); }

    @GameTest(template = "empty", batch = "non_world")
    public static void checkRecipeCollisions(GameTestHelper helper)
    {
        RecipeManager recipeManager = helper.getLevel().getRecipeManager();
        List<? extends Recipe<CraftingContainer>> recipes = recipeManager.getRecipeIds()
                .filter(id -> id.getNamespace().equals(FramedBlocks.MODID))
                .map(recipeManager::byKey)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(CraftingRecipe.class::isInstance)
                .map(CraftingRecipe.class::cast)
                .toList();

        Multimap<ResourceLocation, ResourceLocation> collisions = ArrayListMultimap.create();
        CraftingContainer container = new CraftingContainer(helper.makeMockPlayer().containerMenu, 3, 3);
        NonNullList<ItemStack> items = Objects.requireNonNull(ObfuscationReflectionHelper.getPrivateValue(CraftingContainer.class, container, "f_39320_"));

        recipes.forEach(recipe ->
        {
            List<Ingredient> ingredients = recipe.getIngredients();

            for (int x = 0; x < 3; x++)
            {
                for (int y = 0; y < 3; y++)
                {
                    int destIdx = x + (y * 3);
                    int srcIdx = destIdx;
                    if (recipe instanceof ShapedRecipe shaped)
                    {
                        if (x >= shaped.getRecipeWidth())
                        {
                            items.set(destIdx, ItemStack.EMPTY);
                            continue;
                        }

                        srcIdx = x + (y * shaped.getRecipeWidth());
                    }

                    if (srcIdx < ingredients.size() && !ingredients.get(srcIdx).isEmpty())
                    {
                        items.set(destIdx, ingredients.get(srcIdx).getItems()[0]);
                    }
                    else
                    {
                        items.set(destIdx, ItemStack.EMPTY);
                    }
                }
            }

            recipes.stream()
                    .filter(other -> other != recipe)
                    .forEach(other ->
                    {
                        if (other.matches(container, helper.getLevel()))
                        {
                            collisions.put(recipe.getId(), other.getId());
                        }
                    });
        });

        if (!collisions.isEmpty())
        {
            StringBuilder message = new StringBuilder("Found the following recipe collisions:");
            collisions.forEach((r1, r2) ->
                message.append(String.format("\n\t%s collides with %s", r1, r2))
            );
            helper.fail(message.toString());
        }
        else
        {
            helper.succeed();
        }
    }



    private NonWorldTests() { }
}
