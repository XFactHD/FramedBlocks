package io.github.xfacthd.framedblocks.client.model.geometry.slab;

import io.github.xfacthd.framedblocks.api.model.item.DoubleBlockItemModelInfo;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

public final class AdjustableDoubleBlockItemModelInfo extends DoubleBlockItemModelInfo {
    public static final AdjustableDoubleBlockItemModelInfo STANDARD = new AdjustableDoubleBlockItemModelInfo(
            FramedAdjustableDoubleBlockEntity::getPackedOffsetsStandard
    );
    public static final AdjustableDoubleBlockItemModelInfo COPYCAT = new AdjustableDoubleBlockItemModelInfo(
            FramedAdjustableDoubleBlockEntity::getPackedOffsetsCopycat
    );

    private final FramedAdjustableDoubleBlockEntity.OffsetPacker offsetPacker;

    private AdjustableDoubleBlockItemModelInfo(FramedAdjustableDoubleBlockEntity.OffsetPacker offsetPacker) {
        this.offsetPacker = offsetPacker;
    }

    @Override
    public boolean isDataRequired() {
        return true;
    }

    @Override
    protected void appendItemModelData(ModelData.Builder builder, BlockState state) {
        builder.with(PackedCollapsibleBlockOffsets.PROPERTY, offsetPacker.packDouble(state, FramedAdjustableDoubleBlockEntity.CENTER_PART_HEIGHT));
    }
}
