package xfacthd.framedblocks.common.compat.jei.camo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

public class JeiCamoCraftingRecipeSerializer implements RecipeSerializer<JeiCamoCraftingRecipe>
{
    private static final MapCodec<JeiCamoCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("frame_stacks").forGetter(JeiCamoCraftingRecipe::getFrameStacks),
            Ingredient.CODEC_NONEMPTY.fieldOf("copy_tool").forGetter(JeiCamoCraftingRecipe::getCopyTool),
            Ingredient.CODEC_NONEMPTY.fieldOf("first_ingredient").forGetter(JeiCamoCraftingRecipe::getFirstIngredient),
            Ingredient.CODEC.fieldOf("second_ingredient").forGetter(JeiCamoCraftingRecipe::getSecondIngredient),
            Codec.list(ItemStack.CODEC).fieldOf("results").forGetter(JeiCamoCraftingRecipe::getResults)
    ).apply(inst, JeiCamoCraftingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, JeiCamoCraftingRecipe> STREAM_CODEC = StreamCodec.of(JeiCamoCraftingRecipeSerializer::toNetwork, JeiCamoCraftingRecipeSerializer::fromNetwork);

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

    private static JeiCamoCraftingRecipe fromNetwork(RegistryFriendlyByteBuf buffer)
    {
        Ingredient frameStacks = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        Ingredient copyTool = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        Ingredient firstIngredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        Ingredient secondIngredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        List<ItemStack> results = ItemStack.LIST_STREAM_CODEC.decode(buffer);
        return new JeiCamoCraftingRecipe(frameStacks, copyTool, firstIngredient, secondIngredient, results);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, JeiCamoCraftingRecipe recipe)
    {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getFrameStacks());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getCopyTool());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getFirstIngredient());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getSecondIngredient());
        ItemStack.LIST_STREAM_CODEC.encode(buffer, recipe.getResults());
    }
}
