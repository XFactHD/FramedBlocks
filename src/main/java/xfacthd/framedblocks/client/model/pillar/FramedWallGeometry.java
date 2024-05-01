package xfacthd.framedblocks.client.model.pillar;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;
import org.joml.Vector4f;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

public class FramedWallGeometry extends Geometry
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

    public FramedWallGeometry(GeometryFactory.Context ctx)
    {
        this.center = ctx.state().getValue(BlockStateProperties.UP);
        this.north = ctx.state().getValue(BlockStateProperties.NORTH_WALL);
        this.east =  ctx.state().getValue(BlockStateProperties.EAST_WALL);
        this.south = ctx.state().getValue(BlockStateProperties.SOUTH_WALL);
        this.west =  ctx.state().getValue(BlockStateProperties.WEST_WALL);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
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

    private void buildWallHalfSegment(QuadMap quadMap, BakedQuad quad, Direction dir, WallSide height)
    {
        if (height != WallSide.NONE)
        {
            Direction quadDir = quad.getDirection();
            if (Utils.isY(quadDir))
            {
                Vector4f rect = rects[dir.ordinal() - 2 + (center ? 4 : 0)];
                boolean inset = height != WallSide.TALL && quadDir != Direction.DOWN;
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(rect.x(), rect.y(), rect.z(), rect.w()))
                        .applyIf(Modifiers.setPosition(LOW_HEIGHT), inset)
                        .export(quadMap.get(inset ? null : quadDir));
            }
            else if (quadDir.getAxis() != dir.getAxis())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), center ? LARGE_MIN : SMALL_MIN))
                        .applyIf(Modifiers.cutSideUpDown(false, LOW_HEIGHT), height != WallSide.TALL)
                        .apply(Modifiers.setPosition(SMALL_MAX))
                        .export(quadMap.get(null));
            }
        }
    }

    private static void buildWallEndCap(QuadMap quadMap, BakedQuad quad, Direction dir, WallSide height)
    {
        if (quad.getDirection() == dir && height != WallSide.NONE)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(SMALL_MIN, 0, SMALL_MAX, height == WallSide.TALL ? 1F : LOW_HEIGHT))
                    .export(quadMap.get(dir));
        }
    }

    private void buildCenterPillar(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (center)
        {
            if (Utils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(LARGE_MIN, LARGE_MIN, LARGE_MAX, LARGE_MAX))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.of(quad)
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
                    QuadModifier.of(quad)
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
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(SMALL_MIN, 0, SMALL_MAX, tall ? 1 : LOW_HEIGHT))
                    .apply(Modifiers.setPosition(SMALL_MAX))
                    .export(quadList);
        }
        else if (tall && height == WallSide.LOW)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(SMALL_MIN, LOW_HEIGHT, SMALL_MAX, 1))
                    .apply(Modifiers.setPosition(SMALL_MAX))
                    .export(quadList);
        }
    }
}