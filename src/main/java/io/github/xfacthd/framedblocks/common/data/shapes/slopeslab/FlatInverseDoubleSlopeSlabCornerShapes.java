package io.github.xfacthd.framedblocks.common.data.shapes.slopeslab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.block.slopeslab.SlopeSlabShape;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class FlatInverseDoubleSlopeSlabCornerShapes implements ShapeGenerator {
    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, FlatSlopeSlabCornerShapes.SHAPES, FlatSlopeSlabCornerShapes.INNER_SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, FlatSlopeSlabCornerShapes.OCCLUSION_SHAPES, FlatSlopeSlabCornerShapes.INNER_OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<SlopeSlabShape> cache, ShapeCache<SlopeSlabShape> innerCache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape shapeBot = ShapeUtils.orUnoptimized(
                cache.get(SlopeSlabShape.BOTTOM_TOP_HALF),
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH, Direction.SOUTH, innerCache.get(SlopeSlabShape.TOP_BOTTOM_HALF)
                )
        );
        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                cache.get(SlopeSlabShape.TOP_BOTTOM_HALF),
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH, Direction.SOUTH, innerCache.get(SlopeSlabShape.BOTTOM_TOP_HALF)
                )
        );

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBot, shapeTop, Direction.NORTH);

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeContainer.of(map);
    }
}
