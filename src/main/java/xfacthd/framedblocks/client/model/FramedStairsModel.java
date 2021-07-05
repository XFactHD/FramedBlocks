package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedStairsModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;
    private final StairsShape shape;

    public FramedStairsModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if ((top && quad.getDirection() == Direction.DOWN) || (!top && quad.getDirection() == Direction.UP))
        {
            createCenterQuads(quadMap.get(null), quad);
            createTopBottomQuads(quadMap.get(quad.getDirection()), quad);
        }
        else if (quad.getDirection().getAxis() != Direction.Axis.Y)
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F))
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }

            createSideQuads(quadMap, quad);
        }
    }

    private void createCenterQuads(List<BakedQuad> quadList, BakedQuad quad)
    {
        if (shape == StairsShape.STRAIGHT || shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, .5F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                quadList.add(topBotQuad);
            }
        }

        if (shape != StairsShape.STRAIGHT)
        {
            boolean opposite = shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT;
            boolean left = shape == StairsShape.OUTER_LEFT || shape == StairsShape.INNER_LEFT;

            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, opposite ? dir.getOpposite() : dir, .5F) &&
                    BakedQuadTransformer.createTopBottomQuad(topBotQuad, left ? dir.getCounterClockWise() : dir.getClockWise(), .5F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                quadList.add(topBotQuad);
            }
        }
    }

    private void createTopBottomQuads(List<BakedQuad> quadList, BakedQuad quad)
    {
        if (shape == StairsShape.STRAIGHT || shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F))
            {
                quadList.add(topBotQuad);
            }
        }

        if (shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, shape == StairsShape.OUTER_LEFT ? dir.getClockWise() : dir.getCounterClockWise(), .5F)
            )
            {
                quadList.add(topBotQuad);
            }
        }
        else if (shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, shape == StairsShape.INNER_LEFT ? dir.getClockWise() : dir.getCounterClockWise(), .5F)
            )
            {
                quadList.add(topBotQuad);
            }
        }
    }

    private void createSideQuads(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection() == dir.getOpposite())
        {
            if (shape == StairsShape.STRAIGHT)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                    quadMap.get(null).add(sideQuad);
                }
            }
            else if (shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, shape == StairsShape.INNER_LEFT ? dir.getClockWise() : dir.getCounterClockWise(), .5F)
                )
                {
                    quadMap.get(quad.getDirection()).add(sideQuad);
                }

                sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, shape == StairsShape.INNER_LEFT ? dir.getCounterClockWise() : dir.getClockWise(), .5F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                    quadMap.get(null).add(sideQuad);
                }
            }
            else if (shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, shape == StairsShape.OUTER_LEFT ? dir.getClockWise() : dir.getCounterClockWise(), .5F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }
        else if (quad.getDirection() == dir.getClockWise())
        {
            if (shape == StairsShape.OUTER_RIGHT || shape == StairsShape.STRAIGHT)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), .5F)
                )
                {
                    quadMap.get(quad.getDirection()).add(sideQuad);
                }
            }
            else if (shape == StairsShape.INNER_LEFT)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), .5F)
                )
                {
                    quadMap.get(quad.getDirection()).add(sideQuad);
                }

                sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir, .5F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                    quadMap.get(null).add(sideQuad);
                }
            }
            else if (shape == StairsShape.OUTER_LEFT)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), .5F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }
        else if (quad.getDirection() == dir.getCounterClockWise())
        {
            if (shape == StairsShape.OUTER_LEFT || shape == StairsShape.STRAIGHT)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), .5F)
                )
                {
                    quadMap.get(quad.getDirection()).add(sideQuad);
                }
            }
            else if (shape == StairsShape.INNER_RIGHT)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), .5F)
                )
                {
                    quadMap.get(quad.getDirection()).add(sideQuad);
                }

                sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                        BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir, .5F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                    quadMap.get(null).add(sideQuad);
                }
            }
            else if (shape == StairsShape.OUTER_RIGHT)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, !top, .5F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), .5F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }
        else if (quad.getDirection() == dir && (shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT))
        {
            BakedQuad backQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(backQuad, !top, .5F) &&
                BakedQuadTransformer.createVerticalSideQuad(backQuad, shape == StairsShape.OUTER_LEFT ? dir.getClockWise() : dir.getCounterClockWise(), .5F)
            )
            {
                quadMap.get(quad.getDirection()).add(backQuad);
            }
        }
    }
}