package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

/// Indicates that the implementing block uses copycat-style quad cutting.
public sealed interface CopycatStyleBlock {

    /// {@return whether the given state uses copycat-style quad cutting}
    ///
    /// @param state The blockstate to check
    boolean isCopycatStyle(BlockState state);

    /// Indicates that the implementing block conditionally uses copycat-style quad cutting depending on a blockstate property.
    non-sealed interface StateDependent extends CopycatStyleBlock {
        @Override
        default boolean isCopycatStyle(BlockState state) {
            return state.getValue(FramedProperties.COPYCAT_STYLE);
        }

        /// Toggles the copycat-style state of this block if the player used the correct tool to interact with it.
        ///
        /// @param state  The current state of this block
        /// @param level  The level this block is in
        /// @param pos    The position this block is at
        /// @param player The player interacting with this block
        /// @return whether the copycat-style state was toggled
        default boolean toggleCopycatStyle(BlockState state, Level level, BlockPos pos, Player player) {
            ItemStack stack = player.getMainHandItem();
            if (stack.is(FramedConstants.Objects.FRAMED_HAMMER.value())) {
                if (!level.isClientSide()) {
                    state = state.setValue(FramedProperties.COPYCAT_STYLE, !state.getValue(FramedProperties.COPYCAT_STYLE));
                    level.setBlock(pos, state, Block.UPDATE_ALL);
                }
                return true;
            }
            return false;
        }
    }

    /// Indicates that the implementing block always uses copycat-style quad cutting.
    non-sealed interface Always extends CopycatStyleBlock {
        @Override
        @ApiStatus.NonExtendable
        default boolean isCopycatStyle(BlockState state) {
            return true;
        }
    }

    /// {@return whether the given state's block is a {@link CopycatStyleBlock} and the state uses copycat-style quad cutting}
    ///
    /// @param state The blockstate to check
    static boolean tryIsCopycatStyle(BlockState state) {
        return state.getBlock() instanceof CopycatStyleBlock block && block.isCopycatStyle(state);
    }
}
