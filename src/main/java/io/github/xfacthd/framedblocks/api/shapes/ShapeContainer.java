package io.github.xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/// Simple key-value store holding voxel shapes indexed by blockstates.
public sealed interface ShapeContainer permits SingleShapeContainer, MapBackedShapeContainer {
    /// The singleton empty shape container.
    ShapeContainer EMPTY = new SingleShapeContainer();

    /// {@return the shape for the given state}
    ///
    /// @param state The state to get the shape for
    VoxelShape get(BlockState state);

    /// {@return whether this container is empty}
    boolean isEmpty();

    /// Performs the given action for each entry in the backing map.
    ///
    /// @param consumer The action to perform
    void forEach(BiConsumer<BlockState, VoxelShape> consumer);

    /// {@return a new shape container wrapping the given map}
    ///
    /// @param shapes The map to wrap
    static ShapeContainer of(Map<BlockState, VoxelShape> shapes) {
        return new MapBackedShapeContainer(shapes);
    }

    /// {@return a new shape container mapping the given shape to the given states}
    ///
    /// @param states The states of the block
    /// @param shape  The shape to provide
    static ShapeContainer singleShape(List<BlockState> states, VoxelShape shape) {
        return new SingleShapeContainer(states, shape);
    }
}
