package io.github.xfacthd.framedblocks.api.predicate.cull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

/// Determines the occlusion behavior of a given state of a framed block against an adjacent framed block.
@FunctionalInterface
public interface SideSkipPredicate {
    /// Default instance indicating that the block can never be occluded.
    SideSkipPredicate FALSE = (_, _, _, _, _) -> false;

    /// Check whether the given side should be hidden in presence of the given neighbor.
    ///
    /// @param level    The level the blocks are in
    /// @param pos      The position of the block
    /// @param state    The block being occluded
    /// @param adjState The occluding block
    /// @param side     The side being occluded
    /// @return whether the given side should be hidden
    boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side);
}
