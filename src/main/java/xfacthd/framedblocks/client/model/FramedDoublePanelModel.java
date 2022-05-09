package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import xfacthd.framedblocks.common.blockentity.FramedDoublePanelBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoublePanelModel extends FramedDoubleBlockModel
{
    private final BlockState state;

    public FramedDoublePanelModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, false);
        this.state = state;
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        Direction facing = state.getValue(PropertyHolder.FACING_NE);
        return FramedDoublePanelBlockEntity.getBlockPair(facing);
    }
}