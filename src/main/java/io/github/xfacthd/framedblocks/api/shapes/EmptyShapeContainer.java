package io.github.xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.BiConsumer;

final class EmptyShapeContainer implements ShapeContainer {
    static final EmptyShapeContainer INSTANCE = new EmptyShapeContainer();

    private EmptyShapeContainer() { }

    @Override
    public VoxelShape get(BlockState state) {
        return Shapes.empty();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void forEach(BiConsumer<BlockState, VoxelShape> consumer) { }
}
