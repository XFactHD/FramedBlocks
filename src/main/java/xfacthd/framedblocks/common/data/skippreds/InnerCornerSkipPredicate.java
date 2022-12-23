package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.util.FramedUtils;

public final class InnerCornerSkipPredicate implements SideSkipPredicate
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
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(level, pos, dir, type, adjState, side);
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
                case FRAMED_PRISM_CORNER, FRAMED_THREEWAY_CORNER -> testAgainstThreewaySlope(level, pos, dir, type, adjState, side);
                case FRAMED_INNER_PRISM_CORNER, FRAMED_INNER_THREEWAY_CORNER -> testAgainstInnerThreewaySlope(level, pos, dir, type, adjState, side);
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

    private static boolean testAgainstInnerCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && adjType == type && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) ||
                                                        (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())
        ))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal() && type.isHorizontalAdjacentInner(dir, side, adjType) && adjDir == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((side == dir.getClockWise() && adjType.isRight() && adjDir == dir) ||
                 (side == dir.getOpposite() && !adjType.isRight() && adjDir == dir.getCounterClockWise()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((!type.isRight() && side == dir.getClockWise() && adjDir == dir.getClockWise()) ||
                 (type.isRight() && side == dir.getCounterClockWise() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && !adjType.isHorizontal() && adjDir == dir && adjType.isTop() == type.isTop())
        {
            return (side == dir.getClockWise() || side == dir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal() && adjType == type && adjDir == dir && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop()) ||
                (side == dir.getClockWise() && !type.isRight()) || (side == dir.getCounterClockWise() && type.isRight()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((side == dir.getClockWise() && !adjType.isRight() && adjDir == dir) ||
                 (side == dir.getOpposite() && adjType.isRight() && adjDir == dir.getCounterClockWise()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((!type.isRight() && side == dir.getClockWise() && adjDir == dir) || (type.isRight() && side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstDoubleCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && !adjType.isHorizontal())
        {
            if (type.isTop() == adjType.isTop() && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())))
            {
                Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
            else if (type.isTop() != adjType.isTop() && adjDir == dir.getOpposite() && (side == dir.getClockWise() || side == dir.getOpposite()))
            {
                Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
        }
        else if (type.isHorizontal() && !adjType.isHorizontal())
        {
            if (type.isTop() == adjType.isTop() && ((side == dir.getCounterClockWise() && adjDir == dir && type.isRight()) ||
                                                    (side == dir.getClockWise() && adjDir == dir.getClockWise() && !type.isRight())
            ))
            {
                Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
            else if (type.isTop() != adjType.isTop() && ((side == dir.getClockWise() && adjDir == dir.getOpposite() && !type.isRight()) ||
                                                         (side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise() && type.isRight())
            ))
            {
                Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
        }
        else if (!type.isHorizontal()/* && adjType.isHorizontal()*/)
        {
            if (type.isTop() == adjType.isTop() && ((side == dir.getClockWise() && adjDir == dir && adjType.isRight()) ||
                                                    (side == dir.getOpposite() && adjDir == dir.getCounterClockWise() && !adjType.isRight())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, adjDir, adjDir);
            }
            else if (type.isTop() != adjType.isTop() && ((side == dir.getClockWise() && adjDir == dir.getOpposite() && !adjType.isRight()) ||
                                                         (side == dir.getOpposite() && adjDir == dir.getClockWise() && adjType.isRight())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, adjDir.getOpposite(), adjDir.getOpposite());
            }
        }
        else /*if (type.isHorizontal() && adjType.isHorizontal())*/
        {
            if (adjDir == dir)
            {
                if (type.isRight() == adjType.isRight() && ((side == Direction.UP && !type.isTop() && adjType.isTop()) ||
                                                            (side == Direction.DOWN && type.isTop() && !adjType.isTop())
                ))
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
                else if (type.isTop() == adjType.isTop() && ((side == dir.getClockWise() && !type.isRight() && adjType.isRight()) ||
                                                             (side == dir.getCounterClockWise() && type.isRight() && !adjType.isRight())
                ))
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
            }
            else if (adjDir == dir.getOpposite())
            {
                if (type.isRight() == adjType.isRight() && ((side == Direction.UP && !type.isTop() && adjType.isTop()) ||
                                                            (side == Direction.DOWN && type.isTop() && !adjType.isTop())
                ))
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
                else if (type.isRight() == adjType.isRight() && type.isTop() != adjType.isTop() &&
                        ((side == dir.getCounterClockWise() && type.isRight()) || (side == dir.getClockWise() && !type.isRight()))
                )
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
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

        if (!hor && !adjHor && ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())))
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side, camoSide, side.getOpposite());
        }
        else if (hor)
        {
            boolean right = type.isRight();

            if (((side == Direction.UP && !top) || (side == Direction.DOWN && top)) && adjHor)
            {
                return ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise())) && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
            else if (!Utils.isY(side) && adjDir == dir && adjTop == top)
            {
                return ((!right && side == dir.getClockWise()) || (right && side == dir.getCounterClockWise())) &&
                        SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
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
            (side == dir.getClockWise() && type.isTop() == adjTop && adjDir == dir) ||
            (side == dir.getClockWise() && type.isTop() != adjTop && adjDir == dir.getOpposite()) ||
            (side == dir.getOpposite() && type.isTop() == adjTop && adjDir == dir.getCounterClockWise()) ||
            (side == dir.getOpposite() && type.isTop() != adjTop && adjDir == dir.getClockWise())
        ))
        {
            Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
            return SideSkipPredicate.compareState(level, pos, side, face, face);
        }
        else if (type.isHorizontal() && adjType == SlopeType.HORIZONTAL && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop())))
        {
            if ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) ||
                (type.isRight() && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise()))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if (type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.getClockWise() && !type.isRight()) ||
                                                                            (side == dir.getCounterClockWise() && type.isRight()))
        )
        {
            Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
            return ((type.isTop() == adjTop && adjDir == dir) || (type.isTop() != adjTop && adjDir == dir.getOpposite())) &&
                    SideSkipPredicate.compareState(level, pos, side, face, face);
        }
        return false;
    }

    private static boolean testAgainstThreewaySlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (!type.isHorizontal() && adjDir == dir && adjTop == type.isTop() && (side == dir.getClockWise() || side == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal() && ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.getClockWise())) && adjTop == type.isTop() &&
                ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop()) || (side == dir.getClockWise() && !type.isRight()) ||
                        (side == dir.getCounterClockWise() && type.isRight()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstInnerThreewaySlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (!type.isHorizontal() && adjTop == type.isTop() && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) ||
                                                               (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal())
        {
            if (adjTop != type.isTop() && ((adjDir == dir && !type.isRight()) || (adjDir == dir.getClockWise() && type.isRight())) &&
                ((!type.isTop() && side == Direction.UP) || (type.isTop() && side == Direction.DOWN))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (adjTop == type.isTop() && ((!type.isRight() && side == dir.getClockWise() && adjDir == dir.getClockWise()) ||
                    (type.isRight() && side == dir.getCounterClockWise() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
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
            if (adjTop == type.isTop() && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())))
            {
                Direction camoSide = adjTop ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
            else if (adjTop != type.isTop() && adjDir == dir.getOpposite() && (side == dir.getClockWise() || side == dir.getOpposite()))
            {
                Direction camoSide = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
            }
        }
        else
        {
            if (adjTop == type.isTop() && ((side == dir.getCounterClockWise() && adjDir == dir && type.isRight()) ||
                                           (side == dir.getClockWise() && adjDir == dir.getClockWise() && !type.isRight())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (adjTop != type.isTop() && ((side == dir.getClockWise() && adjDir == dir.getOpposite() && !type.isRight()) ||
                                                (side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise() && type.isRight())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (adjTop != type.isTop() && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop())) &&
                     ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) ||
                      (type.isRight() && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise())))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
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

            if (((side == dir.getCounterClockWise() && right) || (side == dir.getClockWise() && !right)) && adjTop == top && adjRight == right)
            {
                return adjDir == dir && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        else
        {
            boolean top = type == CornerType.TOP;

            if ((!adjRight && side == dir.getClockWise() && adjDir == dir) || (adjRight && side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
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

            if ((side == dir.getCounterClockWise() && right) || (side == dir.getClockWise() && !right))
            {
                if ((!top && adjRight == right && adjDir == dir) || (top && adjRight != right && adjDir == dir.getOpposite()))
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
            }
        }
        else
        {
            boolean top = type == CornerType.TOP;

            if (!top && ((!adjRight && side == dir.getClockWise() && adjDir == dir) || (adjRight && side == dir.getOpposite() && adjDir == dir.getCounterClockWise())))
            {
                return SideSkipPredicate.compareState(level, pos, side, Direction.DOWN, Direction.DOWN);
            }
            else if (top && ((!adjRight && side == dir.getOpposite() && adjDir == dir.getClockWise()) || (adjRight && side == dir.getClockWise() && adjDir == dir.getOpposite())))
            {
                return SideSkipPredicate.compareState(level, pos, side, Direction.UP, Direction.UP);
            }
        }

        return false;
    }

    private static boolean testAgainstVerticalHalfSlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        boolean top = type.isTop();
        if (!type.isHorizontal() || (!top && side != Direction.UP) || (top && side != Direction.DOWN)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        boolean right = type.isRight();
        if ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise()))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        boolean top = type.isTop();
        if (!type.isHorizontal() || (!top && side != Direction.UP) || (top && side != Direction.DOWN)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        boolean right = type.isRight();
        if ((!right && adjDir.getAxis() == dir.getAxis()) || (right && adjDir.getAxis() == dir.getClockWise().getAxis()))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstSlopedStairs(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        boolean top = type.isTop();
        if (!type.isHorizontal() || (!top && side != Direction.UP) || (top && side != Direction.DOWN)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        boolean right = type.isRight();
        if ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise()))
        {
            return adjTop != top && SideSkipPredicate.compareState(level, pos, side, dir, dir);
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

            Direction horDir = type.isRight() ? dir.getCounterClockWise() : dir.getClockWise();
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
            if (side != dir.getOpposite() && side != dir.getClockWise()) { return false; }

            boolean top = type == CornerType.TOP;
            boolean vert = VerticalSlopedStairsSkipPredicate.isPanelFace(adjDir, adjRot, top ? Direction.DOWN : Direction.UP);
            Direction horDir = adjDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite();
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