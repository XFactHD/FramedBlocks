package io.github.xfacthd.framedblocks.common.data.shapes.cube;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class LayeredCubeShapes {
    private static final int LAYER_COUNT = 8;
    private static final int DIR_COUNT = Direction.values().length;

    public static ShapeContainer generate(List<BlockState> states) {
        VoxelShape[] shapes = new VoxelShape[LAYER_COUNT * DIR_COUNT];
        for (int i = 1; i <= LAYER_COUNT; i++) {
            VoxelShape layerShapeUp = Block.box(0, 0, 0, 16, i * 2, 16);
            VoxelShape layerShapeDown = ShapeUtils.rotateShapeAroundX(Direction.UP, Direction.DOWN, layerShapeUp);
            VoxelShape layerShapeNorth = ShapeUtils.rotateShapeAroundX(Direction.UP, Direction.NORTH, layerShapeUp);

            shapes[index(Direction.UP, i)] = layerShapeUp;
            shapes[index(Direction.DOWN, i)] = layerShapeDown;
            ShapeUtils.makeHorizontalRotations(layerShapeNorth, Direction.NORTH, shapes, i, LayeredCubeShapes::index);
        }

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            int layers = state.getValue(BlockStateProperties.LAYERS);
            map.put(state, shapes[index(dir, layers)]);
        }

        return ShapeContainer.of(map);
    }

    private static int index(Direction dir, int layers) {
        return ((layers - 1) * DIR_COUNT) + dir.ordinal();
    }

    private LayeredCubeShapes() { }
}
