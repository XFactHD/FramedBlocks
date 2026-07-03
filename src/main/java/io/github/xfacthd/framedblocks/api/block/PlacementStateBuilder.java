package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/// Builder for constructing the placement state of a block based on a given context.
///
/// @param <T> The specific type of this builder
public class PlacementStateBuilder<T extends PlacementStateBuilder<T>> {
    protected final Block block;
    protected final BlockPlaceContext ctx;
    @Nullable
    protected BlockState state;

    /// @param block The block to build the placement state for
    /// @param state The pre-existing placement state
    /// @param ctx   The context used to place the block
    protected PlacementStateBuilder(Block block, @Nullable BlockState state, BlockPlaceContext ctx) {
        this.block = block;
        this.ctx = ctx;
        this.state = state;
    }

    /// {@return a new placement state builder for the given block with the given context}
    ///
    /// @param block The block to build the placement state for
    /// @param ctx   The context used to place the block
    public static PlacementStateBuilder<?> of(Block block, BlockPlaceContext ctx) {
        return of(block, block.defaultBlockState(), ctx);
    }

    /// {@return a new placement state builder for the given block with the given context and the given pre-existing placement state}
    ///
    /// @param block The block to build the placement state for
    /// @param state The pre-existing placement state
    /// @param ctx   The context used to place the block
    public static PlacementStateBuilder<?> of(Block block, @Nullable BlockState state, BlockPlaceContext ctx) {
        return new PlacementStateBuilder<>(block, state, ctx);
    }

    /// Set the state's [FramedProperties#FACING_HOR] property to the player's horizontal looking direction.
    ///
    /// @return this builder
    public final T withHorizontalFacing() {
        return withHorizontalFacing(false);
    }

    /// Set the state's [FramedProperties#FACING_HOR] property to the player's horizontal looking direction.
    ///
    /// @param opposite Whether the direction should be inverted
    /// @return this builder
    public final T withHorizontalFacing(boolean opposite) {
        if (state == null) {
            return self();
        }

        Direction dir = ctx.getHorizontalDirection();
        if (opposite) {
            dir = dir.getOpposite();
        }
        state = state.setValue(FramedProperties.FACING_HOR, dir);
        return self();
    }

    /// Set the state's [FramedProperties#FACING_HOR] property to the players horizontal looking direction
    /// when looking at a vertical face or the inverse of the faces direction when looking at a horizontal face.
    ///
    /// @return this builder
    public final T withTargetOrHorizontalFacing() {
        return withTargetOrHorizontalFacing(false);
    }

    /// Set the state's [FramedProperties#FACING_HOR] property to the players horizontal looking direction
    /// when looking at a vertical face or the inverse of the faces direction when looking at a horizontal face.
    ///
    /// @param opposite Whether the final direction should be inverted
    /// @return this builder
    public final T withTargetOrHorizontalFacing(boolean opposite) {
        if (state == null) {
            return self();
        }

        Direction face = ctx.getClickedFace();
        if (!DirUtils.isY(face)) {
            if (!opposite) {
                face = face.getOpposite();
            }
            state = state.setValue(FramedProperties.FACING_HOR, face);
            return self();
        }
        return withHorizontalFacing(opposite);
    }

    /// Set the state's [FramedProperties#FACING_HOR] property to the inverse of the faces direction
    /// when looking at a horizontal face. If the face being looked at is vertical, the builder will short-circuit all
    /// subsequent modifications and return a `null` state from [#build()].
    ///
    /// @return this builder
    public final T withHorizontalTargetFacing() {
        return withHorizontalTargetFacing(false);
    }

    /// Set the state's [FramedProperties#FACING_HOR] property to the inverse of the faces direction
    /// when looking at a horizontal face. If the face being looked at is vertical, the builder will short-circuit all
    /// subsequent modifications and return a `null` state from [#build()].
    ///
    /// @param opposite Whether the direction should be inverted
    /// @return this builder
    public final T withHorizontalTargetFacing(boolean opposite) {
        if (state == null) {
            return self();
        }

        Direction face = ctx.getClickedFace();
        if (DirUtils.isY(face)) {
            state = null;
            return self();
        }

        if (!opposite) {
            face = face.getOpposite();
        }
        state = state.setValue(FramedProperties.FACING_HOR, face);
        return self();
    }

    /// Set the state's [BlockStateProperties#FACING] property to the inverse of the face being looked at.
    ///
    /// @return this builder
    public final T withTargetFacing() {
        return withTargetFacing(false);
    }

    /// Set the state's [BlockStateProperties#FACING] property to the inverse of the face being looked at.
    ///
    /// @param opposite Whether the direction should be inverted
    /// @return this builder
    public final T withTargetFacing(boolean opposite) {
        if (state == null) {
            return self();
        }

        Direction face = ctx.getClickedFace();
        if (!opposite) {
            face = face.getOpposite();
        }
        state = state.setValue(BlockStateProperties.FACING, face);
        return self();
    }

    /// Set the state's [FramedProperties#FACING_HOR] property depending on the targeted horizontal half when
    /// looking at a horizontal face or to the player's horizontal looking direction when looking at a vertical face.
    ///
    /// @return this builder
    public final T withHalfOrHorizontalFacing() {
        if (state == null) {
            return self();
        }

        Direction side = ctx.getClickedFace();
        if (DirUtils.isY(side)) {
            state = state.setValue(FramedProperties.FACING_HOR, ctx.getHorizontalDirection());
        } else if (MathUtils.fractionInDir(ctx.getClickLocation(), side.getCounterClockWise()) > .5) {
            state = state.setValue(FramedProperties.FACING_HOR, side.getOpposite().getClockWise());
        } else {
            state = state.setValue(FramedProperties.FACING_HOR, side.getOpposite());
        }
        return self();
    }

