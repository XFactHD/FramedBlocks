package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.property.StairsType;

public class ExtendedSlopePanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);

        if (side == dir || side == rotDir.getOpposite())
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_PANEL -> testAgainstPanel(level, pos, adjState, side, dir, rotDir);
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, adjState, side, dir, rotDir);
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(level, pos, adjState, side, dir, rot, rotDir);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(level, pos, adjState, side, dir, rot, rotDir);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side == rotDir && adjDir == dir && adjRot == rot.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (side != dir.getOpposite() && adjDir == dir && adjRot == rot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (adjRot == rot && ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if ((adjRot == rot || adjRot == rot.getOpposite()) && ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjRot == rot.getOpposite() && adjDir.getAxis() == dir.getAxis())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side == rotDir)
        {
            if (((adjRot == rot && adjDir == dir.getOpposite()) || (adjRot == rot.getOpposite() && adjDir == dir)))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if (side.getAxis() != rotDir.getAxis() && side.getAxis() != dir.getAxis())
        {
            if (adjDir == dir && adjRot == rot)
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        if (!rot.isVertical() || side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjDir == dir && adjTop == (rot == HorizontalRotation.DOWN) && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
    }

    private static boolean testAgainstPanel(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        return adjDir == dir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_NE);

        return (adjDir == dir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstCornerPillar(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        if (rot.isVertical() || side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((rot == HorizontalRotation.RIGHT && adjDir == dir) || (rot == HorizontalRotation.LEFT && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        if (!rot.isVertical() || side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(StairBlock.FACING);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;
        boolean straight = adjState.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT;

        return straight && adjDir == dir && adjTop == (rot == HorizontalRotation.UP) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstDoubleStairs(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        if (!rot.isVertical() || side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(StairBlock.FACING);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjDir.getAxis() == dir.getAxis() && adjTop == (rot == HorizontalRotation.UP) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        if (rot.isVertical() || side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjVert = adjState.getValue(PropertyHolder.STAIRS_TYPE) == StairsType.VERTICAL;

        if (adjVert && ((rot == HorizontalRotation.RIGHT && adjDir == dir.getClockWise()) || (rot == HorizontalRotation.LEFT && adjDir == dir)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        if (rot.isVertical() || side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (rot == HorizontalRotation.RIGHT && (adjDir == dir.getClockWise() || adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (rot == HorizontalRotation.LEFT && (adjDir == dir || adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction dir, HorizontalRotation rot, Direction rotDir)
    {
        if (side != rotDir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (rot.isVertical() && adjTop == (rot == HorizontalRotation.DOWN) && ((!adjRight && adjDir == dir.getClockWise()) || (adjRight && adjDir == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (!rot.isVertical() && ((adjDir == dir.getCounterClockWise() && adjRight) || adjDir == dir.getClockWise() && !adjRight))
        {
            return adjRight == (rot == HorizontalRotation.RIGHT) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }
}
