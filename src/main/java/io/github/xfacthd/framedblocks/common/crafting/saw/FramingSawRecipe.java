package io.github.xfacthd.framedblocks.common.crafting.saw;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.datagen.recipes.AbstractFramingSawRecipeProvider;
import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.FramingSawRecipeBuilder;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.List;

public final class FramingSawRecipe implements Recipe<RecipeInput>
{
    public static final int CUBE_MATERIAL_VALUE = AbstractFramingSawRecipeProvider.CUBE_MATERIAL_VALUE; // Empirically determined value
    public static final int MAX_ADDITIVE_COUNT = FramingSawRecipeBuilder.MAX_ADDITIVE_COUNT;
    public static final MapCodec<FramingSawRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("material").forGetter(FramingSawRecipe::getMaterialAmount),
            FramingSawRecipeAdditive.CODEC.sizeLimitedListOf(FramingSawRecipe.MAX_ADDITIVE_COUNT).optionalFieldOf("additives", List.of()).forGetter(FramingSawRecipe::getAdditives),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(FramingSawRecipe::getResult),
            Codec.BOOL.optionalFieldOf("disabled", false).forGetter(FramingSawRecipe::isDisabled)
    ).apply(inst, FramingSawRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FramingSawRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            FramingSawRecipe::getMaterialAmount,
            FramingSawRecipeAdditive.STREAM_CODEC.apply(ByteBufCodecs.list()),
            FramingSawRecipe::getAdditives,
            ItemStackTemplate.STREAM_CODEC,
            FramingSawRecipe::getResult,
            ByteBufCodecs.BOOL,
            FramingSawRecipe::isDisabled,
            FramingSawRecipe::new
    );

    private final int materialAmount;
    private final List<FramingSawRecipeAdditive> additives;
    private final ItemStackTemplate result;
    private final Lazy<ItemStack> resultStack;
    private final IBlockType resultType;
    private final boolean disabled;

    public FramingSawRecipe(int materialAmount, List<FramingSawRecipeAdditive> additives, ItemStackTemplate result, boolean disabled)
    {
        this.materialAmount = materialAmount;
        this.additives = additives;
        this.result = result;
        this.resultStack = Lazy.of(result::create);
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
        if (!input.getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY).isEmptyOrContentsEmpty())
        {
            return FramingSawRecipeMatchResult.CAMO_PRESENT;
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
    public ItemStack assemble(RecipeInput container)
    {
        return result.create();
    }

    public int getMaterialAmount()
    {
        return materialAmount;
    }

    public List<FramingSawRecipeAdditive> getAdditives()
    {
        return additives;
    }

    public ItemStackTemplate getResult()
    {
        return result;
    }

    public ItemStack getResultStack()
    {
        return resultStack.get();
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
    public boolean showNotification()
    {
        return false;
    }

    @Override
    public String group()
    {
        return "";
    }

    @Override
    public List<RecipeDisplay> display()
    {
        return disabled ? List.of() : List.of(new FramingSawRecipeDisplay(
                materialAmount,
                additives.stream().map(FramingSawRecipeAdditive::toDisplay).toList(),
                new SlotDisplay.ItemStackSlotDisplay(result)
        ));
    }

    @Override
    public PlacementInfo placementInfo()
    {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory()
    {
        return FBContent.RECIPE_BOOK_CATEGORY_FRAMING_SAW.value();
    }

    @Override
    public RecipeSerializer<FramingSawRecipe> getSerializer()
    {
        return FBContent.RECIPE_SERIALIZER_FRAMING_SAW_RECIPE.value();
    }

    @Override
    public RecipeType<FramingSawRecipe> getType()
    {
        return FBContent.RECIPE_TYPE_FRAMING_SAW_RECIPE.value();
    }

    private static IBlockType findResultType(ItemStackTemplate result)
    {
        if (!(result.item().value() instanceof BlockItem item))
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
