package xfacthd.framedblocks.client.model.v2;

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
        super(baseModel);
        this.state = state;
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        Direction facing = state.get(PropertyHolder.FACING_NE);
        BlockState panelState = FBContent.blockFramedPanel.getDefaultState();

        return new Tuple<>(
                panelState.with(PropertyHolder.FACING_HOR, facing),
                panelState.with(PropertyHolder.FACING_HOR, facing.getOpposite())
        );
    }
}