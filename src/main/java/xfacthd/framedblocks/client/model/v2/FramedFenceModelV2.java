package xfacthd.framedblocks.client.model.v2;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedFenceModelV2 extends FramedBlockModelV2
{
    private final boolean north;
    private final boolean east;
    private final boolean south;
    private final boolean west;

    public FramedFenceModelV2(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        north = state.get(BlockStateProperties.NORTH);
        east = state.get(BlockStateProperties.EAST);
        south = state.get(BlockStateProperties.SOUTH);
        west = state.get(BlockStateProperties.WEST);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getFace().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 6F/16F, 6F/16F, 10F/16F, 10F/16F))
            {
                quadMap.get(quad.getFace()).add(topBotQuad);
            }
        }
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, quad.getFace().rotateY(), 10F/16F) &&
                BakedQuadTransformer.createVerticalSideQuad(sideQuad, quad.getFace().rotateYCCW(), 10F/16F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 10F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }

        createFenceBars(quadMap, quad, Direction.NORTH, north);
        createFenceBars(quadMap, quad, Direction.EAST, east);
        createFenceBars(quadMap, quad, Direction.SOUTH, south);
        createFenceBars(quadMap, quad, Direction.WEST, west);
    }

    private void createFenceBars(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, boolean active)
    {
        if (active)
        {
            if (quad.getFace().getAxis() == Direction.Axis.Y)
            {
                BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), 6F/16F) &&
                    BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateY(), 9F/16F) &&
                    BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateYCCW(), 9F/16F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getFace() == Direction.UP ? 15F/16F : 4F/16F);
                    quadMap.get(null).add(topBotQuad);

                    topBotQuad = ModelUtils.duplicateQuad(topBotQuad);
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getFace() == Direction.UP ? 9F/16F : 10F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
            }
            else if (quad.getFace() == dir.rotateY() || quad.getFace() == dir.rotateYCCW())
            {
                boolean neg = dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(sideQuad, neg ? 0F : 10F/16F, 6F/16F, neg ? 6F/16F : 1F, 9F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                    quadMap.get(null).add(sideQuad);
                }

                sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(sideQuad, neg ? 0F : 10F/16F, 12F/16F, neg ? 6F/16F : 1F, 15F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                    quadMap.get(null).add(sideQuad);
                }
            }
            else if (quad.getFace() == dir)
            {
                BakedQuad frontQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(frontQuad, 7F/16F, 6F/16F, 9F/16F, 9F/16F))
                {
                    quadMap.get(quad.getFace()).add(frontQuad);
                }

                frontQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(frontQuad, 7F/16F, 12F/16F, 9F/16F, 15F/16F))
                {
                    quadMap.get(quad.getFace()).add(frontQuad);
                }
            }
        }
    }
}