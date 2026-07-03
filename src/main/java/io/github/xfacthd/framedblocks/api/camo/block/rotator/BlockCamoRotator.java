package io.github.xfacthd.framedblocks.api.camo.block.rotator;

import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/// Describes how to cycle through the states of a block applied as a camo.
public interface BlockCamoRotator {
    /// Default rotation handler, attempts to cycle through the first "orientation" property found on the block.
    BlockCamoRotator DEFAULT = new DefaultBlockCamoRotator();

    /// {@return whether the given state can be rotated}
    boolean canRotate(BlockState state);

    /// Rotate the given state, returning the adjusted state if it's
    /// rotatable or `null` if it is not rotatable.
    ///
    /// @param state The original state
    /// @return the rotated state or `null` if it's not rotatable
    @Nullable BlockState rotate(BlockState state);

    /// {@return the rotator for the given block}
    ///
    /// @param block The block to get the rotator for
    static BlockCamoRotator of(Block block) {
        return InternalAPI.INSTANCE.getCamoRotator(block);
    }
}
