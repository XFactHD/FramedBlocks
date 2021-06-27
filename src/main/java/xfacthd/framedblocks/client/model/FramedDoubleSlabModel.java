package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Tuple;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoubleSlabModel extends FramedDoubleBlockModel
{
    @SuppressWarnings("unused")
    public FramedDoubleSlabModel(BlockState state, IBakedModel baseModel) { super(baseModel, false); }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        BlockState slabState = FBContent.blockFramedSlab.getDefaultState();
        return new Tuple<>(slabState.with(PropertyHolder.TOP, false), slabState.with(PropertyHolder.TOP, true));
    }
}