package io.github.xfacthd.framedblocks.api.predicate.fullface;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Objects;
import java.util.function.BiPredicate;

public interface FullFacePredicate extends BiPredicate<BlockState, Direction>
{
    FullFacePredicate TRUE = (_, _) -> true;
    FullFacePredicate FALSE = (_, _) -> false;
    FullFacePredicate Y_AXIS = (_, dir) -> DirUtils.isY(dir);
    FullFacePredicate TOP = (state, dir) -> state.getValue(FramedProperties.TOP) ? dir == Direction.UP : dir == Direction.DOWN;
    FullFacePredicate DIR = (state, dir) -> dir == state.getValue(BlockStateProperties.FACING);
    FullFacePredicate DIR_OPPOSITE = (state, dir) -> dir == state.getValue(BlockStateProperties.FACING).getOpposite();
    FullFacePredicate DIR_AXIS = (state, dir) -> dir.getAxis() == state.getValue(BlockStateProperties.FACING).getAxis();
    FullFacePredicate NOT_DIR = (state, dir) -> dir != state.getValue(BlockStateProperties.FACING);
    FullFacePredicate HOR_DIR = (state, dir) -> dir == state.getValue(FramedProperties.FACING_HOR);
    FullFacePredicate HOR_DIR_OPPOSITE = (state, dir) -> dir == state.getValue(FramedProperties.FACING_HOR).getOpposite();
    FullFacePredicate HOR_DIR_AXIS = (state, dir) -> dir.getAxis() == state.getValue(FramedProperties.FACING_HOR).getAxis();
    FullFacePredicate NOT_HOR_DIR = (state, side) -> side != state.getValue(FramedProperties.FACING_HOR);
    FullFacePredicate AXIS = (state, side) -> side.getAxis() == state.getValue(BlockStateProperties.AXIS);
    FullFacePredicate NOT_AXIS = (state, side) -> side.getAxis() != state.getValue(BlockStateProperties.AXIS);
    FullFacePredicate DOWN = (_, side) -> side == Direction.DOWN;

    @Override
    boolean test(BlockState state, Direction side);

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
