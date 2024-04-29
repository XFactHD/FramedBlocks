package xfacthd.framedblocks.common.crafting;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;

public final class FramingSawRecipeSerializer implements RecipeSerializer<FramingSawRecipe>
{
    private static final MapCodec<FramingSawRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("material").forGetter(FramingSawRecipe::getMaterialAmount),
            FramingSawRecipeAdditive.CODEC.listOf().optionalFieldOf("additives").flatXmap(
                    FramingSawRecipeSerializer::verifyAndMapAdditivesDecode,
                    FramingSawRecipeSerializer::verifyAndMapAdditivesEncode
            ).forGetter(FramingSawRecipe::getAdditives),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(FramingSawRecipe::getResult),
            Codec.BOOL.optionalFieldOf("disabled").xmap(
                    opt -> opt.orElse(false), flag -> flag ? Optional.of(true) : Optional.empty()
            ).forGetter(FramingSawRecipe::isDisabled)
    ).apply(inst, FramingSawRecipe::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, FramingSawRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            FramingSawRecipe::getMaterialAmount,
            FramingSawRecipeAdditive.STREAM_CODEC.apply(ByteBufCodecs.list()),
            FramingSawRecipe::getAdditives,
            ItemStack.STREAM_CODEC,
            FramingSawRecipe::getResult,
            ByteBufCodecs.BOOL,
            FramingSawRecipe::isDisabled,
            FramingSawRecipe::new
    );

    @Override
    public MapCodec<FramingSawRecipe> codec()
    {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FramingSawRecipe> streamCodec()
    {
        return STREAM_CODEC;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static DataResult<List<FramingSawRecipeAdditive>> verifyAndMapAdditivesDecode(
            Optional<List<FramingSawRecipeAdditive>> additives
    )
    {
        if (additives.isPresent())
        {
            List<FramingSawRecipeAdditive> list = additives.get();
            if (list.size() > FramingSawRecipe.MAX_ADDITIVE_COUNT)
            {
                int count = list.size();
                return DataResult.error(() ->
                        "More than " + FramingSawRecipe.MAX_ADDITIVE_COUNT + " additives are not supported, found " + count
                );
            }
            return DataResult.success(list);
        }
        return DataResult.success(List.of());
    }

    private static DataResult<Optional<List<FramingSawRecipeAdditive>>> verifyAndMapAdditivesEncode(
            List<FramingSawRecipeAdditive> additives
    )
    {
        if (additives.isEmpty())
        {
            return DataResult.success(Optional.empty());
        }
        if (additives.size() > FramingSawRecipe.MAX_ADDITIVE_COUNT)
        {
            int count = additives.size();
            return DataResult.error(() ->
                    "More than " + FramingSawRecipe.MAX_ADDITIVE_COUNT + " additives are not supported, found " + count
            );
        }
        return DataResult.success(Optional.of(additives));
    }
}
