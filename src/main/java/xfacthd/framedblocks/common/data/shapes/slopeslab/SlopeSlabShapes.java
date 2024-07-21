package xfacthd.framedblocks.common.data.shapes.slopeslab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.block.slopeslab.SlopeSlabShape;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

import java.util.function.Supplier;

public final class SlopeSlabShapes implements SplitShapeGenerator
{
    public static final ShapeCache<SlopeSlabShape> SHAPES = makeCache(() -> ShapeUtils.orUnoptimized(
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 8,  8)
    ));
    public static final ShapeCache<SlopeSlabShape> OCCLUSION_SHAPES = makeCache(() -> ShapeUtils.orUnoptimized(
            Block.box(0, 0, 0, 16,   .5, 16),
            Block.box(0, 0, 0, 16,    4, 15),
            Block.box(0, 0, 0, 16, 7.75,  8),
            Block.box(0, 6, 0, 16,    8, .5)
    ));

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, OCCLUSION_SHAPES);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<SlopeSlabShape> cache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottomBottomHalf = cache.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF);
        VoxelShape shapeBottomTopHalf = cache.get(SlopeSlabShape.BOTTOM_TOP_HALF);
        VoxelShape shapeTopBottomHalf = cache.get(SlopeSlabShape.TOP_BOTTOM_HALF);
        VoxelShape shapeTopTopHalf = cache.get(SlopeSlabShape.TOP_TOP_HALF);

        int maskTop = 0b0100;
        int maskTopHalf = 0b1000;
        VoxelShape[] shapes = new VoxelShape[16];
        ShapeUtils.makeHorizontalRotations(shapeBottomBottomHalf, Direction.NORTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(shapeBottomTopHalf, Direction.NORTH, shapes, maskTopHalf);
        ShapeUtils.makeHorizontalRotations(shapeTopBottomHalf, Direction.NORTH, shapes, maskTop);
        ShapeUtils.makeHorizontalRotations(shapeTopTopHalf, Direction.NORTH, shapes, maskTop | maskTopHalf);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int top = state.getValue(FramedProperties.TOP) ? maskTop : 0;
            int topHalf = state.getValue(PropertyHolder.TOP_HALF) ? maskTopHalf : 0;
            int idx = dir.get2DDataValue() | top | topHalf;
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }

    private static ShapeCache<SlopeSlabShape> makeCache(Supplier<VoxelShape> bottomShapeFactory)
    {
        return ShapeCache.createEnum(SlopeSlabShape.class, map ->
        {
            VoxelShape shapeBottom = bottomShapeFactory.get();
            map.put(SlopeSlabShape.BOTTOM_BOTTOM_HALF, shapeBottom);
            map.put(SlopeSlabShape.BOTTOM_TOP_HALF, shapeBottom.move(0, .5, 0));

            VoxelShape shapeTop = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, Direction.DOWN, shapeBottom);
            map.put(SlopeSlabShape.TOP_BOTTOM_HALF, shapeTop.move(0, -.5, 0));
            map.put(SlopeSlabShape.TOP_TOP_HALF, shapeTop);
        });
    }



    public record ShapeKey(boolean top, boolean topHalf) { }
}
