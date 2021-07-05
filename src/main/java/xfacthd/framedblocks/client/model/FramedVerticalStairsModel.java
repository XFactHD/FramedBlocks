package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.StairsType;

import java.util.List;
import java.util.Map;

public class FramedVerticalStairsModel extends FramedBlockModel
{
    private final StairsType type;
    private final Direction dir;

    public FramedVerticalStairsModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        type = state.getValue(PropertyHolder.STAIRS_TYPE);
        dir = state.getValue(PropertyHolder.FACING_HOR);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (type == StairsType.VERTICAL && (quad.getDirection() == dir.getOpposite() || quad.getDirection() == dir.getClockWise()))
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, quad.getDirection() == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite(), .5F))
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, quad.getDirection() == dir.getOpposite() ? dir.getCounterClockWise() : dir, .5F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                quadMap.get(null).add(sideQuad);
            }
        }

        if ((quad.getDirection() == Direction.UP && !type.isTop()) || (quad.getDirection() == Direction.DOWN && !type.isBottom())
        )
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F))
            {
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }

            topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getClockWise(), .5F)
            )
            {
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }
        }

        if ((quad.getDirection() == dir.getOpposite() || quad.getDirection() == dir.getClockWise()) && type != StairsType.VERTICAL)
        {
            boolean opposite = quad.getDirection() == dir.getOpposite();

            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, opposite ? dir.getClockWise() : dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !type.isTop(), .5F)
            )
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, opposite ? dir.getCounterClockWise() : dir, .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !type.isTop(), .5F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                quadMap.get(null).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, opposite ? dir.getClockWise() : dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, type.isTop(), .5F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                quadMap.get(null).add(sideQuad);
            }
        }

        if ((quad.getDirection() == Direction.UP && type.isTop()) || (quad.getDirection() == Direction.DOWN && type.isBottom()))
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getClockWise(), .5F)
            )
            {
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }

            topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getCounterClockWise(), .5F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                quadMap.get(null).add(topBotQuad);
            }

            topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getClockWise(), .5F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                quadMap.get(null).add(topBotQuad);
            }
        }

        if ((quad.getDirection() == dir || quad.getDirection() == dir.getCounterClockWise()) && type != StairsType.VERTICAL)
        {
            boolean ccw = quad.getDirection() == dir.getCounterClockWise();

            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !type.isTop(), .5F))
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, type.isTop(), .5F) &&
                BakedQuadTransformer.createVerticalSideQuad(sideQuad, ccw ? dir.getOpposite() : dir.getClockWise(), .5F)
            )
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }
        }
    }
}