package io.github.xfacthd.framedblocks.api.block.item.placement;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/// A post-processor for modifying the placement state without affecting the stored state.
public interface PlacementStatePostProcessor {
    /// Post-process the placement state without changing the stored state or return `null`
    /// to prevent placing the block.
    ///
    /// @param state   The selected placement state
    /// @param context The context used for placing the block
    /// @return the modified state or null to prevent placement
    @Nullable BlockState postProcessPlacementState(BlockState state, BlockPlaceContext context);
}
