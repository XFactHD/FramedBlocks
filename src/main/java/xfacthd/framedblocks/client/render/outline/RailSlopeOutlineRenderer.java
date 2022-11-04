package xfacthd.framedblocks.client.render.outline;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.FramedUtils;

public final class RailSlopeOutlineRenderer extends SlopeOutlineRenderer
{
    public static final RailSlopeOutlineRenderer INSTANCE = new RailSlopeOutlineRenderer();

    private RailSlopeOutlineRenderer() { }

    @Override
    public Direction getRotationDir(BlockState state)
    {
        return FramedUtils.getDirectionFromAscendingRailShape(state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));
    }
}
