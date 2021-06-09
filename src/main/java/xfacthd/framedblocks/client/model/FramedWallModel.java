package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallHeight;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector4f;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedWallModel extends FramedBlockModel
{
    private static final Vector4f[] rects = new Vector4f[] { //Wall half segment top/bottom rects
            new Vector4f( 5F/16F,      0F, 11F/16F,  5F/16F), //North
            new Vector4f( 5F/16F, 11F/16F, 11F/16F,      1F), //South
            new Vector4f(     0F,  5F/16F,  5F/16F, 11F/16F), //West
            new Vector4f(11F/16F,  5F/16F,      1F, 11F/16F), //East
            new Vector4f( 5F/16F,      0F, 11F/16F,  4F/16F), //North, with center pillar
            new Vector4f( 5F/16F, 12F/16F, 11F/16F,      1F), //South, with center pillar
            new Vector4f(     0F,  5F/16F,  4F/16F, 11F/16F), //West, with center pillar
            new Vector4f(12F/16F,  5F/16F,      1F, 11F/16F)  //East, with center pillar
    };

    private final boolean center;
    private final WallHeight north;
    private final WallHeight east;
    private final WallHeight south;
    private final WallHeight west;

    public FramedWallModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        center = state.get(BlockStateProperties.UP);
        north = state.get(BlockStateProperties.WALL_HEIGHT_NORTH);
        east =  state.get(BlockStateProperties.WALL_HEIGHT_EAST);
        south = state.get(BlockStateProperties.WALL_HEIGHT_SOUTH);
        west =  state.get(BlockStateProperties.WALL_HEIGHT_WEST);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (north != WallHeight.NONE)
        {
            buildWallHalfSegment(quadMap, quad, Direction.NORTH, north);
        }
        if (south != WallHeight.NONE)
        {
            buildWallHalfSegment(quadMap, quad, Direction.SOUTH, south);
        }

        if (east != WallHeight.NONE)
        {
            buildWallHalfSegment(quadMap, quad, Direction.EAST, east);
        }
        if (west != WallHeight.NONE)
        {
            buildWallHalfSegment(quadMap, quad, Direction.WEST, west);
        }

        buildWallEndCap(quadMap, quad, Direction.NORTH, north);
        buildWallEndCap(quadMap, quad, Direction.EAST, east);
        buildWallEndCap(quadMap, quad, Direction.SOUTH, south);
        buildWallEndCap(quadMap, quad, Direction.WEST, west);

        buildCenterPillar(quadMap, quad);
    }

    private void buildWallHalfSegment(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, WallHeight height)
    {
        if (height != WallHeight.NONE)
        {
            if (quad.getFace().getAxis() == Direction.Axis.Y)
            {
                Vector4f rect = rects[dir.ordinal() - 2 + (center ? 4 : 0)];
                BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, rect.getX(), rect.getY(), rect.getZ(), rect.getW()))
                {
                    if (height == WallHeight.TALL || quad.getFace() == Direction.DOWN)
                    {
                        quadMap.get(quad.getFace()).add(topBotQuad);
                    }
                    else
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 14F / 16F);
                        quadMap.get(null).add(topBotQuad);
                    }
                }
            }
            else if (quad.getFace() == dir.rotateY() || quad.getFace() == dir.rotateYCCW())
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if ((height == WallHeight.TALL || BakedQuadTransformer.createHorizontalSideQuad(sideQuad, false, 14F/16F)) &&
                    BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), center ? 4F/16F : 5F/16F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 11F/16F);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }
    }

    private void buildWallEndCap(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, WallHeight height)
    {
        if (quad.getFace() == dir && height != WallHeight.NONE)
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 5F / 16F, 0F, 11F / 16F, height == WallHeight.TALL ? 1F : 14F / 16F))
            {
                quadMap.get(dir).add(sideQuad);
            }
        }
    }

    private void buildCenterPillar(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (center)
        {
            if (quad.getFace().getAxis() == Direction.Axis.Y)
            {
                BakedQuad pillarQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(pillarQuad, 4F/16F, 4F/16F, 12F/16F, 12F/16F))
                {
                    quadMap.get(quad.getFace()).add(pillarQuad);
                }
            }
            else
            {
                BakedQuad pillarQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(pillarQuad, 4F / 16F, 0F, 12F / 16F, 1F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(pillarQuad, 12F / 16F);
                    quadMap.get(null).add(pillarQuad);
                }
            }
        }
        else
        {
            boolean tall = north == WallHeight.TALL || east == WallHeight.TALL || south == WallHeight.TALL || west == WallHeight.TALL;

            if (quad.getFace().getAxis() == Direction.Axis.Y)
            {
                BakedQuad pillarQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(pillarQuad, 5F/16F, 5F/16F, 11F/16F, 11F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(pillarQuad, tall ? 1F : 14F/16F);
                    quadMap.get(tall ? quad.getFace() : null).add(pillarQuad);
                }
            }
            else if (quad.getFace() == Direction.NORTH) { buildSmallCenterSide(quadMap.get(null), quad, north, tall); }
            else if (quad.getFace() == Direction.EAST ) { buildSmallCenterSide(quadMap.get(null), quad, east , tall); }
            else if (quad.getFace() == Direction.SOUTH) { buildSmallCenterSide(quadMap.get(null), quad, south, tall); }
            else if (quad.getFace() == Direction.WEST ) { buildSmallCenterSide(quadMap.get(null), quad, west , tall); }
        }
    }

    private void buildSmallCenterSide(List<BakedQuad> quadList, BakedQuad quad, WallHeight height, boolean tall)
    {
        if (height == WallHeight.NONE)
        {
            BakedQuad pillarQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(pillarQuad, 5F / 16F, 0F, 11F / 16F, tall ? 1F : 14F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(pillarQuad, 11F / 16F);
                quadList.add(pillarQuad);
            }
        }
        else if (tall && height == WallHeight.LOW)
        {
            BakedQuad pillarQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(pillarQuad, 5F / 16F, 14F/16F, 11F / 16F, 1F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(pillarQuad, 11F / 16F);
                quadList.add(pillarQuad);
            }
        }
    }
}