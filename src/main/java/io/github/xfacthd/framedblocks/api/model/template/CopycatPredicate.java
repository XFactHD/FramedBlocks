package io.github.xfacthd.framedblocks.api.model.template;

import io.github.xfacthd.framedblocks.api.block.CopycatStyleBlock;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
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
    /// Indicates that all horizontal faces of the block should use copycat-style quad cutting.
    CopycatPredicate HORIZONTAL = (normal, _) -> DirUtils.isHorizontal(normal);
    /// Indicates that all vertical faces of the block should use copycat-style quad cutting.
    CopycatPredicate VERTICAL = (normal, _) -> DirUtils.isY(normal);
    /// Indicates that all x-axis faces of the block should use copycat-style quad cutting.
    CopycatPredicate X = (normal, _) -> DirUtils.isX(normal);
    /// Indicates that all z-axis faces of the block should use copycat-style quad cutting.
    CopycatPredicate Z = (normal, _) -> DirUtils.isZ(normal);
    /// Indicates that all except x-axis faces of the block should use copycat-style quad cutting.
    CopycatPredicate NOT_X = (normal, _) -> !DirUtils.isX(normal);
    /// Indicates that all except z-axis faces of the block should use copycat-style quad cutting.
    CopycatPredicate NOT_Z = (normal, _) -> !DirUtils.isZ(normal);

    /// {@return whether faces with the given normal and cullability can use copycat-style quad cutting}
    ///
    /// @param normal   The direction closest to the quad's normal vector
    /// @param cullable Whether the face is cullable
    boolean test(Direction normal, boolean cullable);

    /// {@return a copycat predicate which only returns true for faces of the given axis}
    ///
    /// @param axis The axis to check the quad normals against
    static CopycatPredicate ofAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> X;
            case Y -> VERTICAL;
            case Z -> Z;
        };
    }

    /// {@return a copycat predicate which returns false for faces of the given axis}
    ///
    /// @param axis The axis to check the quad normals against
    static CopycatPredicate ofNotAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> NOT_X;
            case Y -> HORIZONTAL;
            case Z -> NOT_Z;
        };
    }
}
