package xfacthd.framedblocks.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.*;

public final class ExtPlacementStateBuilder extends PlacementStateBuilder<ExtPlacementStateBuilder>
{
    private ExtPlacementStateBuilder(Block block, BlockPlaceContext ctx)
    {
        super(block, ctx);
    }

    public static ExtPlacementStateBuilder of(Block block, BlockPlaceContext ctx)
    {
        return new ExtPlacementStateBuilder(block, ctx);
    }

    public ExtPlacementStateBuilder withRight()
    {
        if (state == null) return self();

        boolean right = Utils.fractionInDir(ctx.getClickLocation(), ctx.getHorizontalDirection().getClockWise()) > .5D;
        state = state.setValue(PropertyHolder.RIGHT, right);
        return self();
    }

    public ExtPlacementStateBuilder withHorizontalFacingAndSlopeType()
    {
        if (state == null) return self();

        Direction side = ctx.getClickedFace();
        Direction facing = ctx.getHorizontalDirection();

        state = state.setValue(FramedProperties.FACING_HOR, facing);

        if (!Utils.isY(side))
        {
            double y = Utils.fractionInDir(ctx.getClickLocation(), Direction.UP);
            if (y < (3D / 16D))
            {
                side = Direction.UP;
            }
            else if (y > (13D / 16D))
            {
                side = Direction.DOWN;
            }
        }

        if (side == Direction.DOWN)
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.BOTTOM);
        }
        else
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
            withHalfOrHorizontalFacing();
        }

        return self();
    }

    public ExtPlacementStateBuilder withHorizontalFacingAndCornerType()
    {
        if (state == null) return self();

        Direction side = ctx.getClickedFace();
        Vec3 hitPoint = Utils.fraction(ctx.getClickLocation());

        Direction typeSide = side;
        if (!Utils.isY(side))
        {
            if (hitPoint.y() < (3D / 16D))
            {
                typeSide = Direction.UP;
            }
            else if (hitPoint.y() > (13D / 16D))
            {
                typeSide = Direction.DOWN;
            }
        }

        if (typeSide == Direction.DOWN)
        {
            state = state.setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
            withHalfFacing();
        }
        else if (typeSide == Direction.UP)
        {
            state = state.setValue(PropertyHolder.CORNER_TYPE, CornerType.BOTTOM);
            withHalfFacing();
        }
        else
        {
            boolean xAxis = Utils.isX(side);
            boolean positive = Utils.isPositive(side.getCounterClockWise());
            double xz = xAxis ? hitPoint.z() : hitPoint.x();
            double y = hitPoint.y();

            CornerType type;
            if ((xz > .5D) == positive)
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_RIGHT : CornerType.HORIZONTAL_BOTTOM_RIGHT;
            }
            else
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_LEFT : CornerType.HORIZONTAL_BOTTOM_LEFT;
            }

            state = state.setValue(PropertyHolder.CORNER_TYPE, type)
                    .setValue(FramedProperties.FACING_HOR, ctx.getHorizontalDirection());
        }

        return self();
    }

    public ExtPlacementStateBuilder withCornerOrSideRotation()
    {
        return withCornerOrSideRotation(false);
    }

    public ExtPlacementStateBuilder withCornerOrSideRotation(boolean opposite)
    {
        if (state == null) return self();

        Direction facing = ctx.getHorizontalDirection();
        Direction side = ctx.getClickedFace();
        if (side == facing)
        {
            // Protect against nonsensical context data
            state = null;
            return self();
        }

        HorizontalRotation rotation;
        if (side == facing.getOpposite())
        {
            rotation = HorizontalRotation.fromWallCorner(ctx.getClickLocation(), side);
        }
        else
        {
            rotation = HorizontalRotation.fromDirection(facing, side, ctx.getClickLocation());
        }
        if (opposite)
        {
            rotation = rotation.getOpposite();
        }
        state = state.setValue(PropertyHolder.ROTATION, rotation);

        return self();
    }

    public ExtPlacementStateBuilder withCrossOrSideRotation()
    {
        return withCrossOrSideRotation(false);
    }

    public ExtPlacementStateBuilder withCrossOrSideRotation(boolean opposite)
    {
        if (state == null) return self();

        Direction facing = ctx.getHorizontalDirection();
        Direction side = ctx.getClickedFace();
        if (side == facing)
        {
            // Protect against nonsensical context data
            state = null;
            return self();
        }

        HorizontalRotation rotation;
        if (side == facing.getOpposite())
        {
            rotation = HorizontalRotation.fromWallCross(ctx.getClickLocation(), side);
        }
        else
        {
            rotation = HorizontalRotation.fromDirection(facing, side);
        }
        if (opposite)
        {
            rotation = rotation.getOpposite();
        }
        state = state.setValue(PropertyHolder.ROTATION, rotation);

        return self();
    }

    public ExtPlacementStateBuilder withCornerRotation()
    {
        return withCornerRotation(false);
    }

    public ExtPlacementStateBuilder withCornerRotation(boolean opposite)
    {
        if (state == null) return self();

        HorizontalRotation rotation = HorizontalRotation.fromWallCorner(ctx.getClickLocation(), ctx.getClickedFace());
        if (opposite)
        {
            rotation = rotation.getOpposite();
        }
        state = state.setValue(PropertyHolder.ROTATION, rotation);

        return self();
    }

    public ExtPlacementStateBuilder withCrossRotation()
    {
        return withCrossRotation(false);
    }

    public ExtPlacementStateBuilder withCrossRotation(boolean opposite)
    {
        if (state == null) return self();

        HorizontalRotation rotation = HorizontalRotation.fromWallCross(ctx.getClickLocation(), ctx.getClickedFace());
        if (opposite)
        {
            rotation = rotation.getOpposite();
        }
        state = state.setValue(PropertyHolder.ROTATION, rotation);

        return self();
    }

    public ExtPlacementStateBuilder withFront()
    {
        if (state == null) return self();

        Direction facing = ctx.getHorizontalDirection();
        if (facing.getAxis() != ctx.getClickedFace().getAxis())
        {
            double xz = Utils.fractionInDir(ctx.getClickLocation(), facing);
            state = state.setValue(PropertyHolder.FRONT, xz < .5);
        }

        return self();
    }
}
