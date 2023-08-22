package xfacthd.framedblocks.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.FramedUtils;

public interface ISlopeBlock
{
    Direction getFacing(BlockState state);

    SlopeType getSlopeType(BlockState state);

    interface IRailSlopeBlock extends ISlopeBlock
    {
        @Override
        default Direction getFacing(BlockState state)
        {
            RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
            return FramedUtils.getDirectionFromAscendingRailShape(shape);
        }

        @Override
        default SlopeType getSlopeType(BlockState state)
        {
            return SlopeType.BOTTOM;
        }
    }
}
