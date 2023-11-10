package xfacthd.framedblocks.api.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.Utils;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class PlacementStateBuilder<T extends PlacementStateBuilder<T>>
{
    protected final Block block;
    protected final BlockPlaceContext ctx;
    @Nullable
    protected BlockState state;

    protected PlacementStateBuilder(Block block, BlockPlaceContext ctx)
    {
        this.block = block;
        this.ctx = ctx;
        this.state = block.defaultBlockState();
    }

    public static PlacementStateBuilder<?> of(Block block, BlockPlaceContext ctx)
    {
        return new PlacementStateBuilder<>(block, ctx);
    }

    /**
     * Set the state's {@link FramedProperties#FACING_HOR} property to the player's horizontal looking direction
     */
    public final T withHorizontalFacing()
    {
        return withHorizontalFacing(false);
    }

    /**
     * Set the state's {@link FramedProperties#FACING_HOR} property to the player's horizontal looking direction
     *
     * @param opposite Whether the direction should be inverted
     */
    public final T withHorizontalFacing(boolean opposite)
    {
        if (state == null) return self();

        Direction dir = ctx.getHorizontalDirection();
        if (opposite)
        {
            dir = dir.getOpposite();
        }
        state = state.setValue(FramedProperties.FACING_HOR, dir);
        return self();
    }

    /**
     * Set the state's {@link FramedProperties#FACING_HOR} property to the players horizontal looking direction
     * when looking at a vertical face or the inverse of the faces direction when looking at a horizontal face
     */
    public final T withTargetOrHorizontalFacing()
    {
        return withTargetOrHorizontalFacing(false);
    }

    /**
     * Set the state's {@link FramedProperties#FACING_HOR} property to the players horizontal looking direction
     * when looking at a vertical face or the inverse of the faces direction when looking at a horizontal face
     *
     * @param opposite Whether the final direction should be inverted
     */
    public final T withTargetOrHorizontalFacing(boolean opposite)
    {
        if (state == null) return self();

        Direction face = ctx.getClickedFace();
        if (!Utils.isY(face))
        {
            if (!opposite)
            {
                face = face.getOpposite();
            }
            state = state.setValue(FramedProperties.FACING_HOR, face);
            return self();
        }
        return withHorizontalFacing(opposite);
    }

    /**
     * Set the state's {@link FramedProperties#FACING_HOR} property to the inverse of the faces direction
     * when looking at a horizontal face. If the face being looked at is vertical, the builder will short-circuit all
     * subsequent modifications and return a {@code null} state from {@link #build()}
     */
    public final T withHorizontalTargetFacing()
    {
        return withHorizontalTargetFacing(false);
    }

    /**
     * Set the state's {@link FramedProperties#FACING_HOR} property to the inverse of the faces direction
     * when looking at a horizontal face. If the face being looked at is vertical, the builder will short-circuit all
     * subsequent modifications and return a {@code null} state from {@link #build()}
     *
     * @param opposite Whether the direction should be inverted
     */
    public final T withHorizontalTargetFacing(boolean opposite)
    {
        if (state == null) return self();

        Direction face = ctx.getClickedFace();
        if (Utils.isY(face))
        {
            state = null;
            return self();
        }

        if (!opposite)
        {
            face = face.getOpposite();
        }
        state = state.setValue(FramedProperties.FACING_HOR, face);
        return self();
    }

    /**
     * Set the state's {@link BlockStateProperties#FACING} property to the inverse of the face being looked at
     */
    public final T withTargetFacing()
    {
        return withTargetFacing(false);
    }

    /**
     * Set the state's {@link BlockStateProperties#FACING} property to the inverse of the face being looked at
     *
     * @param opposite Whether the direction should be inverted
     */
    public final T withTargetFacing(boolean opposite)
    {
        if (state == null) return self();

        Direction face = ctx.getClickedFace();
        if (!opposite)
        {
            face = face.getOpposite();
        }
        state = state.setValue(BlockStateProperties.FACING, face);
        return self();
    }

    /**
     * Set the state's {@link FramedProperties#FACING_HOR} property depending on the targeted horizontal half when
     * looking at a horizontal face or to the player's horizontal looking direction when looking at a vertical face
     */
    public final T withHalfOrHorizontalFacing()
    {
        if (state == null) return self();

        Direction side = ctx.getClickedFace();
        if (Utils.isY(side))
        {
            state = state.setValue(FramedProperties.FACING_HOR, ctx.getHorizontalDirection());
        }
        else if (Utils.fractionInDir(ctx.getClickLocation(), side.getCounterClockWise()) > .5)
        {
            state = state.setValue(FramedProperties.FACING_HOR, side.getOpposite().getClockWise());
        }
        else
        {
            state = state.setValue(FramedProperties.FACING_HOR, side.getOpposite());
        }
        return self();
    }

    /**
     * Set the state's {@link FramedProperties#FACING_HOR} property depending on the targeted XZ quarter when
     * looking at a vertical face or depending on the targeted horizontal half when looking at a horizontal face
     */
    public final T withHalfOrQuarterFacing()
    {
        if (state == null) return self();

        Direction side = ctx.getClickedFace();
        if (Utils.isY(side))
        {
            Vec3 hitVec = ctx.getClickLocation();
            double x = Utils.fractionInDir(hitVec, Direction.EAST);
            double z = Utils.fractionInDir(hitVec, Direction.SOUTH);

            Direction dir = z > .5D ? Direction.SOUTH : Direction.NORTH;
            if ((x > .5D) != Utils.isPositive(dir))
            {
                dir = dir.getClockWise();
            }
            state = state.setValue(FramedProperties.FACING_HOR, dir);

            return self();
        }
        return withHalfOrHorizontalFacing();
    }

    /**
     * Set the state's {@link FramedProperties#FACING_HOR} property depending on the targeted half split along the
     * horizontal looking direction when looking at a vertical face or depending on the targeted horizontal half
     * when looking at a horizontal face
     */
    public final T withHalfFacing()
    {
        if (state == null) return self();

        Direction side = ctx.getClickedFace();
        if (Utils.isY(side))
        {
            Direction dir = ctx.getHorizontalDirection();
            double xz = Utils.fractionInDir(ctx.getClickLocation(), dir.getClockWise());
            if (xz > .5D)
            {
                dir = dir.getClockWise();
            }
            state = state.setValue(FramedProperties.FACING_HOR, dir);

            return self();
        }
        return withHalfOrHorizontalFacing();
    }

    /**
     * Set the state's {@link FramedProperties#TOP} property depending on the face when looking at a vertical face or
     * depending on the targeted vertical half when looking at a horizontal face
     */
    public final T withTop()
    {
        return withTop(FramedProperties.TOP);
    }

    /**
     * Set the given property on the state depending on the face when looking at a vertical face or
     * depending on the targeted vertical half when looking at a horizontal face
     */
    public final T withTop(BooleanProperty prop)
    {
        if (state == null) return self();

        Direction side = ctx.getClickedFace();
        if (side == Direction.DOWN)
        {
            state = state.setValue(prop, true);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(prop, false);
        }
        else
        {
            double y = Utils.fractionInDir(ctx.getClickLocation(), Direction.UP);
            state = state.setValue(prop, y >= .5D);
        }
        return self();
    }

    /**
     * Set the state's {@link BlockStateProperties#WATERLOGGED} property if the state has said property
     */
    public final T tryWithWater()
    {
        if (state != null && state.hasProperty(BlockStateProperties.WATERLOGGED))
        {
            return withWater();
        }
        return self();
    }

    /**
     * Set the state's {@link BlockStateProperties#WATERLOGGED} property
     */
    public final T withWater()
    {
        if (state == null) return self();

        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        state = state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
        return self();
    }

    /**
     * Set the state's {@link FramedProperties#Y_SLOPE} property to the given value
     */
    public final T withYSlope(boolean ySlope)
    {
        if (state == null) return self();

        state = state.setValue(FramedProperties.Y_SLOPE, ySlope);
        return self();
    }

    /**
     * Apply a custom modification to the state. The given modifier can return a null state if it deems
     * the block unplaceable in the given environment, in which case the builder will short-circuit all
     * subsequent modifications and return a {@code null} state from {@link #build()}
     */
    public final T withCustom(BiFunction<BlockState, BlockPlaceContext, @Nullable BlockState> modifier)
    {
        if (state == null) return self();

        state = modifier.apply(state, ctx);
        return self();
    }

    /**
     * Validate the calculated state in the given environment. If the validator returns false, the builder will
     * short-circuit all subsequent modifications and return a {@code null} state from {@link #build()}
     */
    public final T validate(BiPredicate<BlockState, BlockPlaceContext> validator)
    {
        if (!validator.test(state, ctx))
        {
            state = null;
        }
        return self();
    }

    /**
     * Get the final state from the builder. Due to states being immutable, the builder can theoretically be
     * re-used after calling this
     */
    @Nullable
    public final BlockState build()
    {
        return state;
    }

    @SuppressWarnings("unchecked")
    protected final T self()
    {
        return (T) this;
    }
}
