package xfacthd.framedblocks.common.data.shapes.slopeslab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.block.slopeslab.SlopeSlabShape;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class FlatElevatedSlopeSlabCornerShapes implements SplitShapeGenerator
{
    private static final ShapeCache<ShapeKey> FINAL_SHAPES = makeCache(SlopeSlabShapes.SHAPES, BooleanOp.AND);
    private static final ShapeCache<ShapeKey> FINAL_OCCLUSION_SHAPES = makeCache(SlopeSlabShapes.OCCLUSION_SHAPES, BooleanOp.AND);
    private static final ShapeCache<ShapeKey> FINAL_INNER_SHAPES = makeCache(SlopeSlabShapes.SHAPES, BooleanOp.OR);
    private static final ShapeCache<ShapeKey> FINAL_INNER_OCCLUSION_SHAPES = makeCache(SlopeSlabShapes.OCCLUSION_SHAPES, BooleanOp.OR);
    public static final FlatElevatedSlopeSlabCornerShapes OUTER = new FlatElevatedSlopeSlabCornerShapes(FINAL_SHAPES, FINAL_OCCLUSION_SHAPES);
    public static final FlatElevatedSlopeSlabCornerShapes INNER = new FlatElevatedSlopeSlabCornerShapes(FINAL_INNER_SHAPES, FINAL_INNER_OCCLUSION_SHAPES);

    private final ShapeCache<ShapeKey> shapes;
    private final ShapeCache<ShapeKey> occlusionShapes;

    private FlatElevatedSlopeSlabCornerShapes(ShapeCache<ShapeKey> shapes, ShapeCache<ShapeKey> occlusionShapes)
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

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<ShapeKey> cache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, cache.get(new ShapeKey(dir, top)));
        }

        return ShapeProvider.of(builder.build());
    }

    private static ShapeCache<ShapeKey> makeCache(ShapeCache<SlopeSlabShape> cache, BooleanOp joinOp)
    {
        return ShapeCache.create(map ->
        {
            VoxelShape shapeSlopeBottom = cache.get(SlopeSlabShape.BOTTOM_TOP_HALF);
            VoxelShape shapeSlopeTop = cache.get(SlopeSlabShape.TOP_BOTTOM_HALF);

            VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                    Shapes.joinUnoptimized(
                            shapeSlopeBottom,
                            ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, shapeSlopeBottom),
                            joinOp
                    ),
                    Block.box(0, 0, 0, 16, 8, 16)
            );
            VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                    Shapes.joinUnoptimized(
                            shapeSlopeTop,
                            ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, shapeSlopeTop),
                            joinOp
                    ),
                    Block.box(0, 8, 0, 16, 16, 16)
            );

            ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH, map, ShapeKey::new);
        });
    }



    private record ShapeKey(Direction dir, boolean top) { }
}
