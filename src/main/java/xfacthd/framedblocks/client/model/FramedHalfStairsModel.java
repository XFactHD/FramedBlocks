package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedHalfStairsModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;
    private final boolean right;

    public FramedHalfStairsModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(PropertyHolder.FACING_HOR);
        top = state.get(PropertyHolder.TOP);
        right = state.get(PropertyHolder.RIGHT);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getFace();
        Direction vertCut = right ? dir.rotateYCCW() : dir.rotateY();

        if (face == dir)
        {
            BakedQuad copy = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(copy, vertCut, .5F))
            {
                quadMap.get(face).add(copy);
            }
        }
        else if (face == dir.getOpposite())
        {
            BakedQuad copy = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(copy, vertCut, .5F))
            {
                BakedQuad copyTwo = ModelUtils.duplicateQuad(copy);
                if (BakedQuadTransformer.createHorizontalSideQuad(copyTwo, !top, .5F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(copyTwo, .5F);
                    quadMap.get(null).add(copyTwo);
                }

                if (BakedQuadTransformer.createHorizontalSideQuad(copy, top, .5F))
                {
                    quadMap.get(face).add(copy);
                }
            }
        }
        else if (face.getAxis() != Direction.Axis.Y && face.getAxis() != dir.getAxis())
        {
            BakedQuad copy = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(copy, dir.getOpposite(), .5F))
            {
                if (face == vertCut)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(copy, .5F);
                    quadMap.get(null).add(copy);
                }
                else
                {
                    quadMap.get(face).add(copy);
                }
            }

            copy = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(copy, dir, .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(copy, top, .5F)
            )
            {
                if (face == vertCut)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(copy, .5F);
                    quadMap.get(null).add(copy);
                }
                else
                {
                    quadMap.get(face).add(copy);
                }
            }
        }
        else if ((face == Direction.UP && top) || (face == Direction.DOWN && !top))
        {
            BakedQuad copy = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(copy, vertCut, .5F))
            {
                quadMap.get(face).add(copy);
            }
        }
        else if ((face == Direction.UP && !top) || (face == Direction.DOWN && top))
        {
            BakedQuad copy = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(copy, vertCut, .5F))
            {
                BakedQuad copyTwo = ModelUtils.duplicateQuad(copy);
                if (BakedQuadTransformer.createTopBottomQuad(copyTwo, dir, .5F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(copyTwo, .5F);
                    quadMap.get(null).add(copyTwo);
                }

                if (BakedQuadTransformer.createTopBottomQuad(copy, dir.getOpposite(), .5F))
                {
                    quadMap.get(face).add(copy);
                }
            }
        }
    }
}
