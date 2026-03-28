package io.github.xfacthd.framedblocks.common.compat.jei.camo;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.Stream;

public record JeiCamoApplicationDummyIngredient(DummyIngredientType dummyType) implements ICustomIngredient {
    public static final MapCodec<JeiCamoApplicationDummyIngredient> CODEC = DummyIngredientType.CODEC
            .xmap(JeiCamoApplicationDummyIngredient::new, JeiCamoApplicationDummyIngredient::dummyType)
            .fieldOf("dummy_type");
    public static final StreamCodec<ByteBuf, JeiCamoApplicationDummyIngredient> STREAM_CODEC = DummyIngredientType.STREAM_CODEC
            .map(JeiCamoApplicationDummyIngredient::new, JeiCamoApplicationDummyIngredient::dummyType);

    @Override
    public boolean test(ItemStack stack) {
        return false;
    }

    @Override
    public Stream<Holder<Item>> items() {
        return Stream.empty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return FBContent.INGREDIENT_TYPE_JEI_CAMO_DUMMY.value();
    }

    @Override
    public SlotDisplay display() {
        return SlotDisplay.Empty.INSTANCE;
    }
}
