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
        type = state.get(PropertyHolder.STAIRS_TYPE);
        dir = state.get(PropertyHolder.FACING_HOR);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (type == StairsType.VERTICAL && (quad.getFace() == dir.getOpposite() || quad.getFace() == dir.rotateY()))
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, quad.getFace() == dir.getOpposite() ? dir.rotateY() : dir.getOpposite(), .5F))
            {
                quadMap.get(quad.getFace()).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, quad.getFace() == dir.getOpposite() ? dir.rotateYCCW() : dir, .5F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                quadMap.get(null).add(sideQuad);
            }
        }

        if ((quad.getFace() == Direction.UP && !type.isTop()) || (quad.getFace() == Direction.DOWN && !type.isBottom())
        )
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F))
            {
                quadMap.get(quad.getFace()).add(topBotQuad);
            }

            topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateY(), .5F)
            )
            {
                quadMap.get(quad.getFace()).add(topBotQuad);
            }
        }

        if ((quad.getFace() == dir.getOpposite() || quad.getFace() == dir.rotateY()) && type != StairsType.VERTICAL)
        {
            boolean opposite = quad.getFace() == dir.getOpposite();

            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, opposite ? dir.rotateY() : dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !type.isTop(), .5F)
            )
            {
                quadMap.get(quad.getFace()).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, opposite ? dir.rotateYCCW() : dir, .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !type.isTop(), .5F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                quadMap.get(null).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, opposite ? dir.rotateY() : dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, type.isTop(), .5F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                quadMap.get(null).add(sideQuad);
            }
        }

        if ((quad.getFace() == Direction.UP && type.isTop()) || (quad.getFace() == Direction.DOWN && type.isBottom()))
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateY(), .5F)
            )
            {
                quadMap.get(quad.getFace()).add(topBotQuad);
            }

            topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateYCCW(), .5F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                quadMap.get(null).add(topBotQuad);
            }

            topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateY(), .5F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                quadMap.get(null).add(topBotQuad);
            }
        }

        if ((quad.getFace() == dir || quad.getFace() == dir.rotateYCCW()) && type != StairsType.VERTICAL)
        {
            boolean ccw = quad.getFace() == dir.rotateYCCW();

            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !type.isTop(), .5F))
            {
                quadMap.get(quad.getFace()).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, type.isTop(), .5F) &&
                BakedQuadTransformer.createVerticalSideQuad(sideQuad, ccw ? dir.getOpposite() : dir.rotateY(), .5F)
            )
            {
                quadMap.get(quad.getFace()).add(sideQuad);
            }
        }
    }
}