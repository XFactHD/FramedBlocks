package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.api.util.*;

public final class FlatInnerSlopePanelCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean front = state.getValue(PropertyHolder.FRONT);

        if (side == dir)
        {
            return !front && SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        HorizontalRotation perpRot = rot.rotate(Rotation.COUNTERCLOCKWISE_90);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = perpRot.withFacing(dir);

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        level, pos, dir, front, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_SLOPE_PANEL_CORNER -> testAgainstFlatSlopePanelCorner(
                        level, pos, dir, front, rot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        level, pos, dir, front, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(
                        level, pos, dir, front, rot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        level, pos, dir, front, rot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerDoubleSlopePanelCorner(
                        level, pos, dir, front, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        level, pos, dir, front, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        level, pos, dir, front, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        level, pos, dir, front, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        level, pos, dir, front, rot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_HALF_STAIRS, FRAMED_HALF_SLOPE -> testAgainstHalfStairs(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        level, pos, dir, front, rot, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        level, pos, dir, front, rot, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (isPanelSide(dir, rot, side) && isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            if ((adjDir == dir && adjFront == front) || (adjDir == dir.getOpposite() && adjFront != front))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
            return false;
        }

        if (adjDir != dir || adjFront != front) { return false; }

        if ((side == rotDir && adjRot == perpRot) || side == perpRotDir && adjRot == rot.rotate(Rotation.CLOCKWISE_90))
        {
            return SideSkipPredicate.compareState(level, pos, side, side.getOpposite(), side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstFlatSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir == dir && adjFront == front && adjRot == rot && (side == rotDir || side == perpRotDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, side.getOpposite(), side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (FlatExtendedSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            if ((adjDir == dir && !front) || (adjDir == dir.getOpposite() && front))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (isPanelSide(dir, rot, side) && isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            if ((adjDir == dir && adjFront == front) || (adjDir == dir.getOpposite() && adjFront != front))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
            return false;
        }

        if (side == rotDir)
        {
            if (adjDir == dir && adjFront == front && adjRot == perpRot)
            {
                return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), adjDir);
            }
            else if (adjDir == dir.getOpposite() && adjFront != front && perpRot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), adjDir.getOpposite());
            }
        }
        else if (side == perpRotDir)
        {
            if (adjDir == dir && adjFront == front && adjRot == rot.rotate(Rotation.CLOCKWISE_90))
            {
                return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), adjDir);
            }
            else if (adjDir == dir.getOpposite() && adjFront != front && perpRot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), adjDir.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (isPanelSide(dir, rot, side) && isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            if ((adjDir == dir && !front) || (adjDir == dir.getOpposite() && front))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
            return false;
        }

        if (!front || (side != rotDir && side != perpRotDir)) { return false; }

        if (adjDir == dir && rot.isSameDir(dir, adjRot.getOpposite(), adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, side.getOpposite(), adjDir.getOpposite());
        }
        else if (adjDir == dir.getOpposite())
        {
            if (side == rotDir && rot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), adjDir);
            }
            else if (side == perpRotDir && rot.isSameDir(dir, adjRot, adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), adjDir);
            }
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (FlatExtendedSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()) && isPanelSide(dir, rot, side))
        {
            Direction camoSide = front ? dir.getOpposite() : dir;
            return adjDir.getAxis() == dir.getAxis() && SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        if (adjDir != dir.getOpposite() || front) { return false; }

        if (side == rotDir && rot.isSameDir(dir, adjRot, adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, side, adjDir.getOpposite());
        }
        else if (side == perpRotDir && rot.isSameDir(dir, adjRot.getOpposite(), adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, side, adjDir.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedInnerDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        if ((side != rotDir && side != perpRotDir) || front) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir == dir.getOpposite() && perpRot.isSameDir(dir, adjRot.getOpposite(), adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, side, adjDir.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (isPanelSide(dir, rot, side) && side == adjRot.withFacing(adjDir))
        {
            if ((adjDir == dir && adjFront == front) || (adjDir == dir.getOpposite() && adjFront != front))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
            return false;
        }

        if (adjDir != dir || adjFront != front) { return false; }

        if ((side == rotDir && adjRot == perpRot) || (side == perpRotDir && adjRot == rot))
        {
            return SideSkipPredicate.compareState(level, pos, side, side.getOpposite(), side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (isPanelSide(dir, rot, side) && adjRot.withFacing(adjDir) == side.getOpposite())
        {
            if ((adjDir == dir && !front) || (adjDir == dir.getOpposite() && front))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        Direction adjRotDir = adjRot.withFacing(adjDir);

        if (isPanelSide(dir, rot, side) && (side == adjRotDir || side == adjRotDir.getOpposite()))
        {
            if ((adjDir == dir && adjFront == front) || (adjDir == dir.getOpposite() && adjFront != front))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        if (side == rotDir)
        {
            if (adjDir == dir && adjFront == front && perpRot.isSameDir(dir, adjRot, adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), camoSide);
            }
            else if (adjDir == dir.getOpposite() && adjFront != front &&  perpRot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                Direction camoSide = adjRot.getOpposite().withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), camoSide);
            }
        }
        else if (side == perpRotDir)
        {
            if (adjDir == dir && adjFront == front && rot.isSameDir(dir, adjRot, adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), camoSide);
            }
            else if (adjDir == dir.getOpposite() && adjFront != front && rot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                Direction camoSide = adjRot.getOpposite().withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), camoSide);
            }
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        Direction adjRotDir = adjRot.withFacing(adjDir);

        if (isPanelSide(dir, rot, side))
        {
            if (((adjDir == dir && !front) || (adjDir == dir.getOpposite() && front)) && adjRotDir == side.getOpposite())
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
            else if (((adjDir == dir.getOpposite() && !front) || (adjDir == dir && front)) && adjRotDir == side)
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        if (!front) { return false; }

        if (side == rotDir)
        {
            if (adjDir == dir && perpRot.isSameDir(dir, adjRot, adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), camoSide);
            }
            else if (adjDir == dir.getOpposite() && perpRot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir);
                return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), camoSide);
            }
        }
        else if (side == perpRotDir)
        {
            if (adjDir == dir && rot.isSameDir(dir, adjRot, adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), camoSide);
            }
            else if (adjDir == dir.getOpposite() && rot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir);
                return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), camoSide);
            }
        }

        return false;
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        Direction adjRotDir = adjRot.withFacing(adjDir);

        if (isPanelSide(dir, rot, side) && adjRotDir == side.getOpposite() && (adjDir == dir || adjDir == dir.getOpposite()))
        {
            Direction camoSide = front ? dir.getOpposite() : dir;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        if (side == rotDir && rot.rotate(Rotation.CLOCKWISE_90).isSameDir(dir, adjRot, adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), dir);
        }
        else if (side == perpRotDir && rot.isSameDir(dir, adjRot.getOpposite(), adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), dir);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side) || !Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((adjDir == dir && front) || (adjDir == dir.getOpposite() && !front)) { return false; }

        if ((side == Direction.UP && !adjTop) || (side == Direction.DOWN && adjTop))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstPanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (((adjDir == dir && !front) || (adjDir == dir.getOpposite() && front)))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoublePanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_NE);

        if ((adjDir == dir || adjDir == dir.getOpposite()))
        {
            Direction camoSide = front ? dir.getOpposite() : dir;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        return false;
    }

    private static boolean testAgainstCornerPillar(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side) || Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        Direction secondPanelSide = front ? dir.getOpposite() : dir;
        if (CornerPillarSkipPredicate.isPanelSide(adjDir, side.getOpposite()) && CornerPillarSkipPredicate.isPanelSide(adjDir, secondPanelSide))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;

        if (adjShape != StairsShape.STRAIGHT || (adjTop && side != Direction.UP) || (!adjTop && side != Direction.DOWN)) { return false; }

        if ((!front && adjDir == dir) || (front && adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, side, adjDir);
        }

        return false;
    }

    private static boolean testAgainstDoubleStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjDir.getAxis() == dir.getAxis() && ((side == Direction.UP && adjTop) || (side == Direction.DOWN && !adjTop)))
        {
            Direction camoSide = front ? dir.getOpposite() : dir;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (adjType != StairsType.VERTICAL) { return false; }

        if (side == dir.getClockWise() && ((!front && adjDir == dir.getClockWise()) || (front && adjDir == dir.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == dir.getCounterClockWise() && ((!front && adjDir == dir) || (front && adjDir == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (side == dir.getClockWise() && (adjDir == dir.getClockWise() || adjDir == dir.getOpposite()))
        {
            Direction camoSide = front ? dir.getOpposite() : dir;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }
        else if (side == dir.getCounterClockWise() && (adjDir == dir || adjDir == dir.getCounterClockWise()))
        {
            Direction camoSide = front ? dir.getOpposite() : dir;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjDir == side.getOpposite() || (adjTop && side == Direction.DOWN) || (!adjTop && side == Direction.UP))
        {
            if ((adjDir == dir.getClockWise() && adjRight == front) || (adjDir == dir.getCounterClockWise() && adjRight != front))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstDividedSlope(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (adjType == SlopeType.HORIZONTAL || !isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjType == SlopeType.TOP;

        if (adjDir == side.getOpposite() || (adjTop && side == Direction.DOWN) || (!adjTop && side == Direction.UP))
        {
            if (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise())
            {
                Direction camoSide = front ? dir.getOpposite() : dir;
                return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
            }
        }

        return false;
    }

    private static boolean testAgainstDoubleHalfSlope(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (adjDir.getAxis() == side.getAxis() || Utils.isY(side))
        {
            if ((adjDir == dir.getClockWise() && adjRight == front) || (adjDir == dir.getCounterClockWise() && adjRight != front))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstVerticalSlopedStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        if (!isPanelSide(dir, rot, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if ((!front && adjDir != dir) || (front && adjDir != dir.getOpposite()))
        {
            return false;
        }

        if (VerticalSlopedStairsSkipPredicate.isPanelFace(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }



    public static boolean isPanelSide(Direction dir, HorizontalRotation rot, Direction side)
    {
        return side == rot.withFacing(dir).getOpposite() || side == rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir).getOpposite();
    }
}
