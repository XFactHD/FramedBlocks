package io.github.xfacthd.framedblocks.api.block;

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
