package io.github.xfacthd.framedblocks.api.model;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/// Extended blockstate model part with support for passing a "shader state" to the
/// Iris shader pipeline.
public interface ExtendedBlockStateModelPart extends BlockStateModelPart {
    /// Soft-override of an Iris extension to provide the "shader state" to its pipeline.
    ///
    /// @return the "real" appearance of the quads provided by this part
    @Nullable BlockState getBlockAppearance();
}
