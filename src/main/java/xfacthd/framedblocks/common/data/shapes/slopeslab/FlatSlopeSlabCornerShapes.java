package xfacthd.framedblocks.common.data.shapes.slopeslab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.block.slopeslab.SlopeSlabShape;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class FlatSlopeSlabCornerShapes implements SplitShapeGenerator
{
    public static final ShapeCache<SlopeSlabShape> SHAPES = makeCache(SlopeSlabShapes.SHAPES, BooleanOp.AND);
    public static final ShapeCache<SlopeSlabShape> OCCLUSION_SHAPES = makeCache(SlopeSlabShapes.OCCLUSION_SHAPES, BooleanOp.AND);
    public static final ShapeCache<SlopeSlabShape> INNER_SHAPES = makeCache(SlopeSlabShapes.SHAPES, BooleanOp.OR);
    public static final ShapeCache<SlopeSlabShape> INNER_OCCLUSION_SHAPES = makeCache(SlopeSlabShapes.OCCLUSION_SHAPES, BooleanOp.OR);
    public static final FlatSlopeSlabCornerShapes OUTER = new FlatSlopeSlabCornerShapes(SHAPES, OCCLUSION_SHAPES);
    public static final FlatSlopeSlabCornerShapes INNER = new FlatSlopeSlabCornerShapes(INNER_SHAPES, INNER_OCCLUSION_SHAPES);

    private final ShapeCache<SlopeSlabShape> shapes;
    private final ShapeCache<SlopeSlabShape> occlusionShapes;

    private FlatSlopeSlabCornerShapes(ShapeCache<SlopeSlabShape> shapes, ShapeCache<SlopeSlabShape> occlusionShapes)
    {
        this.shapes = shapes;
        this.occlusionShapes = occlusionShapes;
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, shapes);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, occlusionShapes);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<SlopeSlabShape> cache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        int maskTop = 0b0100;
        int maskTopHalf = 0b1000;
        VoxelShape[] shapes = new VoxelShape[16];
        ShapeUtils.makeHorizontalRotations(cache.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF), Direction.NORTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(cache.get(SlopeSlabShape.BOTTOM_TOP_HALF), Direction.NORTH, shapes, maskTopHalf);
        ShapeUtils.makeHorizontalRotations(cache.get(SlopeSlabShape.TOP_BOTTOM_HALF), Direction.NORTH, shapes, maskTop);
        ShapeUtils.makeHorizontalRotations(cache.get(SlopeSlabShape.TOP_TOP_HALF), Direction.NORTH, shapes, maskTop | maskTopHalf);

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

    private static ShapeCache<SlopeSlabShape> makeCache(ShapeCache<SlopeSlabShape> cache, BooleanOp joinOp)
    {
        return ShapeCache.createEnum(SlopeSlabShape.class, map ->
        {
            VoxelShape shapeSlopeBottom = cache.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF);
            VoxelShape shapeSlopeTop = cache.get(SlopeSlabShape.TOP_BOTTOM_HALF);

            VoxelShape shapeBottomBottomHalf = Shapes.joinUnoptimized(
                    shapeSlopeBottom,
                    ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeBottom),
                    joinOp
            );
            map.put(SlopeSlabShape.BOTTOM_BOTTOM_HALF, shapeBottomBottomHalf);
            map.put(SlopeSlabShape.BOTTOM_TOP_HALF, shapeBottomBottomHalf.move(0, .5, 0));

            VoxelShape shapeTopBottomHalf = Shapes.joinUnoptimized(
                    shapeSlopeTop,
                    ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeTop),
                    joinOp
            );
            map.put(SlopeSlabShape.TOP_BOTTOM_HALF, shapeTopBottomHalf);
            map.put(SlopeSlabShape.TOP_TOP_HALF, shapeTopBottomHalf.move(0, .5, 0));
        });
    }
}
