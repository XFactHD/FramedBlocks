package io.github.xfacthd.framedblocks.common.crafting.saw;

import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class FramingSawRecipeCache
{
    private static final FramingSawRecipeCache SERVER_INSTANCE = new FramingSawRecipeCache();
    private static final FramingSawRecipeCache CLIENT_INSTANCE = new FramingSawRecipeCache();
    private static final Identifier LISTENER_ID = Utils.id("framing_saw_recipes");

    private final List<RecipeHolder<FramingSawRecipe>> recipes = new ArrayList<>();
    private final List<RecipeHolder<FramingSawRecipe>> recipesView = Collections.unmodifiableList(recipes);
    private final Map<Item, RecipeHolder<FramingSawRecipe>> recipesByResult = new IdentityHashMap<>();
    private final Map<Item, RecipeHolder<FramingSawRecipe>> recipesWithAdditives = new IdentityHashMap<>();
    private final Reference2IntMap<Item> materialValues = new Reference2IntOpenHashMap<>();
    private boolean recipesPopulated = false;

    private void update(RecipeMap recipeMap)
    {
        clear();

        recipes.addAll(recipeMap.byType(FBContent.RECIPE_TYPE_FRAMING_SAW_RECIPE.value()));
        recipes.sort(FramingSawRecipeCache::sortRecipes);

        recipes.forEach(holder ->
        {
            FramingSawRecipe recipe = holder.value();
            ItemStackTemplate result = recipe.getResult();
            int materialValue = recipe.getMaterialAmount();
            materialValues.put(result.item().value(), materialValue / result.count());
        });

        // Remove disabled recipes after extracting material values
        recipes.removeIf(h -> h.value().isDisabled());

        recipes.forEach(holder ->
        {
            FramingSawRecipe recipe = holder.value();

            ItemStackTemplate result = recipe.getResult();
            recipesByResult.put(result.item().value(), holder);

            if (!recipe.getAdditives().isEmpty())
            {
                recipesWithAdditives.put(result.item().value(), holder);
            }
        });

        recipesPopulated = true;
    }

    public void clear()
    {
        recipesPopulated = false;
        recipes.clear();
        recipesByResult.clear();
        recipesWithAdditives.clear();
        materialValues.clear();
    }

    public boolean isPopulated()
    {
        return recipesPopulated;
    }

    public List<RecipeHolder<FramingSawRecipe>> getRecipes()
    {
        return recipesView;
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

    public static void onAddReloadListener(AddServerReloadListenersEvent event)
    {
        event.addListener(LISTENER_ID, new Reloader(event.getServerResources()));
    }

    public static void onDataPackSync(OnDatapackSyncEvent event)
    {
        event.sendRecipes(FBContent.RECIPE_TYPE_FRAMING_SAW_RECIPE.value());
    }

    public static void onRecipesReceived(RecipesReceivedEvent event)
    {
        CLIENT_INSTANCE.update(event.getRecipeMap());
    }

    private static int sortRecipes(RecipeHolder<FramingSawRecipe> holder1, RecipeHolder<FramingSawRecipe> holder2)
    {
        FramingSawRecipe r1 = holder1.value();
        FramingSawRecipe r2 = holder2.value();
        return sortRecipes(r1.getResult(), r2.getResult(), r1.getResultType(), r2.getResultType());
    }

    public static int sortRecipes(ItemStackTemplate resultOne, ItemStackTemplate resultTwo, IBlockType typeOne, IBlockType typeTwo)
    {
        String ns1 = BuiltInRegistries.ITEM.getKey(resultOne.item().value()).getNamespace();
        String ns2 = BuiltInRegistries.ITEM.getKey(resultTwo.item().value()).getNamespace();

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
            FramingSawRecipeCache.SERVER_INSTANCE.update(serverResources.getRecipeManager().recipeMap());
        }
    }
}
