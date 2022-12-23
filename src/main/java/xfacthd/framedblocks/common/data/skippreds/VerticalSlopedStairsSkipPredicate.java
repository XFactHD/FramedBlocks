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
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.*;

public final class VerticalSlopedStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);

        if (side == dir || side == rotDir.getOpposite() || side == rot.rotate(Rotation.CLOCKWISE_90).withFacing(dir))
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            return switch (blockType)
            {
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_SLOPE,
                     FRAMED_RAIL_SLOPE,
                     FRAMED_POWERED_RAIL_SLOPE,
                     FRAMED_DETECTOR_RAIL_SLOPE,
                     FRAMED_ACTIVATOR_RAIL_SLOPE,
                     FRAMED_FANCY_RAIL_SLOPE,
                     FRAMED_FANCY_POWERED_RAIL_SLOPE,
                     FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
                     FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE -> testAgainstSlope(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE -> testAgainstDoubleSlope(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_DOUBLE_CORNER -> testAgainstDoubleCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_PRISM_CORNER, FRAMED_THREEWAY_CORNER -> testAgainstThreewayCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_INNER_PRISM_CORNER, FRAMED_INNER_THREEWAY_CORNER -> testAgainstInnerThreewayCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_DOUBLE_PRISM_CORNER, FRAMED_DOUBLE_THREEWAY_CORNER -> testAgainstDoubleThreewayCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        level, pos, dir, rot, rotDir, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstVerticalSlopedStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (isPanelFace(dir, rot, rotDir, side) && isPanelFace(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == dir.getOpposite() && adjRot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(adjDir) == rotDir)
        {
            return adjDir == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstHalfSlope(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (!HalfSlopeSkipPredicate.isOuterTriangle(adjDir, adjRight, dir))
        {
            return false;
        }

        if (isPanelFace(dir, rot, rotDir, side) && HalfSlopeSkipPredicate.isPanelFace(adjDir, adjTop, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == dir.getOpposite())
        {
            Direction panelFaceVert = adjTop ? Direction.UP : Direction.DOWN;
            if (!isPanelFace(dir, rot, rotDir, panelFaceVert) && !isPanelFace(dir, rot, rotDir, adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstDoubleHalfSlope(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (!HalfSlopeSkipPredicate.isOuterTriangle(adjDir, adjRight, dir))
        {
            return false;
        }

        if (isPanelFace(dir, rot, rotDir, side))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == dir.getOpposite())
        {
            if (!isPanelFace(dir, rot, rotDir, Direction.DOWN) && !isPanelFace(dir, rot, rotDir, adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side, side, Direction.DOWN);
            }
            if (!isPanelFace(dir, rot, rotDir, Direction.UP) && !isPanelFace(dir, rot, rotDir, adjDir.getOpposite()))
            {
                return SideSkipPredicate.compareState(level, pos, side, side, Direction.UP);
            }
        }

        return false;
    }

    private static boolean testAgainstDividedSlope(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (adjType == SlopeType.HORIZONTAL)
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjType == SlopeType.TOP;

        if (isPanelFace(dir, rot, rotDir, side) && HalfSlopeSkipPredicate.isPanelFace(adjDir, adjTop, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, side, dir);
        }
        else if (side == dir.getOpposite())
        {
            Direction panelFaceVert = adjTop ? Direction.UP : Direction.DOWN;
            if (!isPanelFace(dir, rot, rotDir, panelFaceVert) && !isPanelFace(dir, rot, rotDir, adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstSlope(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (adjType == SlopeType.HORIZONTAL || side != dir.getOpposite())
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjType == SlopeType.TOP;

        Direction panelFaceVert = adjTop ? Direction.UP : Direction.DOWN;
        if (!isPanelFace(dir, rot, rotDir, panelFaceVert) && !isPanelFace(dir, rot, rotDir, adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlope(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (adjType == SlopeType.HORIZONTAL || side != dir.getOpposite())
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (isPanelFace(dir, rot, rotDir, side))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == dir.getOpposite())
        {
            if (!isPanelFace(dir, rot, rotDir, Direction.DOWN) && !isPanelFace(dir, rot, rotDir, adjDir))
            {
                return SideSkipPredicate.compareState(level, pos, side, side, Direction.DOWN);
            }
            if (!isPanelFace(dir, rot, rotDir, Direction.UP) && !isPanelFace(dir, rot, rotDir, adjDir.getOpposite()))
            {
                return SideSkipPredicate.compareState(level, pos, side, side, Direction.UP);
            }
        }

        return false;
    }

    private static boolean testAgainstCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (adjType.isHorizontal())
        {
            if (adjType.isTop() != isPanelFace(dir, rot, rotDir, Direction.UP) && !isPanelFace(dir, rot, side, adjDir))
            {
                Direction horFace = adjType.isRight() ? adjDir.getClockWise() : adjDir.getCounterClockWise();
                return horFace == dir && SideSkipPredicate.compareState(level, pos, side);
            }
        }
        else
        {
            if ((adjType == CornerType.TOP) != isPanelFace(dir, rot, rotDir, Direction.UP))
            {
                if (adjDir == dir && !isPanelFace(dir, rot, rotDir, dir.getCounterClockWise()))
                {
                    return SideSkipPredicate.compareState(level, pos, side);
                }
                else if (adjDir == dir.getClockWise() && !isPanelFace(dir, rot, rotDir, dir.getClockWise()))
                {
                    return SideSkipPredicate.compareState(level, pos, side);
                }
            }
        }

        return false;
    }

    private static boolean testAgainstInnerCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (adjType.isHorizontal())
        {
            if (adjType.isTop() != isPanelFace(dir, rot, rotDir, Direction.UP) && !isPanelFace(dir, rot, side, adjDir))
            {
                Direction horFace = adjType.isRight() ? adjDir.getCounterClockWise() : adjDir.getClockWise();
                return horFace == dir && SideSkipPredicate.compareState(level, pos, side);
            }
        }
        else
        {
            if ((adjType == CornerType.TOP) != isPanelFace(dir, rot, rotDir, Direction.UP))
            {
                if (adjDir == dir.getCounterClockWise() && !isPanelFace(dir, rot, rotDir, dir.getCounterClockWise()))
                {
                    return SideSkipPredicate.compareState(level, pos, side);
                }
                else if (adjDir == dir.getOpposite() && !isPanelFace(dir, rot, rotDir, dir.getClockWise()))
                {
                    return SideSkipPredicate.compareState(level, pos, side);
                }
            }
        }

        return false;
    }

    private static boolean testAgainstDoubleCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        boolean topPanel = isPanelFace(dir, rot, rotDir, Direction.UP);
        if (adjType.isHorizontal())
        {
            boolean adjTop = adjType.isTop();
            Direction horFace = adjType.isRight() ? adjDir.getCounterClockWise() : adjDir.getClockWise();

            if (adjTop != topPanel && !isPanelFace(dir, rot, rotDir, adjDir))
            {
                return horFace == dir && SideSkipPredicate.compareState(level, pos, side, side, adjDir);
            }
            else if (adjTop == topPanel && !isPanelFace(dir, rot, rotDir, adjDir.getOpposite()))
            {
                return horFace == dir && SideSkipPredicate.compareState(level, pos, side, side, adjDir.getOpposite());
            }
        }
        else
        {
            boolean adjTop = adjType == CornerType.TOP;
            if (adjTop == topPanel)
            {
                if (adjDir == dir.getOpposite() && !isPanelFace(dir, rot, rotDir, dir.getCounterClockWise()))
                {
                    return SideSkipPredicate.compareState(level, pos, side, side, adjTop ? Direction.DOWN : Direction.UP);
                }
                else if (adjDir == dir.getCounterClockWise() && !isPanelFace(dir, rot, rotDir, dir.getClockWise()))
                {
                    return SideSkipPredicate.compareState(level, pos, side, side, adjTop ? Direction.DOWN : Direction.UP);
                }
            }
            else
            {
                if (adjDir == dir.getCounterClockWise() && !isPanelFace(dir, rot, rotDir, dir.getCounterClockWise()))
                {
                    return SideSkipPredicate.compareState(level, pos, side, side, adjTop ? Direction.UP : Direction.DOWN);
                }
                else if (adjDir == dir.getOpposite() && !isPanelFace(dir, rot, rotDir, dir.getClockWise()))
                {
                    return SideSkipPredicate.compareState(level, pos, side, side, adjTop ? Direction.UP : Direction.DOWN);
                }
            }
        }

        return false;
    }

    private static boolean testAgainstThreewayCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != isPanelFace(dir, rot, rotDir, Direction.UP))
        {
            if (adjDir == dir && !isPanelFace(dir, rot, rotDir, dir.getCounterClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
            else if (adjDir == dir.getClockWise() && !isPanelFace(dir, rot, rotDir, dir.getClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstInnerThreewayCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != isPanelFace(dir, rot, rotDir, Direction.UP))
        {
            if (adjDir == dir.getCounterClockWise() && !isPanelFace(dir, rot, rotDir, dir.getCounterClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
            else if (adjDir == dir.getOpposite() && !isPanelFace(dir, rot, rotDir, dir.getClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstDoubleThreewayCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        boolean topPanel = isPanelFace(dir, rot, rotDir, Direction.UP);
        if (adjTop == topPanel)
        {
            if (adjDir == dir.getOpposite() && !isPanelFace(dir, rot, rotDir, dir.getCounterClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side, side, adjTop ? Direction.DOWN : Direction.UP);
            }
            else if (adjDir == dir.getCounterClockWise() && !isPanelFace(dir, rot, rotDir, dir.getClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side, side, adjTop ? Direction.DOWN : Direction.UP);
            }
        }
        else
        {
            if (adjDir == dir.getCounterClockWise() && !isPanelFace(dir, rot, rotDir, dir.getCounterClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side, side, adjTop ? Direction.UP : Direction.DOWN);
            }
            else if (adjDir == dir.getOpposite() && !isPanelFace(dir, rot, rotDir, dir.getClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side, side, adjTop ? Direction.UP : Direction.DOWN);
            }
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        Direction panelFace = adjTop ? Direction.DOWN : Direction.UP;
        return adjDir == dir && side == panelFace && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstPanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        return adjDir == dir && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoublePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_NE);

        return adjDir.getAxis() == dir.getAxis() && SideSkipPredicate.compareState(level, pos, side, side, dir);
    }

    private static boolean testAgainstCornerPillar(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (CornerPillarSkipPredicate.isPanelSide(adjDir, side.getOpposite()) && CornerPillarSkipPredicate.isPanelSide(adjDir, dir))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        if (adjShape != StairsShape.STRAIGHT || !isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(StairBlock.FACING);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;

        Direction panelFace = adjTop ? Direction.UP : Direction.DOWN;
        if (adjDir == dir && side == panelFace)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        Direction panelFace = adjTop ? Direction.UP : Direction.DOWN;
        if (adjDir.getAxis() == dir.getAxis() && side == panelFace)
        {
            return SideSkipPredicate.compareState(level, pos, side, side, dir);
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);
        if (adjType != StairsType.VERTICAL || !isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((adjDir == dir && side == dir.getCounterClockWise()) || (adjDir == dir.getClockWise() && side == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((adjDir == dir || adjDir == dir.getCounterClockWise()) && side == dir.getCounterClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side, side, dir);
        }
        else if ((adjDir == dir.getClockWise() || adjDir == dir.getOpposite()) && side == dir.getClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side, side, dir);
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if ((!adjRight && adjDir != dir.getClockWise()) || (adjRight && adjDir != dir.getCounterClockWise()))
        {
            return false;
        }

        if (side == adjDir.getOpposite() || (!adjTop && side == Direction.UP) || (adjTop && side == Direction.DOWN))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if ((!adjFront && adjDir != dir) || (adjFront && adjDir != dir.getOpposite()))
        {
            return false;
        }

        return adjRot.withFacing(adjDir) == side && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstExtendedSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir == dir && adjRot.withFacing(adjDir) == side.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if ((!adjFront && adjDir != dir) || (adjFront && adjDir != dir.getOpposite()))
        {
            return false;
        }

        if (adjRot.withFacing(adjDir).getAxis() == side.getAxis())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        Direction adjRotDir = adjRot.withFacing(adjDir);

        if ((adjDir == dir && adjRotDir == side.getOpposite()) || (adjDir == dir.getOpposite() && adjRotDir == side))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir.getAxis() == dir.getAxis() && adjRot.withFacing(adjDir) == side.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side, side, dir);
        }

        return false;
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if ((!adjFront && adjDir != dir) || (adjFront && adjDir != dir.getOpposite()))
        {
            return false;
        }

        if (FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir == dir && FlatExtendedSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if ((!adjFront && adjDir != dir) || (adjFront && adjDir != dir.getOpposite()))
        {
            return false;
        }

        if (FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, side, adjDir);
        }

        return false;
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir == dir && FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, Direction rotDir, BlockState adjState, Direction side
    )
    {
        if (!isPanelFace(dir, rot, rotDir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir.getAxis() == dir.getAxis() && FlatExtendedSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, side, dir);
        }

        return false;
    }



    public static boolean isPanelFace(Direction dir, HorizontalRotation rot, Direction side)
    {
        return isPanelFace(dir, rot, rot.withFacing(dir), side);
    }

    public static boolean isPanelFace(Direction dir, HorizontalRotation rot, Direction rotDir, Direction side)
    {
        return side == rotDir || side == rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
    }
}
