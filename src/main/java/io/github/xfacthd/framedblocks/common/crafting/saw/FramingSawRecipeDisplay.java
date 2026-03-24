package io.github.xfacthd.framedblocks.common.crafting.saw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public record FramingSawRecipeDisplay(int materialAmount, List<AdditiveDisplay> additives, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay
{
    public static final MapCodec<FramingSawRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("material").forGetter(FramingSawRecipeDisplay::materialAmount),
            AdditiveDisplay.CODEC.sizeLimitedListOf(FramingSawRecipe.MAX_ADDITIVE_COUNT).optionalFieldOf("additives", List.of()).forGetter(FramingSawRecipeDisplay::additives),
            SlotDisplay.CODEC.fieldOf("result").forGetter(FramingSawRecipeDisplay::result),
            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(FramingSawRecipeDisplay::craftingStation)
    ).apply(inst, FramingSawRecipeDisplay::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FramingSawRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            FramingSawRecipeDisplay::materialAmount,
            AdditiveDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
            FramingSawRecipeDisplay::additives,
            SlotDisplay.STREAM_CODEC,
            FramingSawRecipeDisplay::result,
            SlotDisplay.STREAM_CODEC,
            FramingSawRecipeDisplay::craftingStation,
            FramingSawRecipeDisplay::new
    );

    public FramingSawRecipeDisplay(int materialAmount, List<AdditiveDisplay> additives, SlotDisplay result)
    {
        this(materialAmount, additives, result, new SlotDisplay.Composite(List.of(
                new SlotDisplay.ItemSlotDisplay(FBContent.BLOCK_FRAMING_SAW.value().asItem()),
                new SlotDisplay.ItemSlotDisplay(FBContent.BLOCK_POWERED_FRAMING_SAW.value().asItem())
        )));
    }

    @Override
    public Type<? extends RecipeDisplay> type()
    {
        return FBContent.RECIPE_DISPLAY_TYPE_FRAMING_SAW.value();
    }

    public record AdditiveDisplay(SlotDisplay ingredient, int count)
    {
        private static final Codec<AdditiveDisplay> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                SlotDisplay.CODEC.fieldOf("ingredient").forGetter(AdditiveDisplay::ingredient),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(AdditiveDisplay::count)
        ).apply(inst, AdditiveDisplay::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, AdditiveDisplay> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC,
                AdditiveDisplay::ingredient,
                ByteBufCodecs.VAR_INT,
                AdditiveDisplay::count,
                AdditiveDisplay::new
        );
    }
}
