package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_POST)
public final class PostSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (side.getAxis() == axis && adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_POST -> testAgainstPost(axis, adjState);
                case FRAMED_FENCE -> testAgainstFence(axis);
                case FRAMED_LATTICE_BLOCK -> testAgainstLattice(axis, adjState);
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_POST)
    private static boolean testAgainstPost(Direction.Axis axis, BlockState adjState)
    {
        return axis == adjState.getValue(BlockStateProperties.AXIS);
    }

    @CullTest.TestTarget(BlockType.FRAMED_FENCE)
    private static boolean testAgainstFence(Direction.Axis axis)
    {
        return axis == Direction.Axis.Y;
    }

    @CullTest.TestTarget(BlockType.FRAMED_LATTICE_BLOCK)
    private static boolean testAgainstLattice(Direction.Axis axis, BlockState adjState)
    {
        return switch (axis)
        {
            case X -> adjState.getValue(FramedProperties.X_AXIS);
            case Y -> adjState.getValue(FramedProperties.Y_AXIS);
            case Z -> adjState.getValue(FramedProperties.Z_AXIS);
        };
    }
}