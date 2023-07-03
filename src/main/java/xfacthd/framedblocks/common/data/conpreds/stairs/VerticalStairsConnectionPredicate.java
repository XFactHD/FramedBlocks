package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class VerticalStairsConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        boolean topEdge = type != StairsType.TOP_CORNER;
        boolean botEdge = type != StairsType.BOTTOM_CORNER;
        boolean vert = topEdge && botEdge;

        if ((topEdge && side == Direction.UP) || (botEdge && side == Direction.DOWN))
        {
            return edge == facing || edge == facing.getCounterClockWise();
        }
        else if (side == facing)
        {
            if (edge == facing.getCounterClockWise() || (vert && edge == facing.getClockWise()))
            {
                return true;
            }
            return (topEdge && edge == Direction.UP) || (botEdge && edge == Direction.DOWN);
        }
        else if (side == facing.getCounterClockWise())
        {
            if (edge == facing || (vert && edge == facing.getOpposite()))
            {
                return true;
            }
            return (topEdge && edge == Direction.UP) || (botEdge && edge == Direction.DOWN);
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        boolean topEdge = type != StairsType.TOP_CORNER;
        boolean botEdge = type != StairsType.BOTTOM_CORNER;

        if (side == Direction.UP)
        {
            return !topEdge || edge == facing.getOpposite() || edge == facing.getClockWise();
        }
        else if (side == Direction.DOWN)
        {
            return !botEdge || edge == facing.getOpposite() || edge == facing.getClockWise();
        }
        else if (side == facing && type != StairsType.VERTICAL)
        {
            return edge == facing.getClockWise() || (!topEdge && edge == Direction.UP) || (!botEdge && edge == Direction.DOWN);
        }
        else if (side == facing.getCounterClockWise() && type != StairsType.VERTICAL)
        {
            return edge == facing.getOpposite() || (!topEdge && edge == Direction.UP) || (!botEdge && edge == Direction.DOWN);
        }
        else if (side == facing.getOpposite())
        {
            if ((topEdge && edge == Direction.UP) || (botEdge && edge == Direction.DOWN))
            {
                return true;
            }
            return type != StairsType.VERTICAL || edge == facing.getClockWise();
        }
        else if (side == facing.getClockWise())
        {
            if ((topEdge && edge == Direction.UP) || (botEdge && edge == Direction.DOWN))
            {
                return true;
            }
            return type != StairsType.VERTICAL || edge == facing.getOpposite();
        }
        return false;
    }
}
