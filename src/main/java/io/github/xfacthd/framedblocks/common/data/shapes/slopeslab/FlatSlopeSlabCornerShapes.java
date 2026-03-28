package io.github.xfacthd.framedblocks.common.data.shapes.slopeslab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.block.slopeslab.SlopeSlabShape;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class FlatSlopeSlabCornerShapes implements ShapeGenerator {
    public static final ShapeCache<SlopeSlabShape> SHAPES = makeCache(SlopeSlabShapes.SHAPES, BooleanOp.AND);
    public static final ShapeCache<SlopeSlabShape> OCCLUSION_SHAPES = makeCache(SlopeSlabShapes.OCCLUSION_SHAPES, BooleanOp.AND);
    public static final ShapeCache<SlopeSlabShape> INNER_SHAPES = makeCache(SlopeSlabShapes.SHAPES, BooleanOp.OR);
    public static final ShapeCache<SlopeSlabShape> INNER_OCCLUSION_SHAPES = makeCache(SlopeSlabShapes.OCCLUSION_SHAPES, BooleanOp.OR);
    public static final FlatSlopeSlabCornerShapes OUTER = new FlatSlopeSlabCornerShapes(SHAPES, OCCLUSION_SHAPES);
    public static final FlatSlopeSlabCornerShapes INNER = new FlatSlopeSlabCornerShapes(INNER_SHAPES, INNER_OCCLUSION_SHAPES);

    private final ShapeCache<SlopeSlabShape> shapes;
    private final ShapeCache<SlopeSlabShape> occlusionShapes;

    private FlatSlopeSlabCornerShapes(ShapeCache<SlopeSlabShape> shapes, ShapeCache<SlopeSlabShape> occlusionShapes) {
        this.shapes = shapes;
        this.occlusionShapes = occlusionShapes;
    }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, shapes);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, occlusionShapes);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<SlopeSlabShape> cache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        int maskTop = 0b0100;
        int maskTopHalf = 0b1000;
        VoxelShape[] shapes = new VoxelShape[16];
        ShapeUtils.makeHorizontalRotations(cache.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF), Direction.NORTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(cache.get(SlopeSlabShape.BOTTOM_TOP_HALF), Direction.NORTH, shapes, maskTopHalf);
        ShapeUtils.makeHorizontalRotations(cache.get(SlopeSlabShape.TOP_BOTTOM_HALF), Direction.NORTH, shapes, maskTop);
        ShapeUtils.makeHorizontalRotations(cache.get(SlopeSlabShape.TOP_TOP_HALF), Direction.NORTH, shapes, maskTop | maskTopHalf);

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int top = state.getValue(FramedProperties.TOP) ? maskTop : 0;
            int topHalf = state.getValue(PropertyHolder.TOP_HALF) ? maskTopHalf : 0;
            int idx = dir.get2DDataValue() | top | topHalf;
            map.put(state, shapes[idx]);
        }

        return ShapeContainer.of(map);
    }

    private static ShapeCache<SlopeSlabShape> makeCache(ShapeCache<SlopeSlabShape> cache, BooleanOp joinOp) {
        return ShapeCache.createEnum(SlopeSlabShape.class, map -> {
            VoxelShape shapeSlopeBottom = cache.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF);
            VoxelShape shapeSlopeTop = cache.get(SlopeSlabShape.TOP_BOTTOM_HALF);

            VoxelShape shapeBottomBottomHalf = Shapes.joinUnoptimized(
                    shapeSlopeBottom,
                    ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, shapeSlopeBottom),
                    joinOp
            );
            map.put(SlopeSlabShape.BOTTOM_BOTTOM_HALF, shapeBottomBottomHalf);
            map.put(SlopeSlabShape.BOTTOM_TOP_HALF, shapeBottomBottomHalf.move(0, .5, 0));

            VoxelShape shapeTopBottomHalf = Shapes.joinUnoptimized(
                    shapeSlopeTop,
                    ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, shapeSlopeTop),
                    joinOp
            );
            map.put(SlopeSlabShape.TOP_BOTTOM_HALF, shapeTopBottomHalf);
            map.put(SlopeSlabShape.TOP_TOP_HALF, shapeTopBottomHalf.move(0, .5, 0));
        });
    }
}
