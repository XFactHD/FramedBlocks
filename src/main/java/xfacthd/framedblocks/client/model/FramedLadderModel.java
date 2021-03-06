package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedLadderModel extends FramedBlockModel
{
    private static final float RUNG_DEPTH = 1F/16F;
    private static final float RUNG_OFFSET = .5F/16F;
    private static final float[] RUNGS = new float[] {
            1.5F/16F,
            5.5F/16F,
            9.5F/16F,
            13.5F/16F
    };

    private final Direction dir;

    public FramedLadderModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(PropertyHolder.FACING_HOR);
    }

    public FramedLadderModel(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedLadder.get().getDefaultState().with(PropertyHolder.FACING_HOR, Direction.SOUTH),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getFace() == Direction.UP || quad.getFace() == Direction.DOWN)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), RUNG_DEPTH * 2F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateY(), RUNG_DEPTH * 2F)
            )
            {
                quadMap.get(quad.getFace()).add(topBotQuad);
            }

            topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), RUNG_DEPTH * 2F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateYCCW(), RUNG_DEPTH * 2F)
            )
            {
                quadMap.get(quad.getFace()).add(topBotQuad);
            }

            for (int i = 0; i < 4; i++)
            {
                topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, 1F - RUNG_OFFSET) &&
                    BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), RUNG_DEPTH + RUNG_OFFSET)
                )
                {
                    float height = quad.getFace() == Direction.DOWN ? 1F - RUNGS[i] : RUNGS[i] + RUNG_DEPTH;
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, height);
                    quadMap.get(null).add(topBotQuad);
                }
            }
        }
        else if (quad.getFace() == dir.rotateY() || quad.getFace() == dir.rotateYCCW())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), RUNG_DEPTH * 2F))
            {
                quadMap.get(quad.getFace()).add(sideQuad);

                sideQuad = ModelUtils.duplicateQuad(sideQuad);
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, RUNG_DEPTH * 2F);
                quadMap.get(null).add(sideQuad);
            }
        }
        else if (quad.getFace() == dir || quad.getFace() == dir.getOpposite())
        {
            boolean opposite = quad.getFace() == dir.getOpposite();

            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.rotateY(), RUNG_DEPTH * 2F))
            {
                if (opposite)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, RUNG_DEPTH * 2F);
                    quadMap.get(null).add(sideQuad);
                }
                else
                {
                    quadMap.get(quad.getFace()).add(sideQuad);
                }
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.rotateYCCW(), RUNG_DEPTH * 2F))
            {
                if (opposite)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, RUNG_DEPTH * 2F);
                    quadMap.get(null).add(sideQuad);
                }
                else
                {
                    quadMap.get(quad.getFace()).add(sideQuad);
                }
            }

            for (int i = 0; i < 4; i++)
            {
                sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, false, RUNGS[i] + RUNG_DEPTH) &&
                    BakedQuadTransformer.createHorizontalSideQuad(sideQuad, true, 1F - RUNGS[i])
                )
                {
                    float pos = quad.getFace() == dir ? 1F - RUNG_OFFSET : RUNG_DEPTH + RUNG_OFFSET;
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, pos);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }
    }
}