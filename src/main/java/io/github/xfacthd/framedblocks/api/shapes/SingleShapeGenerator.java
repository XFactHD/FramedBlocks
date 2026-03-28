package io.github.xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;

final class SingleShapeGenerator implements ShapeGenerator {
    private final VoxelShape shape;
    @Nullable
    private final VoxelShape occlusionShape;

    SingleShapeGenerator(VoxelShape shape, @Nullable VoxelShape occlusionShape) {
        this.shape = shape;
        this.occlusionShape = occlusionShape;
    }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return ShapeContainer.singleShape(states, shape);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return occlusionShape != null ? ShapeContainer.singleShape(states, occlusionShape) : ShapeContainer.EMPTY;
    }
}
