package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/// Marks a block as having a slope face which can be created from two possible faces:
///
/// - When the slope face has a [vertical][SlopeOrientation#VERTICAL] orientation then the slope is expected to be created from
///   a "front" quad if [FramedProperties#ALT_SLOPE] is `false` or from a top/bottom quad if it's `true`.
/// - When the slope face has a [horizontal][SlopeOrientation#HORIZONTAL] orientation then the slope is expected to be created from
///   a "front" quad if [FramedProperties#ALT_SLOPE] is `false` or from a "right" (i.e. counter-clockwise) quad if it's `true`.
///
/// Blocks implementing this interface must have the [FramedProperties#ALT_SLOPE] property.
// TODO: Improve alt-slope handling across all relevant blocks
public interface SlopeToggleBlock extends IFramedBlock {
    /// {@return the orientation of the slope face of the given state of this block}
    ///
    /// @param state The state of this block
    default SlopeOrientation getSlopeOrientation(BlockState state) {
        return SlopeOrientation.VERTICAL;
    }

    /// Toggle between slope source faces.
    ///
    /// @param state  The state of this block
    /// @param level  The level this block is in
    /// @param pos    The position of this block
    /// @param player The player interacting with this block
    /// @return whether the slope source face changed
    static boolean toggleAltSlope(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.getMainHandItem().getItem() == FramedConstants.Objects.FRAMED_WRENCH.value()) {
            level.setBlockAndUpdate(pos, state.setValue(FramedProperties.ALT_SLOPE, !state.getValue(FramedProperties.ALT_SLOPE)));
            return true;
        }
        return false;
    }

    /// Indicates the orientation of the slope face.
    enum SlopeOrientation {
        /// The slope face's normal is a combination of a horizontal direction and a vertical direction.
        VERTICAL,
        /// The slope face's normal is a combination of two horizontal directions.
        HORIZONTAL
    }
}
