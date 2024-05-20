package xfacthd.framedblocks.common.crafting;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.Optional;

public record FramingSawRecipeAdditive(Ingredient ingredient, int count, @Nullable TagKey<Item> srcTag)
{
    public static final Codec<FramingSawRecipeAdditive> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FramingSawRecipeAdditive::ingredient),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(FramingSawRecipeAdditive::count)
    ).apply(inst, FramingSawRecipeAdditive::of));
    public static final StreamCodec<RegistryFriendlyByteBuf, FramingSawRecipeAdditive> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            FramingSawRecipeAdditive::ingredient,
            ByteBufCodecs.VAR_INT,
            FramingSawRecipeAdditive::count,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC).map(
                    opt -> opt.map(ItemTags::create).orElse(null),
                    key -> Optional.ofNullable(key).map(TagKey::location)
            ),
            FramingSawRecipeAdditive::srcTag,
            FramingSawRecipeAdditive::new
    );

    public FramingSawRecipeAdditive
    {
        Preconditions.checkArgument(ingredient != null, "Additive ingredient must be non-null");
        Preconditions.checkArgument(count > 0, "Additive count must be greater than 0");
    }

    public boolean isTagBased()
    {
        return srcTag != null;
    }

    public static FramingSawRecipeAdditive of(Ingredient ingredient, int count)
    {
        TagKey<Item> srcTag = null;
        if (FramedUtils.getSingleIngredientValue(ingredient) instanceof Ingredient.TagValue value)
        {
            srcTag = value.tag();
        }
        return new FramingSawRecipeAdditive(ingredient, count, srcTag);
    }

    public static FramingSawRecipeAdditive of(TagKey<Item> tag)
    {
        return of(tag, 1);
    }

    public static FramingSawRecipeAdditive of(TagKey<Item> tag, int count)
    {
        return of(Ingredient.of(tag), count);
    }

    public static FramingSawRecipeAdditive of(ItemLike item)
    {
        return of(item, 1);
    }

    public static FramingSawRecipeAdditive of(ItemLike item, int count)
    {
        return of(Ingredient.of(item), count);
    }
}
