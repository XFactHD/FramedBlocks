package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.data.FramedBlockData;

public class FramedCubeModel extends FramedCubeBaseModel
{
    public FramedCubeModel(BlockState state, BakedModel baseModel) { super(state, baseModel); }

    @Override
    protected BlockState getNoCamoModelState(BlockState camoState, FramedBlockData fbData)
    {
        return super.getNoCamoModelState(this.state, fbData);
    }
}