    /// Set the state's [FramedProperties#FACING_HOR] property depending on the targeted XZ quarter when
    /// looking at a vertical face or depending on the targeted horizontal half when looking at a horizontal face.
    ///
    /// @return this builder
    public final T withHalfOrQuarterFacing() {
        if (state == null) {
            return self();
        }

        Direction side = ctx.getClickedFace();
        if (DirUtils.isY(side)) {
            Vec3 hitVec = ctx.getClickLocation();
            double x = MathUtils.fractionInDir(hitVec, Direction.EAST);
            double z = MathUtils.fractionInDir(hitVec, Direction.SOUTH);

            Direction dir = z > .5D ? Direction.SOUTH : Direction.NORTH;
            if ((x > .5D) != DirUtils.isPositive(dir)) {
                dir = dir.getClockWise();
            }
            state = state.setValue(FramedProperties.FACING_HOR, dir);

            return self();
        }
        return withHalfOrHorizontalFacing();
    }

    /// Set the state's [FramedProperties#FACING_HOR] property depending on the targeted half split along the
    /// horizontal looking direction when looking at a vertical face or depending on the targeted horizontal half
    /// when looking at a horizontal face.
    ///
    /// @return this builder
    public final T withHalfFacing() {
        if (state == null) {
            return self();
        }

        Direction side = ctx.getClickedFace();
        if (DirUtils.isY(side)) {
            Direction dir = ctx.getHorizontalDirection();
            double xz = MathUtils.fractionInDir(ctx.getClickLocation(), dir.getClockWise());
            if (xz > .5D) {
                dir = dir.getClockWise();
            }
            state = state.setValue(FramedProperties.FACING_HOR, dir);

            return self();
        }
        return withHalfOrHorizontalFacing();
    }

    /// Set the state's [BlockStateProperties#AXIS] property to the [Direction.Axis] of
    /// the face the player clicked on.
    ///
    /// @return this builder
    public final T withClickedAxis() {
        if (state != null) {
            state = state.setValue(BlockStateProperties.AXIS, ctx.getClickedFace().getAxis());
        }
        return self();
    }

    /// Set the state's [FramedProperties#TOP] property depending on the face when looking at a vertical face or
    /// depending on the targeted vertical half when looking at a horizontal face.
    ///
    /// @return this builder
    public final T withTop() {
        return withTop(FramedProperties.TOP);
    }

    /// Set the given property on the state depending on the face when looking at a vertical face or
    /// depending on the targeted vertical half when looking at a horizontal face.
    ///
    /// @param prop The property to set
    /// @return this builder
    public final T withTop(BooleanProperty prop) {
        if (state == null) {
            return self();
        }

        Direction side = ctx.getClickedFace();
        if (side == Direction.DOWN) {
            state = state.setValue(prop, true);
        } else if (side == Direction.UP) {
            state = state.setValue(prop, false);
        } else {
            double y = MathUtils.fractionInDir(ctx.getClickLocation(), Direction.UP);
            state = state.setValue(prop, y >= .5D);
        }
        return self();
    }

    /// Set the state's [BlockStateProperties#WATERLOGGED] property if the state has said property.
    ///
    /// @return this builder
    public final T tryWithWater() {
        if (state != null && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            return withWater();
        }
        return self();
    }

    /// Set the state's [BlockStateProperties#WATERLOGGED] property.
    ///
    /// @return this builder
    public final T withWater() {
        if (state == null) {
            return self();
        }

        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        state = state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
        return self();
    }

    /// Set the state's [FramedProperties#ALT_SLOPE] property to the given value.
    ///
    /// @param altSlope The target value
    /// @return this builder
    public final T withAltSlope(boolean altSlope) {
        if (state == null) {
            return self();
        }

        state = state.setValue(FramedProperties.ALT_SLOPE, altSlope);
        return self();
    }

    /// Apply a custom modification to the state. The given modifier can return a null state if it deems
    /// the block unplaceable in the given environment, in which case the builder will short-circuit all
    /// subsequent modifications and return a `null` state from [#build()].
    ///
    /// @param modifier The modifier to apply to the placement state
    /// @return this builder
    public final T withCustom(BiFunction<BlockState, BlockPlaceContext, @Nullable BlockState> modifier) {
        if (state == null) {
            return self();
        }

        state = modifier.apply(state, ctx);
        return self();
    }

    /// Validate the calculated state in the given environment. If the validator returns false, the builder will
    /// short-circuit all subsequent modifications and return a `null` state from [#build()].
    ///
    /// @param validator The validation predicate
    /// @return this builder
    public final T validate(BiPredicate<BlockState, BlockPlaceContext> validator) {
        if (state != null && !validator.test(state, ctx)) {
            state = null;
        }
        return self();
    }

    /// {@return the final state from this builder}
    ///
    /// Due to states being immutable, the builder can theoretically be re-used after calling this.
    public final @Nullable BlockState build() {
        return state;
    }

    /// {@return this builder casted to its concrete type}
    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }
}
