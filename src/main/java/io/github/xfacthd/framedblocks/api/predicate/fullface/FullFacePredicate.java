package io.github.xfacthd.framedblocks.api.predicate.fullface;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Objects;
import java.util.function.BiPredicate;

/// Determines which faces of a given state of a framed block are considered "full"
/// (i.e. cover the entire surface at the block volume's outer perimeter).
@FunctionalInterface
public interface FullFacePredicate extends BiPredicate<BlockState, Direction> {
    /// Indicates that all faces of the block are full, regardless of the state.
    FullFacePredicate TRUE = (_, _) -> true;
    /// Indicates that none of the faces of the block are full, regardless of the state.
    FullFacePredicate FALSE = (_, _) -> false;
    /// Indicates that the vertical faces of the block are full, regardless of the state.
    FullFacePredicate Y_AXIS = (_, dir) -> DirUtils.isY(dir);
    /// Indicates that the top or bottom face of the block is full when the block is in the top or bottom half of the volume respectively.
    FullFacePredicate TOP = (state, dir) -> state.getValue(FramedProperties.TOP) ? dir == Direction.UP : dir == Direction.DOWN;
    /// Indicates that only the face the block is oriented towards is full.
    FullFacePredicate DIR = (state, dir) -> dir == state.getValue(BlockStateProperties.FACING);
    /// Indicates that only the face opposite of the block's orientation is full.
    FullFacePredicate DIR_OPPOSITE = (state, dir) -> dir == state.getValue(BlockStateProperties.FACING).getOpposite();
    /// Indicates that the faces along the axis of the block's orientation are full.
    FullFacePredicate DIR_AXIS = (state, dir) -> dir.getAxis() == state.getValue(BlockStateProperties.FACING).getAxis();
    /// Indicates that all faces except the one the block is oriented towards are full.
    FullFacePredicate NOT_DIR = (state, dir) -> dir != state.getValue(BlockStateProperties.FACING);
    /// Indicates that only the face the block is horizontally oriented towards is full.
    FullFacePredicate HOR_DIR = (state, dir) -> dir == state.getValue(FramedProperties.FACING_HOR);
    /// Indicates that only the face opposite of the block's horizontal orientation is full.
    FullFacePredicate HOR_DIR_OPPOSITE = (state, dir) -> dir == state.getValue(FramedProperties.FACING_HOR).getOpposite();
    /// Indicates that the faces along the axis of the block's horizontal orientation are full.
    FullFacePredicate HOR_DIR_AXIS = (state, dir) -> dir.getAxis() == state.getValue(FramedProperties.FACING_HOR).getAxis();
    /// Indicates that all faces except the ony the block is horizontally oriented towards are full.
    FullFacePredicate NOT_HOR_DIR = (state, side) -> side != state.getValue(FramedProperties.FACING_HOR);
    /// Indicates that the faces along the axis the block is oriented along are full.
    FullFacePredicate AXIS = (state, side) -> side.getAxis() == state.getValue(BlockStateProperties.AXIS);
    /// Indicates that the faces perpendicular to the axis the block is oriented along are full.
    FullFacePredicate NOT_AXIS = (state, side) -> side.getAxis() != state.getValue(BlockStateProperties.AXIS);
    /// Indicates that only the downward face of the block is full, regardless of the state.
    FullFacePredicate DOWN = (_, side) -> side == Direction.DOWN;

    @Override
    boolean test(BlockState state, Direction side);

    @Override
    default FullFacePredicate and(BiPredicate<? super BlockState, ? super Direction> other) {
        Objects.requireNonNull(other);
        return (state, side) -> test(state, side) && other.test(state, side);
    }

    @Override
    default FullFacePredicate or(BiPredicate<? super BlockState, ? super Direction> other) {
        Objects.requireNonNull(other);
        return (state, side) -> test(state, side) || other.test(state, side);
    }
}
