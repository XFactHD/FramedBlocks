package io.github.xfacthd.framedblocks.common.data.shapes.stairs.vertical;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.shapes.slope.HalfSlopeShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class VerticalSlopedStairsShapes implements ShapeGenerator {
    public static final VerticalSlopedStairsShapes INSTANCE = new VerticalSlopedStairsShapes();
    private static final ShapeCache<ShapeKey> SHAPES = makeCache(HalfSlopeShapes.SHAPES);
    private static final ShapeCache<ShapeKey> OCCLUSION_SHAPES = makeCache(HalfSlopeShapes.OCCLUSION_SHAPES);

    private VerticalSlopedStairsShapes() { }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generateShapes(states, SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generateShapes(states, OCCLUSION_SHAPES);
    }

    private static ShapeContainer generateShapes(List<BlockState> states, ShapeCache<ShapeKey> shapeCache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            map.put(state, shapeCache.get(new ShapeKey(dir, rot)));
        }

        return ShapeContainer.of(map);
    }

    private static ShapeCache<ShapeKey> makeCache(ShapeCache<HalfSlopeShapes.ShapeKey> shapeCache) {
        return ShapeCache.create(map -> {
            VoxelShape panelShape = CommonShapes.PANEL.get(Direction.NORTH);

            VoxelShape shapeUp = ShapeUtils.orUnoptimized(
                    panelShape,
                    ShapeUtils.rotateShapeUnoptimizedAroundY(
                            Direction.NORTH,
                            Direction.EAST,
                            shapeCache.get(new HalfSlopeShapes.ShapeKey(false, true))
                    )
            );

            VoxelShape shapeDown = ShapeUtils.orUnoptimized(
                    panelShape,
                    ShapeUtils.rotateShapeUnoptimizedAroundY(
                            Direction.NORTH,
                            Direction.WEST,
                            shapeCache.get(new HalfSlopeShapes.ShapeKey(true, false))
                    )
            );

            VoxelShape shapeRight = ShapeUtils.orUnoptimized(
                    panelShape,
                    ShapeUtils.rotateShapeUnoptimizedAroundY(
                            Direction.NORTH,
                            Direction.WEST,
                            shapeCache.get(new HalfSlopeShapes.ShapeKey(false, false))
                    )
            );

            VoxelShape shapeLeft = ShapeUtils.orUnoptimized(
                    panelShape,
                    ShapeUtils.rotateShapeUnoptimizedAroundY(
                            Direction.NORTH,
                            Direction.EAST,
                            shapeCache.get(new HalfSlopeShapes.ShapeKey(true, true))
                    )
            );

            ShapeUtils.makeHorizontalRotations(shapeUp, Direction.NORTH, map, HorizontalRotation.UP, ShapeKey::new);
            ShapeUtils.makeHorizontalRotations(shapeDown, Direction.NORTH, map, HorizontalRotation.DOWN, ShapeKey::new);
            ShapeUtils.makeHorizontalRotations(shapeRight, Direction.NORTH, map, HorizontalRotation.RIGHT, ShapeKey::new);
            ShapeUtils.makeHorizontalRotations(shapeLeft, Direction.NORTH, map, HorizontalRotation.LEFT, ShapeKey::new);
        });
    }

    private record ShapeKey(Direction dir, HorizontalRotation rot) { }
}
