package io.github.xfacthd.framedblocks.common.compat.jei.camo;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Locale;
import java.util.function.IntFunction;

public enum DummyIngredientType implements StringRepresentable {
    EMPTY,
    CAMO_EXAMPLES,
    EMPTY_FRAMES,
    EMPTY_DOUBLE_FRAMES,
    ;

    private static final DummyIngredientType[] TYPES = values();
    private static final IntFunction<DummyIngredientType> BY_ID = ByIdMap.continuous(DummyIngredientType::ordinal, TYPES, ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final Codec<DummyIngredientType> CODEC = StringRepresentable.fromEnum(DummyIngredientType::values);
    public static final StreamCodec<ByteBuf, DummyIngredientType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, DummyIngredientType::ordinal);

    private final String name = toString().toLowerCase(Locale.ROOT);

    public Ingredient toIngredient() {
        return new JeiCamoApplicationDummyIngredient(this).toVanilla();
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
