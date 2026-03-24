package io.github.xfacthd.framedblocks.common.crafting.rotation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public final class ShapeRotationRecipe extends ShapelessRecipe
{
    public static final MapCodec<ShapeRotationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
            CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
            Ingredient.CODEC.fieldOf("tool").forGetter(recipe -> recipe.tool),
            Ingredient.CODEC.fieldOf("block").forGetter(recipe -> recipe.block)
    ).apply(inst, ShapeRotationRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapeRotationRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
            o -> o.bookInfo,
            ItemStackTemplate.STREAM_CODEC,
            recipe -> recipe.result,
            Ingredient.CONTENTS_STREAM_CODEC,
            recipe -> recipe.tool,
            Ingredient.CONTENTS_STREAM_CODEC,
            recipe -> recipe.block,
            ShapeRotationRecipe::new
    );

    private final Ingredient tool;
    private final Ingredient block;

    public ShapeRotationRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ItemStackTemplate result, Ingredient tool, Ingredient block)
    {
        super(commonInfo, bookInfo, result, List.of(tool, block));
        this.tool = tool;
        this.block = block;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input)
    {
        NonNullList<ItemStack> remainders = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++)
        {
            ItemStack stack = input.getItem(i);
            if (tool.test(stack))
            {
                remainders.set(i, stack.copyWithCount(1));
            }
        }
        return remainders;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public RecipeSerializer<ShapelessRecipe> getSerializer()
    {
        return (RecipeSerializer<ShapelessRecipe>)(RecipeSerializer) FBContent.RECIPE_SERIALIZER_SHAPE_ROTATION.value();
    }
}
