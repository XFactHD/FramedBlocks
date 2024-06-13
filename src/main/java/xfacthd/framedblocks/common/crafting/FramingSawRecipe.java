package xfacthd.framedblocks.common.crafting;

import com.google.gson.JsonSyntaxException;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;

public final class FramingSawRecipe implements Recipe<RecipeInput>
{
    public static final int CUBE_MATERIAL_VALUE = 6144; // Empirically determined value
    public static final int MAX_ADDITIVE_COUNT = 3;
    private static final Lazy<ItemStack> TOAST_ICON = Lazy.of(() -> new ItemStack(FBContent.BLOCK_FRAMING_SAW.value()));

    private final int materialAmount;
    private final List<FramingSawRecipeAdditive> additives;
    private final ItemStack result;
    private final IBlockType resultType;
    private final boolean disabled;

    FramingSawRecipe(int materialAmount, List<FramingSawRecipeAdditive> additives, ItemStack result, boolean disabled)
    {
        this.materialAmount = materialAmount;
        this.additives = additives;
        this.result = result;
        this.resultType = findResultType(result);
        this.disabled = disabled;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level)
    {
        return matchWithResult(recipeInput, level).success();
    }

    public FramingSawRecipeMatchResult matchWithResult(RecipeInput recipeInput, Level level)
    {
        ItemStack input = recipeInput.getItem(0);
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

        if (FramingSawRecipeCalculation.getOutputCount(materialAmount, result, matLcm) > result.getMaxStackSize())
        {
            return FramingSawRecipeMatchResult.OUTPUT_SIZE;
        }

        for (int idx = 0; idx < MAX_ADDITIVE_COUNT; idx++)
        {
            ItemStack stack = recipeInput.getItem(idx + 1);
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

    public FramingSawRecipeCalculation makeCraftingCalculation(RecipeInput container, boolean client)
    {
        return new FramingSawRecipeCalculation(this, container, client);
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider access)
    {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    public int getMaterialAmount()
    {
        return materialAmount;
    }

    public List<FramingSawRecipeAdditive> getAdditives()
    {
        return additives;
    }

    public ItemStack getResult()
    {
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access)
    {
        return result;
    }

    public IBlockType getResultType()
    {
        return resultType;
    }

    public boolean isDisabled()
    {
        return disabled;
    }

    @Override
    public boolean isSpecial()
    {
        return true;
    }

    @Override
    public ItemStack getToastSymbol()
    {
        return TOAST_ICON.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FBContent.RECIPE_SERIALIZER_FRAMING_SAW_RECIPE.value();
    }

    @Override
    public RecipeType<?> getType()
    {
        return FBContent.RECIPE_TYPE_FRAMING_SAW_RECIPE.value();
    }



    private static IBlockType findResultType(ItemStack result)
    {
        if (!(result.getItem() instanceof BlockItem item))
        {
            throw new JsonSyntaxException("Result items must be BlockItems");
        }
        if (!(item.getBlock() instanceof IFramedBlock block))
        {
            throw new JsonSyntaxException("Block of result items must be IFramedBlocks");
        }
        return block.getBlockType();
    }
}
