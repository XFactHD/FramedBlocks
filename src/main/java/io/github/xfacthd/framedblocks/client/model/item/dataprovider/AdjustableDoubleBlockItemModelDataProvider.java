package io.github.xfacthd.framedblocks.client.model.item.dataprovider;

import io.github.xfacthd.framedblocks.api.model.item.DoubleBlockItemModelDataProvider;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

public final class AdjustableDoubleBlockItemModelDataProvider extends DoubleBlockItemModelDataProvider {
    public static final AdjustableDoubleBlockItemModelDataProvider INSTANCE = new AdjustableDoubleBlockItemModelDataProvider();

    private AdjustableDoubleBlockItemModelDataProvider() { }

    @Override
    protected void appendItemModelData(ModelData.Builder builder, BlockState state) {
        builder.with(PackedCollapsibleBlockOffsets.PROPERTY, FramedAdjustableDoubleBlockEntity.packDoubleOffsets(state, FramedAdjustableDoubleBlockEntity.CENTER_PART_HEIGHT));
    }
}
