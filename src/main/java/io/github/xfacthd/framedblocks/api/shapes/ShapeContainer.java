package io.github.xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public sealed interface ShapeContainer permits EmptyShapeContainer, SingleShapeContainer, MapBackedShapeContainer {
    ShapeContainer EMPTY = EmptyShapeContainer.INSTANCE;

    VoxelShape get(BlockState state);

    boolean isEmpty();

    void forEach(BiConsumer<BlockState, VoxelShape> consumer);

    static ShapeContainer of(Map<BlockState, VoxelShape> shapes) {
        return new MapBackedShapeContainer(shapes);
    }

    static ShapeContainer singleShape(List<BlockState> states, VoxelShape shape) {
        return new SingleShapeContainer(states, shape);
    }
}
