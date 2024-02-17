package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_WALL)
public final class WallSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            boolean up = state.getValue(WallBlock.UP);

            return switch (type)
            {
                case FRAMED_WALL -> testAgainstWall(state, up, adjState, side);
                case FRAMED_PILLAR -> testAgainstPillar(up, adjState, side);
                case FRAMED_HALF_PILLAR -> testAgainstHalfPillar(up, adjState, side);
                case FRAMED_THICK_LATTICE -> testAgainstThickLattice(up, adjState, side);
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_WALL)
    private static boolean testAgainstWall(BlockState state, boolean up, BlockState adjState, Direction side)
    {
        if (Utils.isY(side))
        {
            boolean adjUp = adjState.getValue(WallBlock.UP);
            return up == adjUp;
        }
        else if (getArm(state, side) == getArm(adjState, side.getOpposite()))
        {
            return true;
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR)
    private static boolean testAgainstPillar(boolean up, BlockState adjState, Direction side)
    {
        return Utils.isY(side) && up && adjState.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_PILLAR)
    private static boolean testAgainstHalfPillar(boolean up, BlockState adjState, Direction side)
    {
        return Utils.isY(side) && up && adjState.getValue(BlockStateProperties.FACING) == side.getOpposite();
    }

    @CullTest.TestTarget(BlockType.FRAMED_THICK_LATTICE)
    private static boolean testAgainstThickLattice(boolean up, BlockState adjState, Direction side)
    {
        return Utils.isY(side) && up && adjState.getValue(FramedProperties.Y_AXIS);
    }



    public static WallSide getArm(BlockState state, Direction dir)
    {
        return switch (dir)
        {
            case NORTH -> state.getValue(WallBlock.NORTH_WALL);
            case EAST -> state.getValue(WallBlock.EAST_WALL);
            case SOUTH -> state.getValue(WallBlock.SOUTH_WALL);
            case WEST -> state.getValue(WallBlock.WEST_WALL);
            default -> throw new IllegalArgumentException("Invalid wall arm direction");
        };
    }
}
