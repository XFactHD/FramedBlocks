package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;

public class FramedDoubleCornerModel extends FramedDoubleBlockModel
{
    private final BlockState state;

    public FramedDoubleCornerModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel, true);
        this.state = state;
    }

    public FramedDoubleCornerModel(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedDoubleCorner.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, Direction.WEST),
                baseModel
        );
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        Direction facing = state.getValue(PropertyHolder.FACING_HOR);

        BlockState stateOne = FBContent.blockFramedInnerCornerSlope.get().defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, type)
                .setValue(PropertyHolder.FACING_HOR, type.isHorizontal() ? facing : facing.getCounterClockWise()); //FIXME: remove rotation when fixing inner corner rotation
        BlockState stateTwo = FBContent.blockFramedCornerSlope.get().defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, type.verticalOpposite())
                .setValue(PropertyHolder.FACING_HOR, facing.getOpposite());

        return new Tuple<>(stateOne, stateTwo);
    }
}