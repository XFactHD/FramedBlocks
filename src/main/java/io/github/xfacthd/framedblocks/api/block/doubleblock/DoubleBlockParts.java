package io.github.xfacthd.framedblocks.api.block.doubleblock;

import io.github.xfacthd.framedblocks.api.block.CopycatStyleBlock;
import net.minecraft.world.level.block.state.BlockState;

/// Holds the two part [BlockState]s making up a double block.
///
/// @param stateOne The first part's state
/// @param stateTwo The second part's state
public record DoubleBlockParts(BlockState stateOne, BlockState stateTwo) {
    /// Convenience constructor for copying certain properties to the part states.
    ///
    /// @param doubleState The state of the double block using this part pair
    /// @param stateOne    The first part's state
    /// @param stateTwo    The second part's state
    public DoubleBlockParts(BlockState doubleState, BlockState stateOne, BlockState stateTwo) {
        if (CopycatStyleBlock.tryIsCopycatStyle(doubleState)) {
            stateOne = CopycatStyleBlock.trySetCopycatStyle(stateOne, true);
            stateTwo = CopycatStyleBlock.trySetCopycatStyle(stateTwo, true);
        }
        this(stateOne, stateTwo);
    }

    public BlockState get(boolean second) {
        return second ? stateTwo : stateOne;
    }
}
