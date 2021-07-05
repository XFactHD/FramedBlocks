package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.SlopeType;

public class FramedDoubleSlopeModel extends FramedDoubleBlockModel
{
    private final BlockState state;

    public FramedDoubleSlopeModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel, false);
        this.state = state;
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = state.getValue(PropertyHolder.FACING_HOR);

        BlockState stateOne = FBContent.blockFramedSlope.get().defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, type)
                .setValue(PropertyHolder.FACING_HOR, facing);
        BlockState stateTwo = FBContent.blockFramedSlope.get().defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, type == SlopeType.HORIZONTAL ? type : type.getOpposite())
                .setValue(PropertyHolder.FACING_HOR, facing.getOpposite());

        return new Tuple<>(stateOne, stateTwo);
    }
}