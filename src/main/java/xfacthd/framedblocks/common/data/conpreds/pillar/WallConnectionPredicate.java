package xfacthd.framedblocks.common.data.conpreds.pillar;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Map;

public final class WallConnectionPredicate implements ConnectionPredicate
{
    private static final Map<Direction, Property<WallSide>> WALL_PROPERTIES = Map.of(
            Direction.NORTH, WallBlock.NORTH_WALL,
            Direction.EAST, WallBlock.EAST_WALL,
            Direction.SOUTH, WallBlock.SOUTH_WALL,
            Direction.WEST, WallBlock.WEST_WALL
    );

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        if (Utils.isY(side) || !Utils.isY(edge))
        {
            Property<WallSide> prop = WALL_PROPERTIES.get(edge);
            return state.getValue(prop) != WallSide.NONE;
        }
        else if (edge == Direction.UP)
        {
            if (state.getValue(WallBlock.UP))
            {
                return true;
            }

            Property<WallSide> propA = WALL_PROPERTIES.get(side.getClockWise());
            Property<WallSide> propB = WALL_PROPERTIES.get(side.getCounterClockWise());
            return state.getValue(propA) == WallSide.TALL || state.getValue(propB) == WallSide.TALL;
        }
        else if (edge == Direction.DOWN)
        {
            return true;
        }
        return false;
    }
}
