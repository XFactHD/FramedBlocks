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
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.*;

/*
FIXME: BREAKING CHANGE!!!
FIXME: Fix inner threeway corner top/bottom rotation discrepancy from other corners (should be rotated 90 degree clockwise)
*/
public class FramedThreewayCornerBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        boolean top = state.get(PropertyHolder.TOP);
        if (top && dir == Direction.UP)
        {
            return true;
        }
        else if (!top && dir == Direction.DOWN)
        {
            return true;
        }

        Direction facing = state.get(PropertyHolder.FACING_HOR);
        if (facing == dir) { return true; }

        BlockType type = ((FramedBlock) state.getBlock()).getBlockType();
        if (type == BlockType.FRAMED_INNER_PRISM_CORNER) { return facing.rotateYCCW() == dir; }
        else { return facing.rotateY() == dir; }
    };

    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }

        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        boolean top = state.get(PropertyHolder.TOP);

        if (adjBlock == BlockType.FRAMED_PRISM_CORNER || adjBlock == BlockType.FRAMED_THREEWAY_CORNER)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if (side.getAxis() == Direction.Axis.Y && adjTop != top && adjDir == dir && (side == Direction.UP) == top)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjTop == top && ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY())))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

            if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir))
            {
                return adjType != SlopeType.HORIZONTAL && (adjType == SlopeType.TOP) == top && SideSkipPredicate.compareState(world, pos, side);
            }
            else if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
            {
                return adjType == SlopeType.HORIZONTAL && adjDir == dir && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_DOUBLE_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

            if (adjType != SlopeType.HORIZONTAL && (adjType == SlopeType.TOP) == top && ((side == dir.rotateYCCW() && adjDir == dir) || (side == dir && adjDir == dir.rotateYCCW())))
            {
                return SideSkipPredicate.compareState(world, pos, side, adjDir);
            }
            else if (adjType != SlopeType.HORIZONTAL && (adjType == SlopeType.TOP) != top &&
                     ((side == dir.rotateYCCW() && adjDir == dir.getOpposite()) || (side == dir && adjDir == dir.rotateY()))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, adjDir.getOpposite());
            }
            else if (adjType == SlopeType.HORIZONTAL && ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) && (adjDir == dir || adjDir == dir.getOpposite()))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

            if (((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY())) && !adjType.isHorizontal())
            {
                return adjType.isTop() == top && SideSkipPredicate.compareState(world, pos, side);
            }
            else if ((side == dir && adjDir == dir.rotateYCCW() && !adjType.isRight()) || (side == dir.rotateYCCW() && adjDir == dir && adjType.isRight()))
            {
                return adjType.isTop() == top && adjType.isHorizontal() && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (((!top && side == Direction.DOWN) || (top && side == Direction.UP)) &&
                     ((adjDir == dir.rotateYCCW() && adjType.isRight()) || (adjDir == dir && !adjType.isRight()))
            )
            {
                return adjType.isTop() != top && adjType.isHorizontal() && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_INNER_CORNER_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

            if (!adjType.isHorizontal() && adjDir == dir.rotateYCCW() && (side == dir || side == dir.rotateYCCW()) && adjType.isTop() == top)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjType.isHorizontal() && ((side == dir && adjType.isRight()) || (side == dir.rotateYCCW() && !adjType.isRight())) && adjType.isTop() == top)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjType.isHorizontal() && ((side == Direction.DOWN && !top) || (side == Direction.UP && top)) && adjType.isTop() == top)
            {
                return ((!adjType.isRight() && adjDir == dir) || (adjType.isRight() && adjDir == dir.rotateYCCW())) && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_INNER_PRISM_CORNER || adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if (adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER) { adjDir = adjDir.rotateY(); } //Correct rotation discrepancy of the threeway corner

            if (adjTop == top && adjDir == dir && (side == dir || side == dir.rotateYCCW() || (side == Direction.DOWN && !top) || (side == Direction.UP && top)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
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
        boolean top = state.get(PropertyHolder.TOP);

        if (((IFramedBlock)state.getBlock()).getBlockType() == BlockType.FRAMED_INNER_THREEWAY_CORNER) { dir = dir.rotateY(); } //Correct rotation discrepancy of the threeway corner

        if (adjBlock == BlockType.FRAMED_INNER_PRISM_CORNER || adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if (adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER) { adjDir = adjDir.rotateY(); } //Correct rotation discrepancy of the threeway corner

            if (adjTop == top && ((side == dir.rotateY() && adjDir == dir.rotateY()) || (side == dir.getOpposite() && adjDir == dir.rotateYCCW())))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjTop != top && adjDir == dir && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

            if (adjType != SlopeType.HORIZONTAL && ((side == dir.rotateY() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.rotateYCCW())))
            {
                return (adjType == SlopeType.TOP) == top && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjType == SlopeType.HORIZONTAL && adjDir == dir && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_DOUBLE_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

            if (adjType != SlopeType.HORIZONTAL)
            {
                if ((adjDir == dir && (adjType == SlopeType.TOP) == top) || (adjDir == dir.getOpposite() && (adjType == SlopeType.TOP) != top))
                {
                    return side == dir.rotateY() && SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if ((adjDir == dir.rotateYCCW() && (adjType == SlopeType.TOP) == top) || (adjDir == dir.rotateY() && (adjType == SlopeType.TOP) != top))
                {
                    return side == dir.getOpposite() && SideSkipPredicate.compareState(world, pos, side, dir.rotateYCCW());
                }
            }
            else if (adjDir == dir || adjDir == dir.getOpposite())
            {
                return ((side == Direction.UP && !top) || (side == Direction.DOWN && top)) && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

            if (!adjType.isHorizontal() && adjType.isTop() == top && adjDir == dir && (side == dir.rotateY() || side == dir.getOpposite()))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjType.isHorizontal() && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)) &&
                     ((adjDir == dir && !adjType.isRight()) || (adjDir == dir.rotateYCCW() && adjType.isRight()))
            )
            {
                return adjType.isTop() == top && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjType.isHorizontal() && ((side == dir.rotateY() && adjDir == dir && !adjType.isRight()) ||
                     (side == dir.getOpposite() && adjDir == dir.rotateYCCW() && adjType.isRight()))
            )
            {
                return adjType.isTop() == top && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        else if (adjBlock == BlockType.FRAMED_INNER_CORNER_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

            if (!adjType.isHorizontal() && adjType.isTop() == top && ((side == dir.rotateY() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getOpposite())))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjType.isHorizontal() && adjType.isTop() != top && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)) &&
                    ((adjDir == dir && !adjType.isRight()) || (adjDir == dir.rotateYCCW() && adjType.isRight()))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (adjType.isHorizontal() && adjType.isTop() == top && ((side == dir.rotateY() && adjDir == dir && adjType.isRight()) ||
                     (side == dir.getOpposite() && adjDir == dir.rotateYCCW() && !adjType.isRight()))
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

            if (adjTop == top && adjDir == dir && (side == dir.rotateY() || side == dir.getOpposite() || (side == Direction.UP && !top) || (side == Direction.DOWN && top)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        return false;
    };

    public FramedThreewayCornerBlock(String name, BlockType type)
    {
        super(name, type);
        setDefaultState(getDefaultState().with(PropertyHolder.TOP, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();

        Direction facing = context.getPlacementHorizontalFacing();
        if (getBlockType() == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            facing = facing.rotateYCCW();
        }
        state = state.with(PropertyHolder.FACING_HOR, facing);

        state = withWater(state, context.getWorld(), context.getPos());
        return withTop(state, context.getFace(), context.getHitVec());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateThreewayShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (state.get(PropertyHolder.TOP))
            {
                VoxelShape shapeTop = VoxelShapes.or(
                        makeCuboidShape(0, 12, 0, 4, 16, 16),
                        makeCuboidShape(0, 8, 0, 4, 12, 12),
                        makeCuboidShape(0, 4, 0, 8, 8, 8),
                        makeCuboidShape(0, 0, 0, 4, 4, 4),
                        makeCuboidShape(4, 12, 0, 8, 16, 12),
                        makeCuboidShape(4, 8, 0, 8, 12, 12),
                        makeCuboidShape(8, 12, 0, 12, 16, 8),
                        makeCuboidShape(8, 8, 0, 12, 12, 8),
                        makeCuboidShape(12, 12, 0, 16, 16, 4)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = VoxelShapes.or(
                        makeCuboidShape(0, 0, 0, 4, 4, 16),
                        makeCuboidShape(0, 4, 0, 4, 8, 12),
                        makeCuboidShape(0, 8, 0, 8, 12, 8),
                        makeCuboidShape(0, 12, 0, 4, 16, 4),
                        makeCuboidShape(4, 0, 0, 8, 4, 12),
                        makeCuboidShape(4, 4, 0, 8, 8, 12),
                        makeCuboidShape(8, 0, 0, 12, 4, 8),
                        makeCuboidShape(8, 4, 0, 12, 8, 8),
                        makeCuboidShape(12, 0, 0, 16, 4, 4)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerThreewayShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (state.get(PropertyHolder.TOP))
            {
                VoxelShape shapeTop = VoxelShapes.or(
                        makeCuboidShape(4, 8, 12, 16, 12, 16),
                        makeCuboidShape(0, 12, 0, 16, 16, 16),
                        makeCuboidShape(0, 8, 0, 16, 12, 12),
                        makeCuboidShape(0, 4, 0, 16, 8, 8),
                        makeCuboidShape(0, 0, 0, 16, 4, 4),
                        makeCuboidShape(12, 0, 4, 16, 4, 16),
                        makeCuboidShape(8, 4, 8, 16, 8, 16),
                        makeCuboidShape(4, 0, 4, 8, 4, 8),
                        makeCuboidShape(8, 0, 8, 12, 4, 12),
                        makeCuboidShape(8, 0, 4, 12, 4, 8)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = VoxelShapes.or(
                        makeCuboidShape(4, 4, 12, 16, 8, 16),
                        makeCuboidShape(0, 0, 0, 16, 4, 16),
                        makeCuboidShape(0, 4, 0, 16, 8, 12),
                        makeCuboidShape(0, 8, 0, 16, 12, 8),
                        makeCuboidShape(0, 12, 0, 16, 16, 4),
                        makeCuboidShape(12, 12, 4, 16, 16, 16),
                        makeCuboidShape(8, 8, 8, 16, 12, 16),
                        makeCuboidShape(4, 12, 4, 8, 16, 8),
                        makeCuboidShape(8, 12, 8, 12, 16, 12),
                        makeCuboidShape(8, 12, 4, 12, 16, 8)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }
}