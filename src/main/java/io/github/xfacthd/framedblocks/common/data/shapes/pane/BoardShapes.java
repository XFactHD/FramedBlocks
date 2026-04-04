package io.github.xfacthd.framedblocks.common.data.shapes.pane;

import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public final class BoardShapes {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final ShapeCache<Direction> SINGLE_BOARD_CACHE = ShapeCache.createEnum(Direction.class, map -> {
        VoxelShape shape = Block.box(0, 0, 0, 16, 16, 1);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
        map.put(Direction.UP, ShapeUtils.rotateShapeAroundX(Direction.NORTH, Direction.UP, shape));
        map.put(Direction.DOWN, ShapeUtils.rotateShapeAroundX(Direction.NORTH, Direction.DOWN, shape));
    });

    public static ShapeContainer generate(List<BlockState> states) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape[] allShapes = new VoxelShape[(1 << 6) - 1];
        for (int i = 0; i < allShapes.length; i++) {
            VoxelShape merged = Shapes.empty();
            for (Direction dir : DIRECTIONS) {
                if (((i + 1) & (1 << dir.ordinal())) == 0) {
                    continue;
                }

                VoxelShape faceShape = SINGLE_BOARD_CACHE.get(dir);
                merged = ShapeUtils.orUnoptimized(merged, faceShape);
            }
            allShapes[i] = ShapeUtils.optimize(merged);
        }

        for (BlockState state : states) {
            int mask = state.getValue(PropertyHolder.FACES);
            map.put(state, allShapes[mask - 1]);
        }

        return ShapeContainer.of(map);
    }

    public static ShapeContainer generateDouble(List<BlockState> states) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
            map.put(state, SINGLE_BOARD_CACHE.get(cmpDir.direction()));
        }

        return ShapeContainer.of(map);
    }

    public static ShapeContainer generateHalf(List<BlockState> states) {
        VoxelShape baseShape = Block.box(0, 0, 0, 16, 8, 1);
        return generatePartial(states, baseShape, Direction.SOUTH, _ -> Direction.DOWN);
    }

    public static ShapeContainer generateCorner(List<BlockState> states) {
        VoxelShape baseShape = Block.box(0, 0, 0, 8, 8, 1);
        return generatePartial(states, baseShape, Direction.WEST, Direction::getCounterClockWise);
    }

    public static ShapeContainer generateInnerCorner(List<BlockState> states) {
        VoxelShape baseShape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 16, 8, 1),
                Block.box(0, 8, 0, 8, 16, 1)
        );
        return generatePartial(states, baseShape, Direction.WEST, Direction::getCounterClockWise);
    }

    private static ShapeContainer generatePartial(List<BlockState> states, VoxelShape baseShape, Direction downBaseDir, UnaryOperator<Direction> horBaseDir) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape[] shapes = new VoxelShape[CompoundDirection.COUNT];
        for (Direction face : DIRECTIONS) {
            VoxelShape shape = switch (face) {
                case UP, DOWN -> ShapeUtils.rotateShapeUnoptimizedAroundX(Direction.NORTH, face, baseShape);
                default -> ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, face, baseShape);
            };

            for (Direction dir : DIRECTIONS) {
                if (dir.getAxis() == face.getAxis()) {
                    continue;
                }

                CompoundDirection cmpDir = CompoundDirection.of(face, dir);
                shapes[cmpDir.ordinal()] = switch (face) {
                    case UP -> ShapeUtils.rotateShapeAroundY(Direction.NORTH, dir, shape);
                    case DOWN -> ShapeUtils.rotateShapeAroundY(downBaseDir, dir, shape);
                    case NORTH, SOUTH -> ShapeUtils.rotateShapeAroundZ(horBaseDir.apply(face), dir, shape);
                    case WEST, EAST -> ShapeUtils.rotateShapeAroundX(horBaseDir.apply(face), dir, shape);
                };
            }
        }

        for (BlockState state : states) {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
            map.put(state, shapes[cmpDir.ordinal()]);
        }

        return ShapeContainer.of(map);
    }

    private BoardShapes() { }
}
