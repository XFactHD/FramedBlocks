package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;

public final class LatticeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() == state.getBlock() && hasArm(state, side) && hasArm(adjState, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean hasArm(BlockState state, Direction side)
    {
        return switch (side.getAxis())
        {
            case X -> state.getValue(FramedProperties.X_AXIS);
            case Y -> state.getValue(FramedProperties.Y_AXIS);
            case Z -> state.getValue(FramedProperties.Z_AXIS);
        };
    }
}
