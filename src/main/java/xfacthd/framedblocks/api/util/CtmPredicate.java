package xfacthd.framedblocks.api.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.BiPredicate;

public interface CtmPredicate extends BiPredicate<BlockState, Direction>
{
    CtmPredicate TRUE = (state, dir) -> true;
    CtmPredicate FALSE = (state, dir) -> false;
    CtmPredicate Y_AXIS = (state, dir) -> Utils.isY(dir);
    CtmPredicate DIR = (state, dir) -> dir == state.getValue(BlockStateProperties.FACING);
    CtmPredicate DIR_OPPOSITE = (state, dir) -> dir == state.getValue(BlockStateProperties.FACING).getOpposite();
    CtmPredicate HOR_DIR = (state, dir) -> dir == state.getValue(FramedProperties.FACING_HOR);
    CtmPredicate HOR_DIR_OPPOSITE = (state, dir) -> dir == state.getValue(FramedProperties.FACING_HOR).getOpposite();

    @Override
    boolean test(BlockState state, Direction direction);
}