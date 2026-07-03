package io.github.xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// Generates a shape container holding the primary shapes for a given list of blockstates
/// and optionally a shape container holding separate occlusion shapes.
public interface ShapeGenerator {
    /// Shape generator providing an empty shape container.
    ShapeGenerator EMPTY = _ -> ShapeContainer.EMPTY;

    /// Generate the primary shapes for the given list of blockstates.
    ///
    /// @param states The blockstates to generate shapes for
    /// @return a shape container holding the generated shapes
    @ApiStatus.OverrideOnly
    ShapeContainer generatePrimary(List<BlockState> states);

    /// Generate the occlusion shapes for the given list of blockstates or return an empty
    /// container if the block does not use separate occlusion shapes.
    ///
    /// @param states The blockstates to generate shapes for
    /// @return a shape container holding the generated shapes
    @ApiStatus.OverrideOnly
    default ShapeContainer generateOcclusion(List<BlockState> states) {
        return ShapeContainer.EMPTY;
    }

    /// Returns a shape generator mapping the given shape and optionally the separate occlusion
    /// shape to all states the generator is invoked with.
    ///
    /// @param shape          The primary shape to use
    /// @param occlusionShape The separate occlusion shape to use, if applicable
    /// @return a single-shape shape generator
    static ShapeGenerator singleShape(VoxelShape shape, @Nullable VoxelShape occlusionShape) {
        return new SingleShapeGenerator(shape, occlusionShape);
    }
}
