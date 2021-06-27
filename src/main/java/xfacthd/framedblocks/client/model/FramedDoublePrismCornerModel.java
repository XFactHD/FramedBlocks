package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoublePrismCornerModel extends FramedDoubleBlockModel
{
    private final BlockState state;

    public FramedDoublePrismCornerModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel, true);
        this.state = state;
    }

    public FramedDoublePrismCornerModel(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedDoublePrismCorner.getDefaultState().with(PropertyHolder.FACING_HOR, Direction.WEST),
                baseModel
        );
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        Direction facing = state.get(PropertyHolder.FACING_HOR);
        boolean top = state.get(PropertyHolder.TOP);
        boolean offset = state.get(PropertyHolder.OFFSET);

        BlockState stateOne = FBContent.blockFramedInnerPrismCorner.getDefaultState()
                .with(PropertyHolder.TOP, top)
                .with(PropertyHolder.FACING_HOR, facing)
                .with(PropertyHolder.OFFSET, offset);
        BlockState stateTwo = FBContent.blockFramedPrismCorner.getDefaultState()
                .with(PropertyHolder.TOP, !top)
                .with(PropertyHolder.FACING_HOR, facing.getOpposite())
                .with(PropertyHolder.OFFSET, !offset);

        return new Tuple<>(stateOne, stateTwo);
    }
}