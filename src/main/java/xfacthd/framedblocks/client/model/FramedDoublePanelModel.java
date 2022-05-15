package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import xfacthd.framedblocks.common.blockentity.FramedDoublePanelBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoublePanelModel extends FramedDoubleBlockModel
{
    private final Direction facing;

    public FramedDoublePanelModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, false);
        this.facing = state.getValue(PropertyHolder.FACING_NE);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedDoublePanelBlockEntity.getBlockPair(facing);
    }
}