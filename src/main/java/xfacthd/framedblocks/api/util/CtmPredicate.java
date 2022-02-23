package xfacthd.framedblocks.api.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;

import java.util.function.BiPredicate;

public interface CtmPredicate extends BiPredicate<BlockState, Direction>
{
    CtmPredicate TRUE = (state, dir) -> true;
    CtmPredicate FALSE = (state, dir) -> false;
    CtmPredicate Y_AXIS = (state, dir) -> Utils.isY(dir);
}