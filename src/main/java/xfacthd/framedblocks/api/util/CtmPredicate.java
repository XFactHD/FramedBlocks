package xfacthd.framedblocks.api.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Objects;
import java.util.function.BiPredicate;

public interface CtmPredicate extends BiPredicate<BlockState, Direction>
{
    CtmPredicate TRUE = (state, dir) -> true;
    CtmPredicate FALSE = (state, dir) -> false;
    CtmPredicate Y_AXIS = (state, dir) -> Utils.isY(dir);
    CtmPredicate TOP = (state, dir) -> state.getValue(FramedProperties.TOP) ? dir == Direction.UP : dir == Direction.DOWN;
    CtmPredicate DIR = (state, dir) -> dir == state.getValue(BlockStateProperties.FACING);
    CtmPredicate DIR_OPPOSITE = (state, dir) -> dir == state.getValue(BlockStateProperties.FACING).getOpposite();
    CtmPredicate DIR_AXIS = (state, dir) -> dir.getAxis() == state.getValue(BlockStateProperties.FACING).getAxis();
    CtmPredicate HOR_DIR = (state, dir) -> dir == state.getValue(FramedProperties.FACING_HOR);
    CtmPredicate HOR_DIR_OPPOSITE = (state, dir) -> dir == state.getValue(FramedProperties.FACING_HOR).getOpposite();
    CtmPredicate HOR_DIR_AXIS = (state, dir) -> dir.getAxis() == state.getValue(FramedProperties.FACING_HOR).getAxis();

    @Override
    boolean test(BlockState state, Direction direction);

    @Override
    default CtmPredicate and(BiPredicate<? super BlockState, ? super Direction> other)
    {
        Objects.requireNonNull(other);
        return (state, side) -> test(state, side) && other.test(state, side);
    }

    @Override
    default CtmPredicate or(BiPredicate<? super BlockState, ? super Direction> other)
    {
        Objects.requireNonNull(other);
        return (state, side) -> test(state, side) || other.test(state, side);
    }
}