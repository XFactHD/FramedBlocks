package xfacthd.framedblocks.common.data.skippreds.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalSlopedStairsSkipPredicate;
import xfacthd.framedblocks.common.util.FramedUtils;

public final class CornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

            return switch (blockType)
            {
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_CORNER -> testAgainstDoubleCorner(level, pos, dir, type, adjState, side);
                case FRAMED_SLOPE,
                     FRAMED_RAIL_SLOPE,
                     FRAMED_POWERED_RAIL_SLOPE,
                     FRAMED_DETECTOR_RAIL_SLOPE,
                     FRAMED_ACTIVATOR_RAIL_SLOPE,
                     FRAMED_FANCY_RAIL_SLOPE,
                     FRAMED_FANCY_POWERED_RAIL_SLOPE,
                     FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
                     FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE,
                     FRAMED_DIVIDED_SLOPE -> testAgainstSlope(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_SLOPE -> testAgainstDoubleSlope(level, pos, dir, type, adjState, side);
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(level, pos, dir, type, adjState, side);
                case FRAMED_PRISM_CORNER, FRAMED_THREEWAY_CORNER -> testAgainstThreewayCorner(level, pos, dir, type, adjState, side);
                case FRAMED_INNER_PRISM_CORNER, FRAMED_INNER_THREEWAY_CORNER -> testAgainstInnerThreewayCorner(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_PRISM_CORNER, FRAMED_DOUBLE_THREEWAY_CORNER -> testAgainstDoubleThreewayCorner(level, pos, dir, type, adjState, side);
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(level, pos, dir, type, adjState, side);
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(level, pos, dir, type, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(level, pos, dir, type, adjState, side);
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(level, pos, dir, type, adjState, side);
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(level, pos, dir, type, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && adjType == type && ((side == dir && adjDir == dir.getCounterClockWise()) ||
                                                        (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        )
        {
            Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
        }
        else if (type.isHorizontal() && type.isHorizontalAdjacent(dir, side, adjType) && adjDir == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                 ((side == dir && !adjType.isRight() && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjType.isRight() && adjDir == dir))
        )
        {
            Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                 ((!type.isRight() && side == dir.getCounterClockWise() && adjDir == dir.getClockWise()) || (type.isRight() && side == dir.getClockWise() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstDoubleCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!adjType.isHorizontal() && !type.isHorizontal())
        {
            if (adjType.isTop() == type.isTop() && adjDir == dir && (side == dir || side == dir.getCounterClockWise()))
            {
                Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
            else if (adjType.isTop() != type.isTop() && ((side == dir && adjDir == dir.getClockWise()) ||
                                                         (side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise()))
            )
            {
                Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
        }
        else if (type.isHorizontal() && !adjType.isHorizontal())
        {
            if ((adjDir == dir && side == dir.getCounterClockWise() && !type.isRight() && adjType.isTop() == type.isTop()) ||
                    (adjDir == dir.getOpposite() && side == dir.getClockWise() && type.isRight() && adjType.isTop() != type.isTop())
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if ((adjDir == dir.getCounterClockWise() && side == dir.getCounterClockWise() && !type.isRight() && adjType.isTop() != type.isTop()) ||
                     (adjDir == dir.getClockWise() && side == dir.getClockWise() && type.isRight() && adjType.isTop() == type.isTop())
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if (!type.isHorizontal() /*&& adjType.isHorizontal()*/)
        {
            if ((side == dir.getCounterClockWise() && adjDir == dir && !adjType.isRight()) ||
                    (side == dir && adjDir == dir.getCounterClockWise() && adjType.isRight())
            )
            {
                return adjType.isTop() == type.isTop() && SideSkipPredicate.compareState(level, pos, side, adjDir, adjDir);
            }
            else if ((side == dir.getCounterClockWise() && adjDir == dir.getOpposite() && adjType.isRight()) ||
                    (side == dir && adjDir == dir.getClockWise() && !adjType.isRight())
            )
            {
                return adjType.isTop() != type.isTop() && SideSkipPredicate.compareState(level, pos, side, adjDir.getOpposite(), adjDir.getOpposite());
            }
        }
        else /*if (type.isHorizontal() && adjType.isHorizontal())*/
        {
            if (adjDir == dir && type == adjType && ((side == dir.getClockWise() && type.isRight()) || (side == dir.getCounterClockWise() && !type.isRight()) ||
                    (side == Direction.UP && type.isTop()) || (side == Direction.DOWN && !type.isTop())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (adjDir == dir.getOpposite() && type.isTop() != adjType.isTop() && type.isRight() != adjType.isRight() &&
                     ((side == dir.getClockWise() && type.isRight()) || (side == dir.getCounterClockWise() && !type.isRight()))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (adjDir == dir.getOpposite() && adjType == type && ((side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop())))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        return false;
    }

    private static boolean testAgainstSlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = FramedUtils.getSlopeBlockFacing(adjState);
        SlopeType adjType = FramedUtils.getSlopeType(adjState);
        boolean adjTop = adjType == SlopeType.TOP;
        boolean adjHor = adjType == SlopeType.HORIZONTAL;

        boolean top = type.isTop();
        boolean hor = type.isHorizontal();

        if (!hor && !adjHor && adjTop == top)
        {
            if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir))
            {
                Direction face = top ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, face, side.getOpposite());
            }
        }
        else if (hor)
        {
            boolean right = type.isRight();

            if (((side == dir.getClockWise() && right) || (side == dir.getCounterClockWise() && !right)) && !adjHor && adjTop == top)
            {
                return adjDir == dir && SideSkipPredicate.compareState(level, pos, side);
            }
            else if (((side == Direction.UP && top) || (side == Direction.DOWN && !top)) && adjHor)
            {
                if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir))
                {
                    return SideSkipPredicate.compareState(level, pos, side);
                }
            }
        }
        return false;
    }

    private static boolean testAgainstDoubleSlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjTop = adjType == SlopeType.TOP;

        if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && (
                (side == dir && type.isTop() == adjTop && adjDir == dir.getCounterClockWise()) ||
                        (side == dir && type.isTop() != adjTop && adjDir == dir.getClockWise()) ||
                        (side == dir.getCounterClockWise() && type.isTop() == adjTop && adjDir == dir) ||
                        (side == dir.getCounterClockWise() && type.isTop() != adjTop && adjDir == dir.getOpposite())
        ))
        {
            Direction face = type.isTop() ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, face, face);
        }
        else if (type.isHorizontal() && adjType == SlopeType.HORIZONTAL && ((side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop())))
        {
            if ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) || (type.isRight() && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise())))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if (type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.getCounterClockWise() && !type.isRight()) ||
                                                                            (side == dir.getClockWise() && type.isRight())))
        {
            Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
            return ((type.isTop() == adjTop && adjDir == dir) || (type.isTop() != adjTop && adjDir == dir.getOpposite())) &&
                    SideSkipPredicate.compareState(level, pos, side, face, face);
        }
        return false;
    }

    private static boolean testAgainstInnerCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && adjType == type && adjDir == dir && (side == dir || side == dir.getCounterClockWise()))
        {
            Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
        }
        else if (type.isHorizontal() && adjType == type && ((side == Direction.UP && type.isTop()) || (side == Direction.DOWN && !type.isTop()) ||
                (side == dir.getClockWise() && type.isRight()) || (side == dir.getCounterClockWise() && !type.isRight()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((side == dir && adjType.isRight() && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && !adjType.isRight() && adjDir == dir))
        )
        {
            Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((!type.isRight() && side == dir.getCounterClockWise() && adjDir == dir) ||
                 (type.isRight() && side == dir.getClockWise() && adjDir == dir.getClockWise()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstThreewayCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (!type.isHorizontal() && type.isTop() == adjTop)
        {
            if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
            {
                Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
        }
        else if (type.isHorizontal())
        {
            if ((side == dir.getClockWise() && type.isRight() && adjDir == dir && type.isTop() == adjTop) ||
                (side == dir.getCounterClockWise() && !type.isRight() && adjDir == dir.getClockWise() && type.isTop() == adjTop)
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (Utils.isY(side) && type.isTop() != adjTop && (side == Direction.DOWN) == !type.isTop() &&
                    ((type.isRight() && adjDir == dir.getClockWise()) || (!type.isRight() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        return false;
    }

    private static boolean testAgainstInnerThreewayCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (!type.isHorizontal() && type.isTop() == adjTop && adjDir == dir)
        {
            Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
            return (side == dir || side == dir.getCounterClockWise()) && SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
        }
        else if (type.isHorizontal())
        {
            if (Utils.isY(side) && ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.getClockWise())))
            {
                return type.isTop() == adjTop && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if ((!type.isRight() && side == dir.getCounterClockWise() && adjDir == dir) ||
                     (type.isRight() && side == dir.getClockWise() && adjDir == dir.getClockWise())
            )
            {
                return type.isTop() == adjTop && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        return false;
    }

    private static boolean testAgainstDoubleThreewayCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (!type.isHorizontal())
        {
            if (adjDir == dir && adjTop == type.isTop() && (side == dir || side == dir.getCounterClockWise()))
            {
                Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
            else if (adjTop != type.isTop() && ((side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise()) || (side == dir && adjDir == dir.getClockWise())))
            {
                Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
        }
        else if (adjTop == type.isTop())
        {
            if ((side == dir.getCounterClockWise() && adjDir == dir && !type.isRight()) || (side == dir.getClockWise() && adjDir == dir.getClockWise() && type.isRight()))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (((side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop())) &&
                     ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) ||
                      (type.isRight() && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise()))
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if ((side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise() && !type.isRight()) ||
                 (side == dir.getClockWise() && adjDir == dir.getOpposite() && type.isRight())
        )
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstHalfSlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (type.isHorizontal())
        {
            boolean top = type.isTop();
            boolean right = type.isRight();

            if (((side == dir.getClockWise() && right) || (side == dir.getCounterClockWise() && !right)) && adjTop == top && adjRight != right)
            {
                return adjDir == dir && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        else
        {
            boolean top = type == CornerType.TOP;

            if ((!adjRight && side == dir && adjDir == dir.getCounterClockWise()) || (adjRight && side == dir.getCounterClockWise() && adjDir == dir))
            {
                Direction camoSide = top ? Direction.UP : Direction.DOWN;
                return adjTop == top && SideSkipPredicate.compareState(level, pos, side, camoSide, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstDoubleHalfSlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (type.isHorizontal())
        {
            boolean top = type.isTop();
            boolean right = type.isRight();

            if ((side == dir.getClockWise() && right) || (side == dir.getCounterClockWise() && !right))
            {
                if ((!top && adjRight != right && adjDir == dir) || (top && adjRight == right && adjDir == dir.getOpposite()))
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
            }
        }
        else
        {
            boolean top = type == CornerType.TOP;

            if (!top && ((!adjRight && side == dir && adjDir == dir.getCounterClockWise()) || (adjRight && side == dir.getCounterClockWise() && adjDir == dir)))
            {
                return SideSkipPredicate.compareState(level, pos, side, Direction.DOWN, Direction.DOWN);
            }
            else if (top && ((!adjRight && side == dir.getCounterClockWise() && adjDir == dir.getOpposite()) || (adjRight && side == dir && adjDir == dir.getClockWise())))
            {
                return SideSkipPredicate.compareState(level, pos, side, Direction.UP, Direction.UP);
            }
        }

        return false;
    }

    private static boolean testAgainstVerticalHalfSlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        boolean top = type.isTop();
        if (!type.isHorizontal() || (!top && side != Direction.DOWN) || (top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        boolean right = type.isRight();
        if ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise()))
        {
            return adjTop != top && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        boolean top = type.isTop();
        if (!type.isHorizontal() || (!top && side != Direction.DOWN) || (top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        boolean right = type.isRight();
        if ((!right && adjDir.getAxis() == dir.getAxis()) || (right && adjDir.getAxis() == dir.getClockWise().getAxis()))
        {
            return adjTop != top && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstSlopedStairs(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        boolean top = type.isTop();
        if (!type.isHorizontal() || (!top && side != Direction.DOWN) || (top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        boolean right = type.isRight();
        if ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise()))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstVerticalSlopedStairs(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (type.isHorizontal())
        {
            boolean top = type.isTop();

            Direction horDir = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
            Direction vertDir = top ? Direction.DOWN : Direction.UP;
            if (side != horDir && side != vertDir) { return false; }

            boolean vert = VerticalSlopedStairsSkipPredicate.isPanelFace(adjDir, adjRot, vertDir);
            boolean hor = VerticalSlopedStairsSkipPredicate.isPanelFace(adjDir, adjRot, dir.getOpposite());
            if (adjDir == side && vert && hor)
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        else
        {
            if (side != dir && side != dir.getCounterClockWise()) { return false; }

            boolean top = type == CornerType.TOP;
            boolean vert = VerticalSlopedStairsSkipPredicate.isPanelFace(adjDir, adjRot, top ? Direction.DOWN : Direction.UP);
            Direction horDir = adjDir == dir ? dir.getClockWise() : dir.getOpposite();
            boolean hor = VerticalSlopedStairsSkipPredicate.isPanelFace(adjDir, adjRot, horDir);
            if (adjDir == side && vert && hor)
            {
                Direction camoSide = top ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, side.getOpposite());
            }
        }

        return false;
    }
}