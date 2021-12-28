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
                FBContent.blockFramedDoubleCorner.get().getDefaultState().with(PropertyHolder.FACING_HOR, Direction.WEST),
                baseModel
        );
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);
        Direction facing = state.get(PropertyHolder.FACING_HOR);

        BlockState stateOne = FBContent.blockFramedInnerCornerSlope.get().getDefaultState()
                .with(PropertyHolder.CORNER_TYPE, type)
                .with(PropertyHolder.FACING_HOR, type.isHorizontal() ? facing : facing.rotateYCCW());
        BlockState stateTwo = FBContent.blockFramedCornerSlope.get().getDefaultState()
                .with(PropertyHolder.CORNER_TYPE, type.verticalOpposite())
                .with(PropertyHolder.FACING_HOR, facing.getOpposite());

        return new Tuple<>(stateOne, stateTwo);
    }
}