package io.github.xfacthd.framedblocks.common.data.shapes.pane;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class BoardShapes {
    private static final Direction[] DIRECTIONS = Direction.values();

    public static ShapeContainer generate(List<BlockState> states) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape shape = Block.box(0, 0, 0, 16, 16, 1);
        VoxelShape[] shapesHor = ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH);
        VoxelShape shapeBottom = Block.box(0, 0, 0, 16, 1, 16);
        VoxelShape shapeTop = Block.box(0, 15, 0, 16, 16, 16);

        VoxelShape[] allShapes = new VoxelShape[(1 << 6) - 1];
        for (int i = 0; i < allShapes.length; i++) {
            VoxelShape merged = Shapes.empty();
            for (Direction dir : DIRECTIONS) {
                if (((i + 1) & (1 << dir.ordinal())) == 0) {
                    continue;
                }

                VoxelShape faceShape = switch (dir) {
                    case DOWN -> shapeBottom;
                    case UP -> shapeTop;
                    default -> shapesHor[dir.get2DDataValue()];
                };
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

    private BoardShapes() { }
}
