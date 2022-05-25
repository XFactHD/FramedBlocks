package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.StairsType;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class PanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        if (side == dir) { return SideSkipPredicate.CTM.test(world, pos, state, adjState, side); }

        if (adjState.getBlock() == FBContent.blockFramedPanel.get())
        {
            return testAgainstPanel(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedDoublePanel.get())
        {
            return testAgainstDoublePanel(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedCornerPillar.get())
        {
            return testAgainstPillar(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get())
        {
            return testAgainstEdge(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            return testAgainstStairs(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedVerticalStairs.get())
        {
            return testAgainstVerticalStairs(world, pos, dir, adjState, side);
        }

        if (adjState.is(FBContent.blockFramedHalfStairs.get()))
        {
            return testAgainstHalfStairs(world, pos, dir, adjState, side);
        }

        if (adjState.is(FBContent.blockFramedSlopePanel.get()))
        {
            return testAgainstSlopePanel(world, pos, dir, adjState, side);
        }
        
        if (adjState.is(FBContent.blockFramedExtendedSlopePanel.get()))
        {
            return testAgainstExtendedSlopePanel(world, pos, dir, adjState, side);
        }
        
        if (adjState.is(FBContent.blockFramedDoubleSlopePanel.get()))
        {
            return testAgainstDoubleSlopePanel(world, pos, dir, adjState, side);
        }
        
        if (adjState.is(FBContent.blockFramedInverseDoubleSlopePanel.get()))
        {
            return testAgainstInverseDoubleSlopePanel(world, pos, dir, adjState, side);
        } 

        return false;
    }

    private boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        return dir == adjState.getValue(PropertyHolder.FACING_HOR) && SideSkipPredicate.compareState(world, pos, side, dir);
    }

    private boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);
        return (dir == adjDir || dir == adjDir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side, dir);
    }

    private boolean testAgainstPillar(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstEdge(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if (adjDir != dir) { return false; }

        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        if ((side == Direction.UP && !adjTop) || (side == Direction.DOWN && adjTop))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (dir != adjDir) { return false; }

        if ((side == Direction.UP && adjTop) || (side == Direction.DOWN && !adjTop))
        {
            return adjShape == StairsShape.STRAIGHT && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if ((side == dir.getCounterClockWise() && adjDir == dir) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return adjType == StairsType.VERTICAL && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private static boolean testAgainstHalfStairs(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (side.getAxis() == dir.getAxis()) { return false; }

        if ((adjRight && adjDir == dir.getCounterClockWise()) || (!adjRight && adjDir == dir.getClockWise()))
        {
            if (side.getAxis() == Direction.Axis.Y)
            {
                return (side == Direction.DOWN) == adjTop && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else
            {
                return (side == adjDir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Direction adjRotDir = adjState.getValue(PropertyHolder.ROTATION).withFacing(adjDir);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side.getAxis() == dir.getAxis() || side != adjRotDir) { return false; }

        if ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side.getAxis() == dir.getAxis()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Direction adjRotDir = adjState.getValue(PropertyHolder.ROTATION).withFacing(adjDir);

        return adjDir == dir && adjRotDir == side.getOpposite() && SideSkipPredicate.compareState(world, pos, side, dir);
    }

    private static boolean testAgainstDoubleSlopePanel(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Direction adjRotDir = adjState.getValue(PropertyHolder.ROTATION).withFacing(adjDir);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side.getAxis() == dir.getAxis() || side.getAxis() != adjRotDir.getAxis()) { return false; }

        if ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Direction adjRotDir = adjState.getValue(PropertyHolder.ROTATION).withFacing(adjDir);

        if (side.getAxis() == dir.getAxis() || side.getAxis() != adjRotDir.getAxis()) { return false; }

        if ((adjDir == dir && side == adjRotDir.getOpposite()) || (adjDir == dir.getOpposite() && side == adjRotDir))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir, side.getOpposite());
        }

        return false;
    }
}