package xfacthd.framedblocks.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

import java.util.function.BiPredicate;

public interface CtmPredicate extends BiPredicate<BlockState, Direction>
{
    CtmPredicate TRUE = (state, dir) -> true;
    CtmPredicate FALSE = (state, dir) -> false;
    CtmPredicate Y_AXIS = (state, dir) -> dir.getAxis() == Direction.Axis.Y;
}