package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.Rotation;

public class FramedInverseDoubleSlopePanelModel extends FramedDoubleBlockModel
{
    private final Direction facing;
    private final Rotation rotation;

    public FramedInverseDoubleSlopePanelModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(PropertyHolder.FACING_HOR);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        BlockState defState = FBContent.blockFramedSlopePanel.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(PropertyHolder.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.ROTATION, rotation.isVertical() ? rotation.getOpposite() : rotation)
                        .setValue(PropertyHolder.FRONT, true),
                defState.setValue(PropertyHolder.FACING_HOR, facing)
                        .setValue(PropertyHolder.ROTATION, rotation)
                        .setValue(PropertyHolder.FRONT, true)
        );
    }



    public static BlockState itemSource() { return FBContent.blockFramedInverseDoubleSlopePanel.get().defaultBlockState(); }
}
