package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedWallBoardModel extends FramedBlockModel
{
    private final Direction dir;

    public FramedWallBoardModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        BakedQuad copy = ModelUtils.duplicateQuad(quad);
        if (quad.getDirection() == dir.getOpposite())
        {
            BakedQuadTransformer.setQuadPosInFacingDir(copy, 1F/16F);
            quadMap.get(null).add(copy);
        }
        else if (Utils.isY(quad.getDirection()))
        {
            if (BakedQuadTransformer.createTopBottomQuad(copy, dir.getOpposite(), 1F/16F))
            {
                quadMap.get(quad.getDirection()).add(copy);
            }
        }
        else if (quad.getDirection() != dir)
        {
            if (BakedQuadTransformer.createVerticalSideQuad(copy, dir.getOpposite(), 1F/16F))
            {
                quadMap.get(quad.getDirection()).add(copy);
            }
        }
    }
}
