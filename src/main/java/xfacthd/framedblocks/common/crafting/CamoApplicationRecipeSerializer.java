package xfacthd.framedblocks.common.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.*;

public final class CamoApplicationRecipeSerializer implements RecipeSerializer<CamoApplicationRecipe>
{
    private static final MapCodec<CamoApplicationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CamoApplicationRecipe::category),
            Ingredient.CODEC_NONEMPTY.fieldOf("copy_tool").forGetter(CamoApplicationRecipe::getCopyTool)
    ).apply(inst, CamoApplicationRecipe::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, CamoApplicationRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC,
            CamoApplicationRecipe::category,
            Ingredient.CONTENTS_STREAM_CODEC,
            CamoApplicationRecipe::getCopyTool,
            CamoApplicationRecipe::new
    );

    @Override
    public MapCodec<CamoApplicationRecipe> codec()
    {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CamoApplicationRecipe> streamCodec()
    {
        return STREAM_CODEC;
    }
}
