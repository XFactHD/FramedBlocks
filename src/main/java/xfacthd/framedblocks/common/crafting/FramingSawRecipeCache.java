package xfacthd.framedblocks.common.crafting;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.Util;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;

public final class FramingSawRecipeCache
{
    private static final FramingSawRecipeCache SERVER_INSTANCE = new FramingSawRecipeCache();
    private static final FramingSawRecipeCache CLIENT_INSTANCE = new FramingSawRecipeCache();

    private final List<FramingSawRecipe> recipes = new ArrayList<>();
    private final Set<Item> containsAdditive = Sets.newIdentityHashSet();
    private final Object2IntMap<Item> materialValues = new Object2IntOpenCustomHashMap<>(Util.identityStrategy());

    public void update(RecipeManager recipeManager)
    {
        clear();

        recipes.addAll(recipeManager.getAllRecipesFor(FBContent.recipeTypeFramingSawRecipe.get()));
        recipes.sort(FramingSawRecipeCache::sortRecipes);

        recipes.forEach(recipe ->
        {
            ItemStack result = recipe.getResultItem();

            if (!recipe.getAdditives().isEmpty())
            {
                containsAdditive.add(result.getItem());
            }

            int materialValue = recipe.getMaterialAmount();
            materialValues.put(result.getItem(), materialValue / result.getCount());
        });

        // Remove disabled recipes after extracting material values
        recipes.removeIf(FramingSawRecipe::isDisabled);
    }

    public void clear()
    {
        recipes.clear();
        containsAdditive.clear();
        materialValues.clear();
    }

    public List<FramingSawRecipe> getRecipes()
    {
        return Collections.unmodifiableList(recipes);
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
        return containsAdditive.contains(item);
    }



    public static FramingSawRecipeCache get(boolean client)
    {
        return client ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    public static void onAddReloadListener(final AddReloadListenerEvent event)
    {
        event.addListener(new Reloader(event.getServerResources()));
    }

    private static int sortRecipes(FramingSawRecipe r1, FramingSawRecipe r2)
    {
        //noinspection ConstantConditions
        String ns1 = ForgeRegistries.ITEMS.getKey(r1.getResultItem().getItem()).getNamespace();
        //noinspection ConstantConditions
        String ns2 = ForgeRegistries.ITEMS.getKey(r2.getResultItem().getItem()).getNamespace();

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
        IBlockType t1 = r1.getResultType();
        IBlockType t2 = r2.getResultType();
        return t1.compareTo(t2);
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
