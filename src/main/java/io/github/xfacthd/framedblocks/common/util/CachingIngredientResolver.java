package io.github.xfacthd.framedblocks.common.util;

import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public final class CachingIngredientResolver
{
    private final ContextMap context;
    @Nullable
    private Ingredient lastIngredient = null;
    private List<ItemStack> resolvedStacks = List.of();

    public CachingIngredientResolver(Level level)
    {
        this.context = SlotDisplayContext.fromLevel(level);
    }

    public List<ItemStack> getStacks(Ingredient ingredient)
    {
        if (!ingredient.equals(lastIngredient))
        {
            lastIngredient = ingredient;
            resolvedStacks = ingredient.display().resolveForStacks(context);
        }
        return resolvedStacks;
    }

    public ItemStack getFirstStack(Ingredient ingredient)
    {
        List<ItemStack> stacks = getStacks(ingredient);
        return stacks.isEmpty() ? ItemStack.EMPTY : stacks.getFirst();
    }

    public static final class Multi
    {
        private final CachingIngredientResolver[] resolvers;

        public Multi(Level level, int size)
        {
            this.resolvers = new CachingIngredientResolver[size];
            Arrays.setAll(resolvers, _ -> new CachingIngredientResolver(level));
        }

        public List<ItemStack> getStacks(int idx, Ingredient ingredient)
        {
            return resolvers[idx].getStacks(ingredient);
        }

        public ItemStack getFirstStack(int idx, Ingredient ingredient)
        {
            return resolvers[idx].getFirstStack(ingredient);
        }
    }
}
