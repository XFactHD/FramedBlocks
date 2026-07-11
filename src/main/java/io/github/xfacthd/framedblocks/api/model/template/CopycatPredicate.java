package io.github.xfacthd.framedblocks.api.model.template;

import io.github.xfacthd.framedblocks.api.block.CopycatStyleBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/// Determines which faces of a block implementing [CopycatStyleBlock] should use copycat-style quad
/// cutting when [CopycatStyleBlock#isCopycatStyle(BlockState)] returns true.
@FunctionalInterface
public interface CopycatPredicate {
    /// Indicates that none of the block's faces can use copycat-style quad cutting.
    CopycatPredicate NEVER = (_, _) -> false;
    /// Indicates that all faces of the block should use copycat-style quad cutting.
    CopycatPredicate ALWAYS = (_, _) -> true;

    /// {@return whether faces with the given normal and cullability can use copycat-style quad cutting}
    ///
    /// @param normal   The direction closest to the quad's normal vector
    /// @param cullable Whether the face is cullable
    boolean test(Direction normal, boolean cullable);
}
