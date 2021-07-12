package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedFenceGateModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean inWall;
    private final boolean open;

    public FramedFenceGateModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(BlockStateProperties.HORIZONTAL_FACING);
        inWall = state.get(BlockStateProperties.IN_WALL);
        open = state.get(BlockStateProperties.OPEN);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        float yOff = inWall ? 3F/16F : 0F;
        if (quad.getFace().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateY(), 2F/16F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, 9F/16F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), 9F/16F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getFace() == Direction.UP ? 1F - yOff : 11F/16F + yOff);
                quadMap.get(inWall || quad.getFace() == Direction.DOWN ? null : quad.getFace()).add(topBotQuad);
            }

            topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateYCCW(), 2F/16F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, 9F/16F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), 9F/16F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getFace() == Direction.UP ? 1F - yOff : 11F/16F + yOff);
                quadMap.get(inWall || quad.getFace() == Direction.DOWN ? null : quad.getFace()).add(topBotQuad);
            }
        }
        else if (quad.getFace() == dir || quad.getFace() == dir.getOpposite())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.rotateY(), 2F/16F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, true, 11F/16F + yOff) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, false, 1F - yOff)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                quadMap.get(null).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.rotateYCCW(), 2F/16F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, true, 11F/16F + yOff) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, false, 1F - yOff)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }
        else if (quad.getFace() == dir.rotateY() || quad.getFace() == dir.rotateYCCW())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 7F/16F, 5F/16F - yOff, 9F/16F, 1F - yOff))
            {
                quadMap.get(quad.getFace()).add(sideQuad);

                sideQuad = ModelUtils.duplicateQuad(sideQuad);
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 2F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }

        if (open) { createGateOpen(quadMap, quad, yOff); }
        else { createGateClosed(quadMap.get(null), quad, yOff); }
    }

    private void createGateClosed(List<BakedQuad> quadList, BakedQuad quad, float yOff)
    {
        if (quad.getFace() == dir || quad.getFace() == dir.getOpposite())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 6F/16F, 6F/16F - yOff, 10F/16F, 15F/16F - yOff))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                quadList.add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, false, 9F/16F - yOff) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, true, 10F/16F + yOff)
            )
            {
                BakedQuad sideQuadDup = ModelUtils.duplicateQuad(sideQuad);
                if (BakedQuadTransformer.createVerticalSideQuad(sideQuadDup, dir.rotateY(), 6F/16F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuadDup, dir.rotateYCCW(), 14F/16F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuadDup, 9F/16F);
                    quadList.add(sideQuadDup);
                }

                if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.rotateYCCW(), 6F/16F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.rotateY(), 14F/16F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                    quadList.add(sideQuad);
                }
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, false, 15F/16F - yOff) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, true, 4F/16F + yOff)
            )
            {
                BakedQuad sideQuadDup = ModelUtils.duplicateQuad(sideQuad);
                if (BakedQuadTransformer.createVerticalSideQuad(sideQuadDup, dir.rotateY(), 6F/16F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuadDup, dir.rotateYCCW(), 14F/16F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuadDup, 9F/16F);
                    quadList.add(sideQuadDup);
                }

                if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.rotateYCCW(), 6F/16F) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.rotateY(), 14F/16F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                    quadList.add(sideQuad);
                }
            }
        }
        else if (quad.getFace().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, 9F/16F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), 9F/16F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateY(), 14F/16F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateYCCW(), 14F/16F)
            )
            {
                boolean up = quad.getFace() == Direction.UP;

                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, up ? 15F/16F - yOff : 10F/16F + yOff);
                quadList.add(topBotQuad);

                float height = up ? 9F / 16F - yOff : 4F / 16F + yOff;

                BakedQuad topBotQuadDup = ModelUtils.duplicateQuad(topBotQuad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuadDup, dir.rotateY(), 6F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuadDup, height);
                    quadList.add(topBotQuadDup);
                }

                topBotQuadDup = ModelUtils.duplicateQuad(topBotQuad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuadDup, dir.rotateYCCW(), 6F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuadDup, height);
                    quadList.add(topBotQuadDup);
                }
            }
        }
        else if (quad.getFace() == dir.rotateY() || quad.getFace() == dir.rotateYCCW())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 7F/16F, 9F/16F - yOff, 9F/16F, 12F/16F - yOff))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 10F/16F);
                quadList.add(sideQuad);
            }
        }
    }

    private void createGateOpen(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, float yOff)
    {
        if (quad.getFace() == dir.rotateY() || quad.getFace() == dir.rotateYCCW())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), 7F/16F) &&
                BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir, 15F/16F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, false, 15F/16F - yOff) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, true, 10F/16F + yOff)
            )
            {
                BakedQuad topSideQuad = ModelUtils.duplicateQuad(sideQuad);
                if (BakedQuadTransformer.createHorizontalSideQuad(topSideQuad, true, 4F/16F + yOff))
                {
                    quadMap.get(quad.getFace()).add(topSideQuad);

                    topSideQuad = ModelUtils.duplicateQuad(topSideQuad);
                    BakedQuadTransformer.setQuadPosInFacingDir(topSideQuad, 2F/16F);
                    quadMap.get(null).add(topSideQuad);
                }

                BakedQuad botSideQuad = ModelUtils.duplicateQuad(sideQuad);
                if (BakedQuadTransformer.createHorizontalSideQuad(botSideQuad, false, 9F/16F - yOff))
                {
                    quadMap.get(quad.getFace()).add(botSideQuad);

                    botSideQuad = ModelUtils.duplicateQuad(botSideQuad);
                    BakedQuadTransformer.setQuadPosInFacingDir(botSideQuad, 2F/16F);
                    quadMap.get(null).add(botSideQuad);
                }

                if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, false, 12F/16F - yOff) &&
                    BakedQuadTransformer.createHorizontalSideQuad(sideQuad, true, 7F/16F + yOff) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), 3F/16F)
                )
                {
                    quadMap.get(quad.getFace()).add(sideQuad);

                    sideQuad = ModelUtils.duplicateQuad(sideQuad);
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 2F/16F);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }
        else if (quad.getFace().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, 15F/16F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), 7F/16F)
            )
            {
                boolean up = quad.getFace() == Direction.UP;
                float heightOuter = up ? 15F/16F - yOff : 10F/16F + yOff;
                float heightInner = up ? 9F/16F - yOff : 4F/16F + yOff;

                BakedQuad leftTopBotQuad = ModelUtils.duplicateQuad(topBotQuad);
                if (BakedQuadTransformer.createTopBottomQuad(leftTopBotQuad, dir.rotateY(), 2F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(leftTopBotQuad, heightOuter);
                    quadMap.get(null).add(leftTopBotQuad);

                    leftTopBotQuad = ModelUtils.duplicateQuad(leftTopBotQuad);
                    if (BakedQuadTransformer.createTopBottomQuad(leftTopBotQuad, dir, 13F/16F))
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(leftTopBotQuad, heightInner);
                        quadMap.get(null).add(leftTopBotQuad);
                    }
                }

                BakedQuad rightTopBotQuad = ModelUtils.duplicateQuad(topBotQuad);
                if (BakedQuadTransformer.createTopBottomQuad(rightTopBotQuad, dir.rotateYCCW(), 2F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(rightTopBotQuad, heightOuter);
                    quadMap.get(null).add(rightTopBotQuad);

                    rightTopBotQuad = ModelUtils.duplicateQuad(rightTopBotQuad);
                    if (BakedQuadTransformer.createTopBottomQuad(rightTopBotQuad, dir, 13F/16F))
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(rightTopBotQuad, heightInner);
                        quadMap.get(null).add(rightTopBotQuad);
                    }
                }
            }
        }
        else if (quad.getFace() == dir)
        {
            BakedQuad frontQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(frontQuad, 0F, 6F/16F - yOff, 2F/16F, 15F/16F - yOff))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(frontQuad, 15F/16F);
                quadMap.get(null).add(frontQuad);
            }

            frontQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(frontQuad, 14F/16F, 6F/16F - yOff, 1F, 15F/16F - yOff))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(frontQuad, 15F/16F);
                quadMap.get(null).add(frontQuad);
            }
        }
        else if (quad.getFace() == dir.getOpposite())
        {
            BakedQuad backQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(backQuad, 0F, 9F/16F - yOff, 2F/16F, 12F/16F - yOff))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(backQuad, 3F/16F);
                quadMap.get(null).add(backQuad);
            }

            backQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(backQuad, 14F/16F, 9F/16F - yOff, 1F, 12F/16F - yOff))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(backQuad, 3F/16F);
                quadMap.get(null).add(backQuad);
            }
        }
    }
}