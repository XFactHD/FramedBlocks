package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedTrapDoorModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;
    private final boolean open;

    public FramedTrapDoorModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        open = state.getValue(BlockStateProperties.OPEN);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (open)
        {
            if (quad.getDirection() == dir)
            {
                BakedQuad frontQuad = ModelUtils.duplicateQuad(quad);
                BakedQuadTransformer.setQuadPosInFacingDir(frontQuad, 3F/16F);
                quadMap.get(null).add(frontQuad);
            }
            else if (quad.getDirection().getAxis() == Direction.Axis.Y)
            {
                BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, 3F/16F))
                {
                    quadMap.get(quad.getDirection()).add(topBotQuad);
                }
            }
            else
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                boolean facePositive = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
                if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, !facePositive, 3F/16F))
                {
                    quadMap.get(quad.getDirection()).add(sideQuad);
                }
            }
        }
        else
        {
            if ((top && quad.getDirection() == Direction.DOWN) || (!top && quad.getDirection() == Direction.UP))
            {
                BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 3F/16F);
                quadMap.get(null).add(topBotQuad);
            }
            else if (quad.getDirection().getAxis() != Direction.Axis.Y)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, 3F/16F))
                {
                    quadMap.get(quad.getDirection()).add(sideQuad);
                }
            }
        }
    }
}