package xfacthd.framedblocks.client.model.cube;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedCubeModel extends FramedBlockModel
{
    public FramedCubeModel(BlockState state, BakedModel baseModel) { super(state, baseModel); }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad) { }

    @Override
    protected BakedModel getCamoModel(BlockState camoState)
    {
        if (camoState == FBContent.blockFramedCube.get().defaultBlockState()) { return baseModel; }
        return super.getCamoModel(camoState);
    }
}