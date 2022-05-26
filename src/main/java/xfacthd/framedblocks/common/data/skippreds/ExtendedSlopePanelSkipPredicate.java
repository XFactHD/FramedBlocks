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

public class ExtendedSlopePanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        Rotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);

        if (side == dir || side == rotDir.getOpposite())
        {
            return SideSkipPredicate.CTM.test(world, pos, state, adjState, side);
        }

        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }
        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();

        switch (adjBlock)
        {
            case FRAMED_EXTENDED_SLOPE_PANEL: return testAgainstExtendedSlopePanel(world, pos, adjState, side, dir, rot, rotDir);
            case FRAMED_SLOPE_PANEL: return testAgainstSlopePanel(world, pos, adjState, side, dir, rot, rotDir);
            case FRAMED_DOUBLE_SLOPE_PANEL: return testAgainstDoubleSlopePanel(world, pos, adjState, side, dir, rot, rotDir);
            case FRAMED_INV_DOUBLE_SLOPE_PANEL: return testAgainstInverseDoubleSlopePanel(world, pos, adjState, side, dir, rot, rotDir);
            case FRAMED_SLAB_EDGE: return testAgainstSlabEdge(world, pos, adjState, side, dir, rot, rotDir);
            case FRAMED_PANEL: return testAgainstPanel(world, pos, adjState, side, dir, rotDir);
            case FRAMED_DOUBLE_PANEL: return testAgainstDoublePanel(world, pos, adjState, side, dir, rotDir);
            case FRAMED_CORNER_PILLAR: return testAgainstCornerPillar(world, pos, adjState, side, dir, rot, rotDir);
            case FRAMED_STAIRS: return testAgainstStairs(world, pos, adjState, side, dir, rot, rotDir);
            case FRAMED_VERTICAL_STAIRS: return testAgainstVerticalStairs(world, pos, adjState, side, dir, rot, rotDir);
            case FRAMED_HALF_STAIRS: return testAgainstHalfStairs(world, pos, adjState, side, dir, rot, rotDir);
            default: return false;
        }
    }

    private static boolean testAgainstExtendedSlopePanel(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Rotation rot, Direction rotDir)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side == rotDir && adjDir == dir && adjRot == rot.getOpposite())
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side != dir.getOpposite() && adjDir == dir && adjRot == rot)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Rotation rot, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (adjRot == rot && ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Rotation rot, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if ((adjRot == rot || adjRot == rot.getOpposite()) && ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Rotation rot, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if ((adjRot == rot && adjDir == dir.getOpposite()) || (adjRot == rot.getOpposite() && adjDir == dir))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Rotation rot, Direction rotDir)
    {
        if (!rot.isVertical() || side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        return adjDir == dir && adjTop == (rot == Rotation.DOWN) && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstPanel(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        return adjDir == dir && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);

        return (adjDir == dir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side, dir, dir);
    }

    private static boolean testAgainstCornerPillar(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Rotation rot, Direction rotDir)
    {
        if (rot.isVertical() || side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if ((rot == Rotation.RIGHT && adjDir == dir) || (rot == Rotation.LEFT && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Rotation rot, Direction rotDir)
    {
        if (!rot.isVertical() || side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(StairsBlock.FACING);
        boolean adjTop = adjState.getValue(StairsBlock.HALF) == Half.TOP;
        boolean straight = adjState.getValue(StairsBlock.SHAPE) == StairsShape.STRAIGHT;

        return straight && adjDir == dir && adjTop == (rot == Rotation.UP) && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Rotation rot, Direction rotDir)
    {
        if (rot.isVertical() || side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjVert = adjState.getValue(PropertyHolder.STAIRS_TYPE) == StairsType.VERTICAL;

        if (adjVert && ((rot == Rotation.RIGHT && adjDir == dir.getClockWise()) || (rot == Rotation.LEFT && adjDir == dir)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction dir, Rotation rot, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (rot.isVertical() && adjTop == (rot == Rotation.DOWN) && ((!adjRight && adjDir == dir.getClockWise()) || (adjRight && adjDir == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (!rot.isVertical() && ((adjDir == dir.getCounterClockWise() && adjRight) || adjDir == dir.getClockWise() && !adjRight))
        {
            return adjRight == (rot == Rotation.RIGHT) && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }
}
