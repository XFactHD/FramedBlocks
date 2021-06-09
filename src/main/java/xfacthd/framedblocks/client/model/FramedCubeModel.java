package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedCubeModel extends FramedBlockModel
{
    public FramedCubeModel(BlockState state, IBakedModel baseModel) { super(state, baseModel); }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad) { }

    @Override
    protected IBakedModel getCamoModel(BlockState camoState)
    {
        if (camoState == FBContent.blockFramedCube.getDefaultState()) { return baseModel; }
        return super.getCamoModel(camoState);
    }
}