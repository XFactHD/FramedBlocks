package io.github.xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

final class SingleShapeContainer implements ShapeContainer {
    private final List<BlockState> states;
    private final VoxelShape shape;

    SingleShapeContainer(List<BlockState> states, VoxelShape shape) {
        this.states = states;
        this.shape = shape;
    }

    SingleShapeContainer() {
        this.states = List.of();
        this.shape = Shapes.empty();
    }

    @Override
    public VoxelShape get(BlockState state) {
        return shape;
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public void forEach(BiConsumer<BlockState, VoxelShape> consumer) {
        states.forEach(state -> consumer.accept(state, shape));
    }

    VoxelShape getShape() {
        return shape;
    }

    static @Nullable SingleShapeContainer unwrap(ShapeContainer provider) {
        if (provider.isEmpty()) {
            return null;
        }
        if (provider instanceof SingleShapeContainer singleShape) {
            return singleShape;
        }
        throw new IllegalArgumentException("Expected SingleShapeProvider, got " + provider);
    }
}
