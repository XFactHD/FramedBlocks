package xfacthd.framedblocks.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;

public final class FramingSawRecipe implements Recipe<Container>
{
    public static final int CUBE_MATERIAL_VALUE = 6144; // Empirically determined value
    public static final int MAX_ADDITIVE_COUNT = 3;
    private static final Lazy<ItemStack> TOAST_ICON = Lazy.of(() -> new ItemStack(FBContent.blockFramingSaw.get()));

    private final ResourceLocation id;
    private final int materialAmount;
    private final List<FramingSawRecipeAdditive> additives;
    private final ItemStack result;
    private final IBlockType resultType;
    private final boolean disabled;

    FramingSawRecipe(ResourceLocation id, int materialAmount, List<FramingSawRecipeAdditive> additives, ItemStack result, IBlockType resultType, boolean disabled)
    {
        this.id = id;
        this.materialAmount = materialAmount;
        this.additives = additives;
        this.result = result;
        this.resultType = resultType;
        this.disabled = disabled;
    }

    @Override
    public boolean matches(Container container, Level level)
    {
        return matchWithReason(container, level).success();
    }

    public FramingSawRecipeMatchResult matchWithReason(Container container, Level level)
    {
        ItemStack input = container.getItem(0);
        if (input.isEmpty())
        {
            return FramingSawRecipeMatchResult.MATERIAL_VALUE;
        }

        int inputValue = FramingSawRecipeCalculation.getInputValue(input, level.isClientSide());
        int totalInputValue = inputValue * input.getCount();
        if (totalInputValue < materialAmount)
        {
            return FramingSawRecipeMatchResult.MATERIAL_VALUE;
        }

        long matLcm = FramingSawRecipeCalculation.getMaterialLCM(this, inputValue);
        if (matLcm > totalInputValue)
        {
            return FramingSawRecipeMatchResult.MATERIAL_LCM;
        }

        for (int idx = 0; idx < MAX_ADDITIVE_COUNT; idx++)
        {
            ItemStack stack = container.getItem(idx + 1);
            FramingSawRecipeAdditive additive = idx < additives.size() ? additives.get(idx) : null;

            boolean empty = stack.isEmpty();

            if (empty && additive == null)
            {
                continue;
            }

            if (!empty && additive == null)
            {
                return FramingSawRecipeMatchResult.UNEXPECTED_ADDITIVE[idx];
            }
            else if (empty /* && additive != null*/)
            {
                return FramingSawRecipeMatchResult.MISSING_ADDITIVE[idx];
            }
            else if (!additive.ingredient().test(stack))
            {
                return FramingSawRecipeMatchResult.INCORRECT_ADDITIVE[idx];
            }

            if (stack.getCount() < FramingSawRecipeCalculation.getAdditiveCount(this, additive, matLcm))
            {
                return FramingSawRecipeMatchResult.INSUFFICIENT_ADDITIVE[idx];
            }
        }
        return FramingSawRecipeMatchResult.SUCCESS;
    }

    public FramingSawRecipeCalculation makeCraftingCalculation(Container container, boolean client)
    {
        return new FramingSawRecipeCalculation(this, container, client);
    }

    @Override
    public ItemStack assemble(Container container) { return result.copy(); }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    public int getMaterialAmount() { return materialAmount; }

    public List<FramingSawRecipeAdditive> getAdditives() { return additives; }

    @Override
    public ItemStack getResultItem() { return result; }

    public IBlockType getResultType() { return resultType; }

    public boolean isDisabled() { return disabled; }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public boolean isSpecial() { return true; }

    @Override
    public ItemStack getToastSymbol() { return TOAST_ICON.get(); }

    @Override
    public RecipeSerializer<?> getSerializer() { return FBContent.recipeSerializerFramingSawRecipe.get(); }

    @Override
    public RecipeType<?> getType() { return FBContent.recipeTypeFramingSawRecipe.get(); }
}
