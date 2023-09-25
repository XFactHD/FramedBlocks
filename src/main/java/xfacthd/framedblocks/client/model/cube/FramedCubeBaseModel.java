package xfacthd.framedblocks.client.model.cube;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;

public class FramedCubeBaseModel extends FramedBlockModel
{
    public FramedCubeBaseModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad) { }

    @Override
    protected boolean forceUngeneratedBaseModel()
    {
        return true;
    }
}
