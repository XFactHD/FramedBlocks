package xfacthd.framedblocks.client.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.blockentity.FramedVerticalDoubleHalfSlopeBlockEntity;

public class FramedVerticalDoubleHalfSlopeModel extends FramedDoubleBlockModel
{
    private final Direction facing;
    private final boolean top;

    public FramedVerticalDoubleHalfSlopeModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, false);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedVerticalDoubleHalfSlopeBlockEntity.getBlockPair(facing, top);
    }
}
