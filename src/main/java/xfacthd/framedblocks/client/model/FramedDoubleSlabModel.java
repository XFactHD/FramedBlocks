package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Tuple;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoubleSlabModel extends FramedDoubleBlockModel
{
    @SuppressWarnings("unused")
    public FramedDoubleSlabModel(BlockState state, BakedModel baseModel) { super(baseModel, false); }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        BlockState slabState = FBContent.blockFramedSlab.get().defaultBlockState();
        return new Tuple<>(slabState.setValue(PropertyHolder.TOP, false), slabState.setValue(PropertyHolder.TOP, true));
    }
}