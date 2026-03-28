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

public final class InverseDoubleSlopeSlabShapes implements ShapeGenerator {
    public static final InverseDoubleSlopeSlabShapes INSTANCE = new InverseDoubleSlopeSlabShapes();
    private static final ShapeCache<Direction> SHAPES = makeCache(SlopeSlabShapes.SHAPES);
    private static final ShapeCache<Direction> OCCLUSION_SHAPES = makeCache(SlopeSlabShapes.OCCLUSION_SHAPES);

    private InverseDoubleSlopeSlabShapes() { }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<Direction> cache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            map.put(state, cache.get(dir));
        }

        return ShapeContainer.of(map);
    }

    private static ShapeCache<Direction> makeCache(ShapeCache<SlopeSlabShape> cache) {
        return ShapeCache.createEnum(Direction.class, map -> {
            VoxelShape shape = ShapeUtils.orUnoptimized(
                    cache.get(SlopeSlabShape.BOTTOM_TOP_HALF),
                    ShapeUtils.rotateShapeUnoptimizedAroundY(
                            Direction.NORTH, Direction.SOUTH, cache.get(SlopeSlabShape.TOP_BOTTOM_HALF)
                    )
            );
            ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
        });
    }
}
