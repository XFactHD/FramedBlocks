package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoublePanelModel extends FramedDoubleBlockModel
{
    private final BlockState state;

    public FramedDoublePanelModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel, false);
        this.state = state;
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        Direction facing = state.getValue(PropertyHolder.FACING_NE);
        BlockState panelState = FBContent.blockFramedPanel.get().defaultBlockState();

        return new Tuple<>(
                panelState.setValue(PropertyHolder.FACING_HOR, facing),
                panelState.setValue(PropertyHolder.FACING_HOR, facing.getOpposite())
        );
    }
}