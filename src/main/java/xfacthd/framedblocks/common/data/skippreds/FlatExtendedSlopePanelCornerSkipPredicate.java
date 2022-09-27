package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class FlatExtendedSlopePanelCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);

        if (side == dir)
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        HorizontalRotation perpRot = rot.rotate(Rotation.COUNTERCLOCKWISE_90);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = perpRot.withFacing(dir);

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        level, pos, dir, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerSlopePanelCorner(
                        level, pos, dir, rot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        level, pos, dir, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        level, pos, dir, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        level, pos, dir, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        level, pos, dir, rot, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        level, pos, dir, rot, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir != dir) { return false; }

        if (isPanelSide(dir, rot, side) && isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        if (side == perpRotDir.getOpposite() && adjRot == rot.rotate(Rotation.CLOCKWISE_90))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (side == rotDir.getOpposite() && adjRot == perpRot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir == dir && adjRot == rot && (side == rotDir.getOpposite() || side == perpRotDir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            if ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side)
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            if ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot,
            BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (adjRot.withFacing(adjDir) == side && ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir != dir) { return false; }

        if (isPanelSide(dir, rot, side) && adjRot.withFacing(adjDir) == side.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        if (side == rotDir.getOpposite() && adjRot == perpRot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (side == perpRotDir.getOpposite() && adjRot == rot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (adjRot.withFacing(adjDir).getAxis() == side.getAxis() && ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side == rotDir)
        {
            if (adjDir == dir && rot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
            else if (adjDir == dir.getOpposite() && rot.isSameDir(dir, adjRot, adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        else if (side == perpRotDir)
        {
            if (adjDir == dir && perpRot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
            else if (adjDir == dir.getOpposite() && perpRot.isSameDir(dir, adjRot, adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (isPanelSide(dir, rot, side) && adjDir.getAxis() == dir.getAxis() && adjRot.withFacing(adjDir) == side.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        if (adjDir != dir) { return false; }

        if (side == rotDir.getOpposite() && adjRot == perpRot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (side == perpRotDir.getOpposite() && adjRot == rot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!Utils.isY(side) || !isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjDir == dir && ((side == Direction.DOWN && adjTop) || (side == Direction.UP && !adjTop)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstPanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        return adjDir == dir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstDoublePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_NE);

        return adjDir.getAxis() == dir.getAxis() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstCornerPillar(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (Utils.isY(side) || !isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;

        if (adjShape != StairsShape.STRAIGHT || adjDir != dir) { return false; }

        if ((side == Direction.DOWN && !adjTop) || (side == Direction.UP && adjTop))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstDoubleStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjDir.getAxis() == dir.getAxis() && ((side == Direction.DOWN && !adjTop) || (side == Direction.UP && adjTop)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (adjType != StairsType.VERTICAL) { return false; }

        if ((side == dir.getClockWise() && adjDir == dir.getClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (side == dir.getClockWise() && (adjDir == dir.getClockWise() || adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (side == dir.getCounterClockWise() && (adjDir == dir || adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if ((side == Direction.UP && !adjTop) || (side == Direction.DOWN && adjTop))
        {
            if ((adjDir == dir.getClockWise() && !adjRight) || (adjDir == dir.getCounterClockWise() && adjRight))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if (!Utils.isY(side))
        {
            if (side == dir.getClockWise() && adjDir == dir.getCounterClockWise() && adjRight)
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (side == dir.getCounterClockWise() && adjDir == dir.getClockWise() && !adjRight)
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }

        return false;
    }



    public static boolean isPanelSide(Direction dir, HorizontalRotation rot, Direction side)
    {
        return side == rot.withFacing(dir) || side == rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
    }
}
