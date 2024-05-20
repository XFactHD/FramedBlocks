package xfacthd.framedblocks.common.compat.ae2;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

record EncodedFramingSawPattern(ItemStack input, List<ItemStack> additives, ItemStack output)
{
    public static final Codec<EncodedFramingSawPattern> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStack.CODEC.fieldOf("input").forGetter(EncodedFramingSawPattern::input),
            ItemStack.CODEC.listOf().fieldOf("additives").forGetter(EncodedFramingSawPattern::additives),
            ItemStack.CODEC.fieldOf("output").forGetter(EncodedFramingSawPattern::output)
    ).apply(inst, EncodedFramingSawPattern::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EncodedFramingSawPattern> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            EncodedFramingSawPattern::input,
            ItemStack.LIST_STREAM_CODEC,
            EncodedFramingSawPattern::additives,
            ItemStack.STREAM_CODEC,
            EncodedFramingSawPattern::output,
            EncodedFramingSawPattern::new
    );

    @Override
    @SuppressWarnings("deprecation")
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncodedFramingSawPattern that = (EncodedFramingSawPattern) o;
        return ItemStack.matches(input, that.input) &&
                ItemStack.listMatches(additives, that.additives) &&
                ItemStack.matches(output, that.output);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int hashCode()
    {
        int result = 1;
        result = 31 * result + ItemStack.hashItemAndComponents(input);
        result = 31 * result + ItemStack.hashStackList(additives);
        result = 31 * result + ItemStack.hashItemAndComponents(output);
        return result;
    }
}
