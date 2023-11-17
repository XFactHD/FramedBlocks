package xfacthd.framedblocks.common.crafting;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.Util;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;

public final class FramingSawRecipeCache
{
    private static final FramingSawRecipeCache SERVER_INSTANCE = new FramingSawRecipeCache();
    private static final FramingSawRecipeCache CLIENT_INSTANCE = new FramingSawRecipeCache();

    private final List<RecipeHolder<FramingSawRecipe>> recipes = new ArrayList<>();
    private final Map<Item, RecipeHolder<FramingSawRecipe>> recipesByResult = new IdentityHashMap<>();
    private final Map<Item, RecipeHolder<FramingSawRecipe>> recipesWithAdditives = new IdentityHashMap<>();
    private final Object2IntMap<Item> materialValues = new Object2IntOpenCustomHashMap<>(Util.identityStrategy());

    public void update(RecipeManager recipeManager)
    {
        clear();

        recipes.addAll(recipeManager.getAllRecipesFor(FBContent.RECIPE_TYPE_FRAMING_SAW_RECIPE.get()));
        recipes.sort(FramingSawRecipeCache::sortRecipes);

        recipes.forEach(holder ->
        {
            FramingSawRecipe recipe = holder.value();
            ItemStack result = recipe.getResult();
            int materialValue = recipe.getMaterialAmount();
            materialValues.put(result.getItem(), materialValue / result.getCount());
        });

        // Remove disabled recipes after extracting material values
        recipes.removeIf(h -> h.value().isDisabled());

        recipes.forEach(holder ->
        {
            FramingSawRecipe recipe = holder.value();

            ItemStack result = recipe.getResult();
            recipesByResult.put(result.getItem(), holder);

            if (!recipe.getAdditives().isEmpty())
            {
                recipesWithAdditives.put(result.getItem(), holder);
            }
        });
    }

    public void clear()
    {
        recipes.clear();
        recipesByResult.clear();
        recipesWithAdditives.clear();
        materialValues.clear();
    }

    public List<RecipeHolder<FramingSawRecipe>> getRecipes()
    {
        return Collections.unmodifiableList(recipes);
    }

    @Nullable
    public RecipeHolder<FramingSawRecipe> findRecipeFor(ItemStack result)
    {
        return recipesByResult.get(result.getItem());
    }

    public Set<Item> getKnownItems()
    {
        return materialValues.keySet();
    }

    public int getMaterialValue(Item item)
    {
        return materialValues.getOrDefault(item, -1);
    }

    public boolean containsAdditive(Item item)
    {
        return recipesWithAdditives.containsKey(item);
    }

    public List<RecipeHolder<FramingSawRecipe>> getRecipesWithAdditive(ItemStack additive)
    {
        return recipesWithAdditives.values()
                .stream()
                .filter(recipe -> recipe.value().getAdditives()
                        .stream()
                        .map(FramingSawRecipeAdditive::ingredient)
                        .anyMatch(ing -> ing.test(additive))
                )
                .toList();
    }



    public static FramingSawRecipeCache get(boolean client)
    {
        return client ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    public static void onAddReloadListener(final AddReloadListenerEvent event)
    {
        event.addListener(new Reloader(event.getServerResources()));
    }

    private static int sortRecipes(RecipeHolder<FramingSawRecipe> holder1, RecipeHolder<FramingSawRecipe> holder2)
    {
        FramingSawRecipe r1 = holder1.value();
        FramingSawRecipe r2 = holder2.value();
        return sortRecipes(r1.getResult(), r2.getResult(), r1.getResultType(), r2.getResultType());
    }

    public static int sortRecipes(ItemStack resultOne, ItemStack resultTwo, IBlockType typeOne, IBlockType typeTwo)
    {
        //noinspection ConstantConditions
        String ns1 = ForgeRegistries.ITEMS.getKey(resultOne.getItem()).getNamespace();
        //noinspection ConstantConditions
        String ns2 = ForgeRegistries.ITEMS.getKey(resultTwo.getItem()).getNamespace();

        if (!ns1.equals(ns2))
        {
            if (ns1.equals(FramedConstants.MOD_ID))
            {
                return -1;
            }
            if (ns2.equals(FramedConstants.MOD_ID))
            {
                return 1;
            }
            return ns1.compareTo(ns2);
        }

        // Assume that items from the same namespace use the same IBlockType implementation and are therefore comparable
        return typeOne.compareTo(typeTwo);
    }



    private record Reloader(ReloadableServerResources serverResources) implements ResourceManagerReloadListener
    {
        @Override
        public void onResourceManagerReload(ResourceManager resourceManager)
        {
            FramingSawRecipeCache.SERVER_INSTANCE.update(serverResources.getRecipeManager());
        }
    }
}
