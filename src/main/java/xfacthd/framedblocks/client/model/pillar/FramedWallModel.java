package xfacthd.framedblocks.client.model.pillar;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;
import org.joml.Vector4f;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

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
    private static final float LOW_HEIGHT = 14F/16F;
    private static final float SMALL_MIN = 5F/16F;
    private static final float SMALL_MAX = 11F/16F;
    private static final float LARGE_MIN = 4F/16F;
    private static final float LARGE_MAX = 12F/16F;

    private final boolean center;
    private final WallSide north;
    private final WallSide east;
    private final WallSide south;
    private final WallSide west;

    public FramedWallModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        center = state.getValue(BlockStateProperties.UP);
        north = state.getValue(BlockStateProperties.NORTH_WALL);
        east =  state.getValue(BlockStateProperties.EAST_WALL);
        south = state.getValue(BlockStateProperties.SOUTH_WALL);
        west =  state.getValue(BlockStateProperties.WEST_WALL);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (north != WallSide.NONE)
        {
            buildWallHalfSegment(quadMap, quad, Direction.NORTH, north);
        }
        if (south != WallSide.NONE)
        {
            buildWallHalfSegment(quadMap, quad, Direction.SOUTH, south);
        }

        if (east != WallSide.NONE)
        {
            buildWallHalfSegment(quadMap, quad, Direction.EAST, east);
        }
        if (west != WallSide.NONE)
        {
            buildWallHalfSegment(quadMap, quad, Direction.WEST, west);
        }

        buildWallEndCap(quadMap, quad, Direction.NORTH, north);
        buildWallEndCap(quadMap, quad, Direction.EAST, east);
        buildWallEndCap(quadMap, quad, Direction.SOUTH, south);
        buildWallEndCap(quadMap, quad, Direction.WEST, west);

        buildCenterPillar(quadMap, quad);
    }

    private void buildWallHalfSegment(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, WallSide height)
    {
        if (height != WallSide.NONE)
        {
            Direction quadDir = quad.getDirection();
            if (Utils.isY(quadDir))
            {
                Vector4f rect = rects[dir.ordinal() - 2 + (center ? 4 : 0)];
                boolean inset = height != WallSide.TALL && quadDir != Direction.DOWN;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(rect.x(), rect.y(), rect.z(), rect.w()))
                        .applyIf(Modifiers.setPosition(LOW_HEIGHT), inset)
                        .export(quadMap.get(inset ? null : quadDir));
            }
            else if (quadDir.getAxis() != dir.getAxis())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), center ? LARGE_MIN : SMALL_MIN))
                        .applyIf(Modifiers.cutSideUpDown(false, LOW_HEIGHT), height != WallSide.TALL)
                        .apply(Modifiers.setPosition(SMALL_MAX))
                        .export(quadMap.get(null));
            }
        }
    }

    private static void buildWallEndCap(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, WallSide height)
    {
        if (quad.getDirection() == dir && height != WallSide.NONE)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(SMALL_MIN, 0, SMALL_MAX, height == WallSide.TALL ? 1F : LOW_HEIGHT))
                    .export(quadMap.get(dir));
        }
    }

    private void buildCenterPillar(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (center)
        {
            if (Utils.isY(quadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(LARGE_MIN, LARGE_MIN, LARGE_MAX, LARGE_MAX))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSide(LARGE_MIN, 0, LARGE_MAX, 1))
                        .apply(Modifiers.setPosition(LARGE_MAX))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            boolean tall = north == WallSide.TALL || east == WallSide.TALL || south == WallSide.TALL || west == WallSide.TALL;

            switch (quadDir)
            {
                case UP, DOWN ->
                {
                    boolean inset = !tall && quadDir == Direction.UP;
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutTopBottom(SMALL_MIN, SMALL_MIN, SMALL_MAX, SMALL_MAX))
                            .applyIf(Modifiers.setPosition(LOW_HEIGHT), inset)
                            .export(quadMap.get(inset ? null : quadDir));
                }
                case NORTH -> buildSmallCenterSide(quadMap.get(null), quad, north, tall);
                case EAST -> buildSmallCenterSide(quadMap.get(null), quad, east , tall);
                case SOUTH -> buildSmallCenterSide(quadMap.get(null), quad, south, tall);
                case WEST -> buildSmallCenterSide(quadMap.get(null), quad, west , tall);
            }
        }
    }

    private static void buildSmallCenterSide(List<BakedQuad> quadList, BakedQuad quad, WallSide height, boolean tall)
    {
        if (height == WallSide.NONE)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(SMALL_MIN, 0, SMALL_MAX, tall ? 1 : LOW_HEIGHT))
                    .apply(Modifiers.setPosition(SMALL_MAX))
                    .export(quadList);
        }
        else if (tall && height == WallSide.LOW)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(SMALL_MIN, LOW_HEIGHT, SMALL_MAX, 1))
                    .apply(Modifiers.setPosition(SMALL_MAX))
                    .export(quadList);
        }
    }
}