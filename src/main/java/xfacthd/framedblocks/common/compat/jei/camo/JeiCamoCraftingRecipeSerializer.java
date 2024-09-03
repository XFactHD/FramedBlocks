package xfacthd.framedblocks.common.compat.jei.camo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class JeiCamoCraftingRecipeSerializer implements RecipeSerializer<JeiCamoCraftingRecipe>
{
    private static final MapCodec<JeiCamoCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("frame_stacks").forGetter(JeiCamoCraftingRecipe::getFrameStacks),
            Ingredient.CODEC_NONEMPTY.fieldOf("copy_tool").forGetter(JeiCamoCraftingRecipe::getCopyTool),
            Ingredient.CODEC_NONEMPTY.fieldOf("first_ingredient").forGetter(JeiCamoCraftingRecipe::getFirstIngredient),
            Ingredient.CODEC.fieldOf("second_ingredient").forGetter(JeiCamoCraftingRecipe::getSecondIngredient),
            Codec.list(ItemStack.CODEC).fieldOf("results").forGetter(JeiCamoCraftingRecipe::getResults)
    ).apply(inst, JeiCamoCraftingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, JeiCamoCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoCraftingRecipe::getFrameStacks,
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoCraftingRecipe::getCopyTool,
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoCraftingRecipe::getFirstIngredient,
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoCraftingRecipe::getSecondIngredient,
            ItemStack.LIST_STREAM_CODEC,
            JeiCamoCraftingRecipe::getResults,
            JeiCamoCraftingRecipe::new
    );

    @Override
    public MapCodec<JeiCamoCraftingRecipe> codec()
    {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, JeiCamoCraftingRecipe> streamCodec()
    {
        return STREAM_CODEC;
    }
}
