package io.github.xfacthd.framedblocks.common.crafting.rotation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public final class ShapeRotationRecipe extends ShapelessRecipe
{
    public static final MapCodec<ShapeRotationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::group),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
            Ingredient.CODEC.fieldOf("tool").forGetter(recipe -> recipe.tool),
            Ingredient.CODEC.fieldOf("block").forGetter(recipe -> recipe.block)
    ).apply(inst, ShapeRotationRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapeRotationRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ShapelessRecipe::group,
            ItemStack.STREAM_CODEC,
            recipe -> recipe.result,
            Ingredient.CONTENTS_STREAM_CODEC,
            recipe -> recipe.tool,
            Ingredient.CONTENTS_STREAM_CODEC,
            recipe -> recipe.block,
            ShapeRotationRecipe::new
    );

    private final Ingredient tool;
    private final Ingredient block;

    public ShapeRotationRecipe(String group, ItemStack result, Ingredient tool, Ingredient block)
    {
        super(group, CraftingBookCategory.BUILDING, result, List.of(tool, block));
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
    @SuppressWarnings({ "unchecked", "rawtypes", "NullableProblems" })
    public RecipeSerializer<ShapelessRecipe> getSerializer()
    {
        return (RecipeSerializer<ShapelessRecipe>)(RecipeSerializer) FBContent.RECIPE_SERIALIZER_SHAPE_ROTATION.value();
    }
}
