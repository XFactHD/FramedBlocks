package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.*;

/*
FIXME: BREAKING CHANGE!!!
FIXME: Fix inner corner top/bottom rotation discrepancy from other corners (should be rotated 90 degree clockwise)
*/
public class FramedCornerSlopeBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);
        if (type == CornerType.TOP)
        {
            return dir == Direction.UP;
        }
        else if (type == CornerType.BOTTOM)
        {
            return dir == Direction.DOWN;
        }
        return state.get(PropertyHolder.FACING_HOR) == dir;
    };

    public static final CtmPredicate CTM_PREDICATE_INNER = (state, dir) ->
    {
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);
        if ((type == CornerType.TOP || (type.isHorizontal() && type.isTop())) && dir == Direction.UP)
        {
            return true;
        }
        else if ((type == CornerType.BOTTOM || (type.isHorizontal() && !type.isTop())) && dir == Direction.DOWN)
        {
            return true;
        }

        Direction facing = state.get(PropertyHolder.FACING_HOR);
        if (type.isHorizontal())
        {
            return facing == dir || (type.isRight() && facing.rotateY() == dir) || (!type.isRight() && facing.rotateYCCW() == dir);
        }
        else
        {
            return facing == dir || facing.rotateY() == dir;
        }
    };

    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }

        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);

        if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

            if (!type.isHorizontal() && adjType == type && ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY())))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal() && type.isHorizontalAdjacent(dir, side, adjType) && adjDir == dir)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                    ((side == dir && !adjType.isRight() && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjType.isRight() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                    ((!type.isRight() && side == dir.rotateYCCW() && adjDir == dir.rotateY()) || (type.isRight() && side == dir.rotateY() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

            if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && (adjType == SlopeType.TOP) == type.isTop())
            {
                if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir))
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
            }
            else if (type.isHorizontal())
            {
                if (((side == dir.rotateY() && type.isRight()) || (side == dir.rotateYCCW() && !type.isRight())) && (adjType == SlopeType.TOP) == type.isTop())
                {
                    return adjDir == dir && SideSkipPredicate.compareState(world, pos, side);
                }
                else if ((side == Direction.UP && type.isTop()) || (side == Direction.DOWN && !type.isTop()))
                {
                    return ((type.isRight() && adjDir == dir.rotateY()) || (!type.isRight() && adjDir == dir)) && SideSkipPredicate.compareState(world, pos, side);
                }
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_DOUBLE_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);
            boolean adjTop = adjType == SlopeType.TOP;

            if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && (
                    (side == dir && type.isTop() == adjTop && adjDir == dir.rotateYCCW()) ||
                    (side == dir && type.isTop() != adjTop && adjDir == dir.rotateY()) ||
                    (side == dir.rotateYCCW() && type.isTop() == adjTop && adjDir == dir) ||
                    (side == dir.rotateYCCW() && type.isTop() != adjTop && adjDir == dir.getOpposite())
            ))
            {
                Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
                return SideSkipPredicate.compareState(world, pos, side, face);
            }
            else if (type.isHorizontal() && adjType == SlopeType.HORIZONTAL && ((side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop())))
            {
                if ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) || (type.isRight() && (adjDir == dir.rotateY() || adjDir == dir.rotateYCCW())))
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
            else if (type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.rotateYCCW() && !type.isRight()) || (side == dir.rotateY() && type.isRight())))
            {
                Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
                return ((type.isTop() == adjTop && adjDir == dir) || (type.isTop() != adjTop && adjDir == dir.getOpposite())) && SideSkipPredicate.compareState(world, pos, side, face);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_INNER_CORNER_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

            if (!type.isHorizontal() && adjType == type && adjDir == dir.rotateYCCW() && (side == dir || side == dir.rotateYCCW()))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal() && adjType == type && ((side == Direction.UP && type.isTop()) || (side == Direction.DOWN && !type.isTop()) ||
                    (side == dir.rotateY() && type.isRight()) || (side == dir.rotateYCCW() && !type.isRight()))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                    ((side == dir && adjType.isRight() && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && !adjType.isRight() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                    ((!type.isRight() && side == dir.rotateYCCW() && adjDir == dir.rotateYCCW()) || (type.isRight() && side == dir.rotateY() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }

        else if (adjBlock == BlockType.FRAMED_PRISM_CORNER || adjBlock == BlockType.FRAMED_THREEWAY_CORNER)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if (!type.isHorizontal() && type.isTop() == adjTop)
            {
                if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
            }
            else if (type.isHorizontal())
            {
                if ((side == dir.rotateY() && type.isRight() && adjDir == dir && type.isTop() == adjTop) ||
                    (side == dir.rotateYCCW() && !type.isRight() && adjDir == dir.rotateY() && type.isTop() == adjTop)
                )
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
                else if (side.getAxis() == Direction.Axis.Y && type.isTop() != adjTop && (side == Direction.DOWN) == !type.isTop() &&
                        ((type.isRight() && adjDir == dir.rotateY()) || (!type.isRight() && adjDir == dir))
                )
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_INNER_PRISM_CORNER || adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if (adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER) { adjDir = adjDir.rotateY(); } //Correct rotation discrepancy of the threeway corner

            if (!type.isHorizontal() && type.isTop() == adjTop && adjDir == dir)
            {
                return (side == dir || side == dir.rotateYCCW()) && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal())
            {
                if (side.getAxis() == Direction.Axis.Y && ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.rotateY())))
                {
                    return type.isTop() == adjTop && SideSkipPredicate.compareState(world, pos, side);
                }
                else if ((!type.isRight() && side == dir.rotateYCCW() && adjDir == dir) ||
                         (type.isRight() && side == dir.rotateY() && adjDir == dir.rotateY())
                )
                {
                    return type.isTop() == adjTop && SideSkipPredicate.compareState(world, pos, side);
                }
            }
            return false;
        }

        return false;
    };

    public static final SideSkipPredicate SKIP_PREDICATE_INNER = (world, pos, state, adjState, side) ->
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }

        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);

        if (adjBlock == BlockType.FRAMED_INNER_CORNER_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

            if (!type.isHorizontal() && adjType == type && ((side == dir.getOpposite() && adjDir == dir.rotateY()) || (side == dir.rotateYCCW() && adjDir == dir.rotateYCCW())))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal() && type.isHorizontalAdjacentInner(dir, side, adjType) && adjDir == dir)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                    ((side == dir.getOpposite() && adjType.isRight() && adjDir == dir.rotateY()) || (side == dir.rotateYCCW() && !adjType.isRight() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                    ((!type.isRight() && side == dir.rotateY() && adjDir == dir) || (type.isRight() && side == dir.rotateYCCW() && adjDir == dir.rotateYCCW()))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

            if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.getOpposite() && adjDir == dir.rotateY()) || (side == adjDir.rotateYCCW() && adjDir == dir)))
            {
                return (adjType == SlopeType.TOP) == type.isTop() && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal())
            {
                if (((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop())) && adjType == SlopeType.HORIZONTAL)
                {
                    return ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.rotateY())) && SideSkipPredicate.compareState(world, pos, side);
                }
                else if (side.getAxis() != Direction.Axis.Y && adjDir == dir && (adjType == SlopeType.TOP) == type.isTop())
                {
                    return ((!type.isRight() && side == dir.rotateY()) || (type.isRight() && side == dir.rotateYCCW())) && SideSkipPredicate.compareState(world, pos, side);
                }
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_DOUBLE_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);
            boolean adjTop = adjType == SlopeType.TOP;

            if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && (
                    (side == dir.getOpposite() && type.isTop() == adjTop && adjDir == dir.rotateY()) ||
                    (side == dir.getOpposite() && type.isTop() != adjTop && adjDir == dir.rotateYCCW()) ||
                    (side == dir.rotateYCCW() && type.isTop() == adjTop && adjDir == dir) ||
                    (side == dir.rotateYCCW() && type.isTop() != adjTop && adjDir == dir.getOpposite())
            ))
            {
                Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
                return SideSkipPredicate.compareState(world, pos, side, face);
            }
            else if (type.isHorizontal() && adjType == SlopeType.HORIZONTAL && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop())))
            {
                if ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) || (type.isRight() && (adjDir == dir.rotateY() || adjDir == dir.rotateYCCW())))
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
            else if (type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.rotateY() && !type.isRight()) || (side == dir.rotateYCCW() && type.isRight())))
            {
                Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
                return ((type.isTop() == adjTop && adjDir == dir) || (type.isTop() != adjTop && adjDir == dir.getOpposite())) && SideSkipPredicate.compareState(world, pos, side, face);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

            if (!type.isHorizontal() && !adjType.isHorizontal() && adjDir == dir.rotateY() && adjType.isTop() == type.isTop())
            {
                return (side == dir.getOpposite() || side == dir.rotateYCCW()) && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal() && adjType == type && adjDir == dir && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop()) ||
                     (side == dir.rotateY() && !type.isRight()) || (side == dir.rotateYCCW() && type.isRight()))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                    ((side == dir.getOpposite() && !adjType.isRight() && adjDir == dir.rotateY()) || (side == dir.rotateYCCW() && adjType.isRight() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                    ((!type.isRight() && side == dir.rotateY() && adjDir == dir) || (type.isRight() && side == dir.rotateYCCW() && adjDir == dir.rotateY()))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_PRISM_CORNER || adjBlock == BlockType.FRAMED_THREEWAY_CORNER)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if (!type.isHorizontal() && adjDir == dir.rotateY() && adjTop == type.isTop() && (side == dir.getOpposite() || side == dir.rotateYCCW()))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal() && ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.rotateY())) && adjTop == type.isTop() &&
                    ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop()) || (side == dir.rotateY() && !type.isRight()) ||
                     (side == dir.rotateYCCW() && type.isRight()))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_INNER_PRISM_CORNER || adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if (adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER) { adjDir = adjDir.rotateY(); } //Correct rotation discrepancy of the threeway corner

            if (!type.isHorizontal() && adjTop == type.isTop() && ((side == dir.getOpposite() && adjDir == dir.getOpposite()) || (side == dir.rotateYCCW() && adjDir == dir)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (type.isHorizontal())
            {
                if (adjTop != type.isTop() && adjDir == dir && ((!type.isTop() && side == Direction.UP) || (type.isTop() && side == Direction.DOWN)))
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
                else if (adjTop == type.isTop() && ((!type.isRight() && side == dir.rotateY() && adjDir == dir.rotateY()) ||
                         (type.isRight() && side == dir.rotateYCCW() && adjDir == dir))
                )
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
            }
            return false;
        }

        return false;
    };

    public FramedCornerSlopeBlock(BlockType type) { super(type); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.CORNER_TYPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();

        Direction side = context.getFace();
        Vector3d hitPoint = Utils.fraction(context.getHitVec());
        if (side.getAxis() != Direction.Axis.Y)
        {
            if (hitPoint.getY() < (3D / 16D))
            {
                side = Direction.UP;
            }
            else if (hitPoint.getY() > (13D / 16D))
            {
                side = Direction.DOWN;
            }
        }

        Direction facing = context.getPlacementHorizontalFacing();
        if (getBlockType() == BlockType.FRAMED_INNER_CORNER_SLOPE && side.getAxis() == Direction.Axis.Y)
        {
            facing = facing.rotateYCCW();
        }
        state = state.with(PropertyHolder.FACING_HOR, facing);

        if (side == Direction.DOWN)
        {
            state = state.with(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.with(PropertyHolder.CORNER_TYPE, CornerType.BOTTOM);
        }
        else
        {
            boolean xAxis = context.getFace().getAxis() == Direction.Axis.X;
            boolean positive = context.getFace().rotateYCCW().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.getZ() : hitPoint.getX();
            double y = hitPoint.getY();

            CornerType type;
            if ((xz > .5D) == positive)
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_RIGHT : CornerType.HORIZONTAL_BOTTOM_RIGHT;
            }
            else
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_LEFT : CornerType.HORIZONTAL_BOTTOM_LEFT;
            }
            state = state.with(PropertyHolder.CORNER_TYPE, type);
        }

        return withWater(state, context.getWorld(), context.getPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateCornerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            CornerType type = state.get(PropertyHolder.CORNER_TYPE);
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (type.isHorizontal())
            {
                VoxelShape shapeBottomLeft = VoxelShapes.or(
                        makeCuboidShape(0, 0, 16,  4,  4, 0),
                        makeCuboidShape(0, 0, 12,  8,  8, 0),
                        makeCuboidShape(0, 0,  8, 12, 12, 0),
                        makeCuboidShape(0, 0,  4, 16, 16, 0)
                ).simplify();

                VoxelShape shapeBottomRight = VoxelShapes.or(
                        makeCuboidShape( 0, 0, 0, 16, 16,  4),
                        makeCuboidShape( 4, 0, 0, 16, 12,  8),
                        makeCuboidShape( 8, 0, 0, 16,  8, 12),
                        makeCuboidShape(12, 0, 0, 16,  4, 16)
                ).simplify();

                VoxelShape shapeTopLeft = VoxelShapes.or(
                        makeCuboidShape(0,  0, 0, 16, 16,  4),
                        makeCuboidShape(0,  4, 0, 12, 16,  8),
                        makeCuboidShape(0,  8, 0,  8, 16, 12),
                        makeCuboidShape(0, 12, 0,  4, 16, 16)
                ).simplify();

                VoxelShape shapeTopRight = VoxelShapes.or(
                        makeCuboidShape( 0,  0, 0, 16, 16,  4),
                        makeCuboidShape( 4,  4, 0, 16, 16,  8),
                        makeCuboidShape( 8,  8, 0, 16, 16, 12),
                        makeCuboidShape(12, 12, 0, 16, 16, 16)
                ).simplify();

                VoxelShape shape = VoxelShapes.fullCube();
                switch (type)
                {
                    case HORIZONTAL_BOTTOM_LEFT:
                        shape = shapeBottomLeft;
                        break;
                    case HORIZONTAL_BOTTOM_RIGHT:
                        shape = shapeBottomRight;
                        break;
                    case HORIZONTAL_TOP_LEFT:
                        shape = shapeTopLeft;
                        break;
                    case HORIZONTAL_TOP_RIGHT:
                        shape = shapeTopRight;
                        break;
                }
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
            }
            else if (type.isTop())
            {
                VoxelShape shapeTop = VoxelShapes.or(
                        makeCuboidShape(0,  0, 0,  4,  4,  4),
                        makeCuboidShape(0,  4, 0,  8,  8,  8),
                        makeCuboidShape(0,  8, 0, 12, 12, 12),
                        makeCuboidShape(0, 12, 0, 16, 16, 16)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = VoxelShapes.or(
                        makeCuboidShape(0,  0, 0, 16,  4, 16),
                        makeCuboidShape(0,  4, 0, 12,  8, 12),
                        makeCuboidShape(0,  8, 0,  8, 12,  8),
                        makeCuboidShape(0, 12, 0,  4, 16,  4)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerCornerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            CornerType type = state.get(PropertyHolder.CORNER_TYPE);
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (type.isHorizontal())
            {
                VoxelShape shapeBottomLeft = VoxelShapes.or(
                        makeCuboidShape(0, 0, 0, 16, 16, 4),
                        makeCuboidShape(0, 0, 4, 16, 12, 8),
                        makeCuboidShape(0, 0, 8, 16, 8, 12),
                        makeCuboidShape(0, 0, 12, 16, 4, 16),
                        makeCuboidShape(0, 8, 8, 8, 16, 12),
                        makeCuboidShape(0, 4, 12, 4, 16, 16),
                        makeCuboidShape(0, 12, 4, 12, 16, 8)
                ).simplify();

                VoxelShape shapeBottomRight = VoxelShapes.or(
                        makeCuboidShape(0, 0, 0, 16, 16, 4),
                        makeCuboidShape(0, 0, 4, 16, 12, 8),
                        makeCuboidShape(0, 0, 8, 16, 8, 12),
                        makeCuboidShape(0, 0, 12, 16, 4, 16),
                        makeCuboidShape(8, 8, 8, 16, 16, 12),
                        makeCuboidShape(12, 4, 12, 16, 16, 16),
                        makeCuboidShape(4, 12, 4, 16, 16, 8)
                ).simplify();

                VoxelShape shapeTopLeft = VoxelShapes.or(
                        makeCuboidShape(0, 0, 0, 16, 16, 4),
                        makeCuboidShape(0, 4, 4, 16, 16, 8),
                        makeCuboidShape(0, 8, 8, 16, 16, 12),
                        makeCuboidShape(0, 12, 12, 16, 16, 16),
                        makeCuboidShape(0, 0, 8, 8, 8, 12),
                        makeCuboidShape(0, 0, 12, 4, 12, 16),
                        makeCuboidShape(0, 0, 4, 12, 4, 8)
                ).simplify();

                VoxelShape shapeTopRight = VoxelShapes.or(
                        makeCuboidShape(0, 0, 0, 16, 16, 4),
                        makeCuboidShape(0, 4, 4, 16, 16, 8),
                        makeCuboidShape(0, 8, 8, 16, 16, 12),
                        makeCuboidShape(0, 12, 12, 16, 16, 16),
                        makeCuboidShape(8, 0, 8, 16, 8, 12),
                        makeCuboidShape(12, 0, 12, 16, 12, 16),
                        makeCuboidShape(4, 0, 4, 16, 4, 8)
                ).simplify();

                VoxelShape shape = VoxelShapes.fullCube();
                switch (type)
                {
                    case HORIZONTAL_BOTTOM_LEFT:
                        shape = shapeBottomLeft;
                        break;
                    case HORIZONTAL_BOTTOM_RIGHT:
                        shape = shapeBottomRight;
                        break;
                    case HORIZONTAL_TOP_LEFT:
                        shape = shapeTopLeft;
                        break;
                    case HORIZONTAL_TOP_RIGHT:
                        shape = shapeTopRight;
                        break;
                }
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
            }
            else if (type.isTop())
            {
                VoxelShape shapeTop = VoxelShapes.or(
                        makeCuboidShape( 0,  0,  0, 16,  4,  4),
                        makeCuboidShape(12,  0,  4, 16,  4, 16),
                        makeCuboidShape( 0,  4,  0, 16,  8,  8),
                        makeCuboidShape( 8,  4,  8, 16,  8, 16),
                        makeCuboidShape( 0,  8,  0, 16, 12, 12),
                        makeCuboidShape( 4,  8, 12, 16, 12, 16),
                        makeCuboidShape( 0, 12,  0, 16, 16, 16)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = VoxelShapes.or(
                        makeCuboidShape( 0,  0,  0, 16,  4, 16),
                        makeCuboidShape( 0,  4,  0, 16,  8, 12),
                        makeCuboidShape( 4,  4, 12, 16,  8, 16),
                        makeCuboidShape( 0,  8,  0, 16, 12,  8),
                        makeCuboidShape( 8,  8,  8, 16, 12, 16),
                        makeCuboidShape( 0, 12,  0, 16, 16,  4),
                        makeCuboidShape(12, 12,  4, 16, 16, 16)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }
}