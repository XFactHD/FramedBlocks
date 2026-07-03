package io.github.xfacthd.framedblocks.api.block.doubleblock;

import net.minecraft.world.level.block.state.BlockState;

/// Holds the two part [BlockState]s making up a double block.
///
/// @param stateOne The first part's state
/// @param stateTwo The second part's state
public record DoubleBlockParts(BlockState stateOne, BlockState stateTwo) { }
