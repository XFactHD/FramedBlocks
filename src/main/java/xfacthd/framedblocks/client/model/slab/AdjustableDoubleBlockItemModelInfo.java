package xfacthd.framedblocks.client.model.slab;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.client.model.DoubleBlockItemModelInfo;
import xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCopycatBlockEntity;

public final class AdjustableDoubleBlockItemModelInfo extends DoubleBlockItemModelInfo
{
    public static final AdjustableDoubleBlockItemModelInfo STANDARD = new AdjustableDoubleBlockItemModelInfo(
            FramedCollapsibleBlockEntity.OFFSETS,
            FramedAdjustableDoubleBlockEntity::getPackedOffsetsStandard
    );
    public static final AdjustableDoubleBlockItemModelInfo COPYCAT = new AdjustableDoubleBlockItemModelInfo(
            FramedCollapsibleCopycatBlockEntity.OFFSETS,
            FramedAdjustableDoubleBlockEntity::getPackedOffsetsCopycat
    );

    private final ModelProperty<Integer> offsetProperty;
    private final FramedAdjustableDoubleBlockEntity.OffsetPacker offsetPacker;

    private AdjustableDoubleBlockItemModelInfo(
            ModelProperty<Integer> offsetProperty,
            FramedAdjustableDoubleBlockEntity.OffsetPacker offsetPacker
    )
    {
        this.offsetProperty = offsetProperty;
        this.offsetPacker = offsetPacker;
    }

    @Override
    public boolean isDataRequired()
    {
        return true;
    }

    @Override
    protected void appendItemModelPartData(ModelData.Builder builder, BlockState state, boolean second)
    {
        int offset = offsetPacker.pack(state, FramedAdjustableDoubleBlockEntity.CENTER_PART_HEIGHT, second);
        builder.with(offsetProperty, offset);
    }
}
