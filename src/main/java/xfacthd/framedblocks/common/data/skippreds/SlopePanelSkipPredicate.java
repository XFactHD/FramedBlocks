package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.*;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class SlopePanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        Rotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);
        boolean front = state.getValue(PropertyHolder.FRONT);

        if (side == dir)
        {
            return !front && SideSkipPredicate.CTM.test(world, pos, state, adjState, side);
        }

        Block block = adjState.getBlock();
        if (block instanceof IFramedBlock)
        {
            switch (((IFramedBlock) block).getBlockType())
            {
                case FRAMED_SLOPE_PANEL: return testAgainstSlopePanel(world, pos, dir, rot, rotDir, front, adjState, side);
                case FRAMED_EXTENDED_SLOPE_PANEL: return testAgainstExtendedSlopePanel(world, pos, dir, rot, rotDir, front, adjState, side);
                case FRAMED_DOUBLE_SLOPE_PANEL: return testAgainstDoubleSlopePanel(world, pos, dir, rot, rotDir, front, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_PANEL: return testAgainstInverseDoubleSlopePanel(world, pos, dir, rot, rotDir, front, adjState, side);
                case FRAMED_SLAB_EDGE: return testAgainstSlabEdge(world, pos, dir, rot, rotDir, front, adjState, side);
                case FRAMED_PANEL: return testAgainstPanel(world, pos, dir, rotDir, front, adjState, side);
                case FRAMED_DOUBLE_PANEL: return testAgainstDoublePanel(world, pos, dir, rotDir, front, adjState, side);
                case FRAMED_CORNER_PILLAR: return testAgainstCornerPillar(world, pos, dir, rot, rotDir, front, adjState, side);
                case FRAMED_STAIRS: return testAgainstStairs(world, pos, dir, rot, rotDir, front, adjState, side);
                case FRAMED_VERTICAL_STAIRS: return testAgainstVerticalStairs(world, pos, dir, rot, rotDir, front, adjState, side);
                case FRAMED_HALF_STAIRS: return testAgainstHalfStairs(world, pos, dir, rot, rotDir, front, adjState, side);
                default: return false;
            }
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(IBlockReader world, BlockPos pos, Direction dir, Rotation rot, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side == rotDir.getOpposite() && adjRot == rot.getOpposite() && ((adjDir == dir && adjFront == front) || (adjDir == dir.getOpposite() && adjFront != front)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side.getAxis() != dir.getAxis() && side.getAxis() != rotDir.getAxis() && adjDir == dir && adjRot == rot && adjFront == front)
        {
            return SideSkipPredicate.compareState(world, pos, side, rotDir.getOpposite(), rotDir);
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(IBlockReader world, BlockPos pos, Direction dir, Rotation rot, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        if (side != rotDir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjRot == rot && ((adjDir == dir && !front) || (adjDir == dir.getOpposite() && front)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(IBlockReader world, BlockPos pos, Direction dir, Rotation rot, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side == rotDir.getOpposite() && (adjRot == rot || adjRot == rot.getOpposite()) && ((adjDir == dir && adjFront == front) || (adjDir == dir.getOpposite() && adjFront != front)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side.getAxis() != dir.getAxis() && side.getAxis() != rotDir.getAxis())
        {
            if ((adjDir == dir && adjRot == rot && adjFront == front) || (adjDir == dir.getOpposite() && adjRot == rot.getOpposite() && adjFront != front))
            {
                return SideSkipPredicate.compareState(world, pos, side, rotDir.getOpposite(), rotDir.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(IBlockReader world, BlockPos pos, Direction dir, Rotation rot, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        if (side != rotDir.getOpposite() && (side.getAxis() == dir.getAxis() || (side.getAxis() == rotDir.getAxis()))) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side == rotDir.getOpposite() && adjRot == rot && ((adjDir == dir && !front) || (adjDir == dir.getOpposite() && front)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side != rotDir.getOpposite() && front && ((adjDir == dir && adjRot == rot) || (adjDir == dir.getOpposite() && adjRot == rot.getOpposite())))
        {
            return SideSkipPredicate.compareState(world, pos, side, rotDir.getOpposite(), rotDir.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(IBlockReader world, BlockPos pos, Direction dir, Rotation rot, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        if (!rot.isVertical() || side != rotDir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop == (rot == Rotation.UP) && ((adjDir == dir && !front) || (adjDir == dir.getOpposite() && front)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        if (side != rotDir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if ((adjDir == dir && !front) || (adjDir == dir.getOpposite() && front))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        if (side != rotDir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);

        return (adjDir == dir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side, side, front ? dir.getOpposite() : dir);
    }

    private static boolean testAgainstCornerPillar(IBlockReader world, BlockPos pos, Direction dir, Rotation rot, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        if (rot.isVertical() || side != rotDir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if ((!front && (adjDir == dir || adjDir == dir.getClockWise())) || (front && (adjDir == dir.getOpposite() || adjDir == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, Rotation rot, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        if (!rot.isVertical() || side != rotDir.getOpposite()) { return false; }
        if (adjState.getValue(StairsBlock.SHAPE) != StairsShape.STRAIGHT) { return false; }

        Direction adjDir = adjState.getValue(StairsBlock.FACING);
        boolean adjTop = adjState.getValue(StairsBlock.HALF) == Half.TOP;

        if ((!front && adjDir == dir) || (front && adjDir == dir.getOpposite()))
        {
            return adjTop == (rot == Rotation.DOWN) && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, Rotation rot, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        if (rot.isVertical() || side != rotDir.getOpposite()) { return false; }
        if (adjState.getValue(PropertyHolder.STAIRS_TYPE) != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if (!front && ((adjDir == dir && rot == Rotation.RIGHT) || (adjDir == dir.getClockWise() && rot == Rotation.LEFT)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (front && ((adjDir == dir.getOpposite() && rot == Rotation.LEFT) || (adjDir == dir.getCounterClockWise() && rot == Rotation.RIGHT)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(IBlockReader world, BlockPos pos, Direction dir, Rotation rot, Direction rotDir, boolean front, BlockState adjState, Direction side)
    {
        if (side != rotDir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (rot.isVertical())
        {
            if (adjState.getValue(PropertyHolder.TOP) != (rot == Rotation.UP)) { return false; }

            if (!adjRight && ((adjDir == dir.getClockWise() && !front) || (adjDir == dir.getCounterClockWise() && front)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjRight && ((adjDir == dir.getCounterClockWise() && !front) || (adjDir == dir.getClockWise() && front)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        else if (adjDir == side.getOpposite())
        {
            if (!adjRight && ((adjDir == dir.getClockWise() && !front) || (adjDir == dir.getCounterClockWise() && front)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjRight && ((adjDir == dir.getCounterClockWise() && !front) || (adjDir == dir.getClockWise() && front)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }

        return false;
    }
}
