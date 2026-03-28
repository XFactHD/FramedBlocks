package io.github.xfacthd.framedblocks.common.data.shapes.prism;

import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.DirectionAxis;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.shapes.slope.SlopeShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class PrismShapes implements ShapeGenerator {
    public static final PrismShapes OUTER = new PrismShapes();

    private PrismShapes() { }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generateShapes(states, SlopeShapes.SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generateShapes(states, SlopeShapes.OCCLUSION_SHAPES);
    }

    private static ShapeContainer generateShapes(List<BlockState> states, ShapeCache<SlopeType> shapeCache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape shapeBottom = ShapeUtils.andUnoptimized(
                shapeCache.get(SlopeType.BOTTOM),
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.SOUTH, shapeCache.get(SlopeType.BOTTOM))
        );
        VoxelShape shapeTop = ShapeUtils.andUnoptimized(
                shapeCache.get(SlopeType.TOP),
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.SOUTH, shapeCache.get(SlopeType.TOP))
        );
        VoxelShape shapeXZ = ShapeUtils.andUnoptimized(
                shapeCache.get(SlopeType.BOTTOM),
                shapeCache.get(SlopeType.TOP)
        );
        VoxelShape shapeY = ShapeUtils.andUnoptimized(
                shapeCache.get(SlopeType.HORIZONTAL),
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.EAST, shapeCache.get(SlopeType.HORIZONTAL))
        );

        VoxelShape[] shapes = new VoxelShape[DirectionAxis.COUNT];
        for (DirectionAxis dirAxis : DirectionAxis.values()) {
            Direction facing = dirAxis.direction();
            Direction.Axis axis = dirAxis.axis();

            if (DirUtils.isY(facing)) {
                shapes[dirAxis.ordinal()] = ShapeUtils.rotateShapeAroundY(
                        Direction.EAST,
                        Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE),
                        facing == Direction.UP ? shapeBottom : shapeTop
                );
            } else {
                shapes[dirAxis.ordinal()] = ShapeUtils.rotateShapeAroundY(
                        Direction.SOUTH,
                        facing,
                        axis == Direction.Axis.Y ? shapeY : shapeXZ
                );
            }
        }

        for (BlockState state : states) {
            DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
            map.put(state, shapes[dirAxis.ordinal()]);
        }

        return ShapeContainer.of(map);
    }
}
