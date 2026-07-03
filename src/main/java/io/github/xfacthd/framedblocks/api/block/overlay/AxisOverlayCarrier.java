package io.github.xfacthd.framedblocks.api.block.overlay;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/// Indicates that the block implementing this interface has an axis orientation to
/// which the [BlockOverlay] should be aligned.
public interface AxisOverlayCarrier {
    /// {@return the orientation axis of the given state}
    ///
    /// @param state The state to compute the orientation axis for
    Direction.Axis getAxis(BlockState state);
}
