package io.github.xfacthd.framedblocks.api.block.render;

import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

/**
 * Declares whether the "uncullable" quads of either part of a double block should be culled for a particular part state
 * when the other part has an opaque camo applied to it.
 */
public record NullCullPredicate(Predicate<BlockState> partOne, Predicate<BlockState> partTwo) {
    public static final NullCullPredicate NEVER = new NullCullPredicate(_ -> false, _ -> false);
    public static final NullCullPredicate ALWAYS = new NullCullPredicate(_ -> true, _ -> true);
    public static final NullCullPredicate ONLY_PART_ONE = new NullCullPredicate(_ -> true, _ -> false);
    public static final NullCullPredicate ONLY_PART_TWO = new NullCullPredicate(_ -> false, _ -> true);

    public boolean testPartOne(BlockState state) {
        return partOne.test(state);
    }

    public boolean testPartTwo(BlockState state) {
        return partTwo.test(state);
    }
}
