package xfacthd.framedblocks.api.predicate;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Objects;
import java.util.function.BiPredicate;

public interface FullFacePredicate extends BiPredicate<BlockState, Direction>
{
    FullFacePredicate TRUE = (state, dir) -> true;
    FullFacePredicate FALSE = (state, dir) -> false;
    FullFacePredicate Y_AXIS = (state, dir) -> Utils.isY(dir);
    FullFacePredicate TOP = (state, dir) -> state.getValue(FramedProperties.TOP) ? dir == Direction.UP : dir == Direction.DOWN;
    FullFacePredicate DIR = (state, dir) -> dir == state.getValue(BlockStateProperties.FACING);
    FullFacePredicate DIR_OPPOSITE = (state, dir) -> dir == state.getValue(BlockStateProperties.FACING).getOpposite();
    FullFacePredicate DIR_AXIS = (state, dir) -> dir.getAxis() == state.getValue(BlockStateProperties.FACING).getAxis();
    FullFacePredicate HOR_DIR = (state, dir) -> dir == state.getValue(FramedProperties.FACING_HOR);
    FullFacePredicate HOR_DIR_OPPOSITE = (state, dir) -> dir == state.getValue(FramedProperties.FACING_HOR).getOpposite();
    FullFacePredicate HOR_DIR_AXIS = (state, dir) -> dir.getAxis() == state.getValue(FramedProperties.FACING_HOR).getAxis();

    @Override
    boolean test(BlockState state, Direction direction);

    @Override
    default FullFacePredicate and(BiPredicate<? super BlockState, ? super Direction> other)
    {
        Objects.requireNonNull(other);
        return (state, side) -> test(state, side) && other.test(state, side);
    }

    @Override
    default FullFacePredicate or(BiPredicate<? super BlockState, ? super Direction> other)
    {
        Objects.requireNonNull(other);
        return (state, side) -> test(state, side) || other.test(state, side);
    }
}