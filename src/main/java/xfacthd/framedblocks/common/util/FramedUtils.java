package xfacthd.framedblocks.common.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.block.FramedRailSlopeBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.SlopeType;

public class FramedUtils
{
    public static Direction getBlockFacing(BlockState state)
    {
        if (state.getBlock() instanceof FramedRailSlopeBlock)
        {
            return FramedRailSlopeBlock.directionFromShape(state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));
        }
        return state.getValue(PropertyHolder.FACING_HOR);
    }

    public static SlopeType getSlopeType(BlockState state)
    {
        if (state.getBlock() instanceof FramedRailSlopeBlock)
        {
            return SlopeType.BOTTOM;
        }
        return state.getValue(PropertyHolder.SLOPE_TYPE);
    }
}