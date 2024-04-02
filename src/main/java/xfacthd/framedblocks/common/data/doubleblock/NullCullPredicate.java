package xfacthd.framedblocks.common.data.doubleblock;

import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public record NullCullPredicate(Predicate<BlockState> leftStateTest, Predicate<BlockState> rightStateTest)
{
    public static final NullCullPredicate NEVER = new NullCullPredicate(state -> false, state -> false);
    public static final NullCullPredicate ALWAYS = new NullCullPredicate(state -> true, state -> true);
    public static final NullCullPredicate ONLY_LEFT = new NullCullPredicate(state -> true, state -> false);
    public static final NullCullPredicate ONLY_RIGHT = new NullCullPredicate(state -> false, state -> true);

    public boolean testLeft(BlockState state)
    {
        return leftStateTest.test(state);
    }

    public boolean testRight(BlockState state)
    {
        return rightStateTest.test(state);
    }
}
