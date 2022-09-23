package xfacthd.framedblocks.client.render.outline;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.block.FramedRailSlopeBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class RailSlopeOutlineRenderer extends SlopeOutlineRenderer
{
    @Override
    public Direction getRotationDir(BlockState state)
    {
        return FramedRailSlopeBlock.directionFromShape(state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));
    }
}
