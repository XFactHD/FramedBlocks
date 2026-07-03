package io.github.xfacthd.framedblocks.api.block.render;

import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

/// Declares whether the "uncullable" quads of either part of a double block should be culled for a particular part state
/// when the other part has an opaque camo applied to it.
///
/// @param partOne The predicate testing whether the given state of the first part can have its "uncullable" quads culled
/// @param partTwo The predicate testing whether the given state of the second part can have its "uncullable" quads culled
public record NullCullPredicate(Predicate<BlockState> partOne, Predicate<BlockState> partTwo) {
    /// Indicates that "uncullable" quads can never be culled.
    public static final NullCullPredicate NEVER = new NullCullPredicate(_ -> false, _ -> false);
    /// Indicates that "uncullable" quads of either part can alway be culled if the other part has an opaque camo applied.
    public static final NullCullPredicate ALWAYS = new NullCullPredicate(_ -> true, _ -> true);
    /// Indicates that only "uncullable" quads of the first part can be culled if the second part has an opaque camo applied.
    public static final NullCullPredicate ONLY_PART_ONE = new NullCullPredicate(_ -> true, _ -> false);
    /// Indicates that only "uncullable" quads of the second part can be culled if the first part has an opaque camo applied.
    public static final NullCullPredicate ONLY_PART_TWO = new NullCullPredicate(_ -> false, _ -> true);

    /// {@return whether the given state of the first part can have its "uncullable" quads culled}
    ///
    /// @param state The state to test against
    public boolean testPartOne(BlockState state) {
        return partOne.test(state);
    }

    /// {@return whether the given state of the second part can have its "uncullable" quads culled}
    ///
    /// @param state The state to test against
    public boolean testPartTwo(BlockState state) {
        return partTwo.test(state);
    }
}
