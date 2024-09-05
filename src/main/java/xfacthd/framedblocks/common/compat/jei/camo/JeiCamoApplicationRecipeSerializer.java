package xfacthd.framedblocks.common.compat.jei.camo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class JeiCamoApplicationRecipeSerializer implements RecipeSerializer<JeiCamoApplicationRecipe>
{
    public static final MapCodec<JeiCamoApplicationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("frame").forGetter(JeiCamoApplicationRecipe::getFrame),
            Ingredient.CODEC_NONEMPTY.fieldOf("copy_tool").forGetter(JeiCamoApplicationRecipe::getCopyTool),
            Ingredient.CODEC_NONEMPTY.fieldOf("camo_one").forGetter(JeiCamoApplicationRecipe::getCamoOne),
            Ingredient.CODEC.fieldOf("camo_two").forGetter(JeiCamoApplicationRecipe::getCamoTwo),
            Codec.list(ItemStack.CODEC).fieldOf("results").forGetter(JeiCamoApplicationRecipe::getResults)
    ).apply(inst, JeiCamoApplicationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, JeiCamoApplicationRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoApplicationRecipe::getFrame,
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoApplicationRecipe::getCopyTool,
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoApplicationRecipe::getCamoOne,
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoApplicationRecipe::getCamoTwo,
            ItemStack.LIST_STREAM_CODEC,
            JeiCamoApplicationRecipe::getResults,
            JeiCamoApplicationRecipe::new
    );

    @Override
    public MapCodec<JeiCamoApplicationRecipe> codec()
    {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, JeiCamoApplicationRecipe> streamCodec()
    {
        return STREAM_CODEC;
    }
}
