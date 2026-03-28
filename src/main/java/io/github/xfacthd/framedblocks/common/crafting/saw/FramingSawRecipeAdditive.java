package io.github.xfacthd.framedblocks.common.crafting.saw;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.FramingSawRecipeBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public record FramingSawRecipeAdditive(Ingredient ingredient, int count, Optional<TagKey<Item>> srcTag) {
    public static final Codec<FramingSawRecipeAdditive> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(FramingSawRecipeAdditive::ingredient),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(FramingSawRecipeAdditive::count)
    ).apply(inst, FramingSawRecipeAdditive::of));
    public static final StreamCodec<RegistryFriendlyByteBuf, FramingSawRecipeAdditive> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            FramingSawRecipeAdditive::ingredient,
            ByteBufCodecs.VAR_INT,
            FramingSawRecipeAdditive::count,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC).map(
                    opt -> opt.map(ItemTags::create),
                    key -> key.map(TagKey::location)
            ),
            FramingSawRecipeAdditive::srcTag,
            FramingSawRecipeAdditive::new
    );

    public FramingSawRecipeAdditive {
        //noinspection ConstantValue
        Preconditions.checkArgument(ingredient != null, "Additive ingredient must be non-null");
        Preconditions.checkArgument(count > 0, "Additive count must be greater than 0");
    }

    public boolean isTagBased() {
        return srcTag.isPresent();
    }

    public FramingSawRecipeDisplay.AdditiveDisplay toDisplay() {
        return new FramingSawRecipeDisplay.AdditiveDisplay(ingredient.display(), count);
    }

    private static FramingSawRecipeAdditive of(Ingredient ingredient, int count) {
        TagKey<Item> srcTag = null;
        if (!ingredient.isCustom() && ingredient.getValues() instanceof HolderSet.Named<Item> named) {
            srcTag = named.key();
        }
        return new FramingSawRecipeAdditive(ingredient, count, Optional.ofNullable(srcTag));
    }

    public static FramingSawRecipeAdditive of(FramingSawRecipeBuilder.Additive additive) {
        return of(additive.ingredient(), additive.count());
    }
}
