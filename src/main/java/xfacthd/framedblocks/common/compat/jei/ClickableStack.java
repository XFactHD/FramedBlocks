package xfacthd.framedblocks.common.compat.jei;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

record ClickableStack(ITypedIngredient<ItemStack> stack, Rect2i area) implements IClickableIngredient<ItemStack>
{
    @SuppressWarnings("removal")
	@Override
    public ITypedIngredient<ItemStack> getTypedIngredient()
    {
        return stack;
    }

    @Override
    public ItemStack getIngredient()
    {
        return stack.getIngredient();
    }

    @Override
    public IIngredientType<ItemStack> getIngredientType()
    {
        return stack.getType();
    }

    @Override
    public Rect2i getArea()
    {
        return area;
    }
}
