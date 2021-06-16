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

public class FramedSlopeBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        SlopeType type = state.get(PropertyHolder.SLOPE_TYPE);
        if (dir == Direction.UP && type == SlopeType.TOP)
        {
            return true;
        }
        else if (dir == Direction.DOWN && type == SlopeType.BOTTOM)
        {
            return true;
        }
        else if (type == SlopeType.HORIZONTAL)
        {
            Direction facing = state.get(PropertyHolder.FACING_HOR);
            return dir == facing || dir == facing.rotateYCCW();
        }
        return state.get(PropertyHolder.FACING_HOR) == dir;
    };

    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }

        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        SlopeType type = state.get(PropertyHolder.SLOPE_TYPE);

        if (adjBlock == BlockType.FRAMED_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

            if (type == SlopeType.HORIZONTAL && side.getAxis() == Direction.Axis.Y)
            {
                return dir == adjDir && type == adjType && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (type != SlopeType.HORIZONTAL && (side == dir.rotateY() || side == dir.rotateYCCW()))
            {
                return dir == adjDir && type == adjType && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }

        else if (adjBlock == BlockType.FRAMED_DOUBLE_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

            if (type == SlopeType.HORIZONTAL && adjType == SlopeType.HORIZONTAL && side.getAxis() == Direction.Axis.Y)
            {
                return (dir == adjDir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (type != SlopeType.HORIZONTAL && adjType != SlopeType.HORIZONTAL && (side == dir.rotateY() || side == dir.rotateYCCW()))
            {
                return (dir == adjDir && type == adjType) || (dir.getOpposite() == adjDir && type != adjType) && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }

        else if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

            if (side == dir.rotateY() && adjDir == dir)
            {
                if (type == SlopeType.BOTTOM && (adjType == CornerType.BOTTOM || adjType == CornerType.HORIZONTAL_BOTTOM_LEFT))
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if (type == SlopeType.TOP && (adjType == CornerType.TOP || adjType == CornerType.HORIZONTAL_TOP_LEFT))
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
            else if (side == dir.rotateYCCW())
            {
                if (adjDir == dir)
                {
                    if (type == SlopeType.BOTTOM && adjType == CornerType.HORIZONTAL_BOTTOM_RIGHT)
                    {
                        return SideSkipPredicate.compareState(world, pos, side, dir);
                    }
                    else if (type == SlopeType.TOP && adjType == CornerType.HORIZONTAL_TOP_RIGHT)
                    {
                        return SideSkipPredicate.compareState(world, pos, side, dir);
                    }
                }
                else if (adjDir == dir.rotateY())
                {
                    if (type == SlopeType.BOTTOM && adjType == CornerType.BOTTOM)
                    {
                        return SideSkipPredicate.compareState(world, pos, side, dir);
                    }
                    else if (type == SlopeType.TOP && adjType == CornerType.TOP)
                    {
                        return SideSkipPredicate.compareState(world, pos, side, dir);
                    }
                }
            }
            else if (side.getAxis() == Direction.Axis.Y && type == SlopeType.HORIZONTAL && ((side == Direction.UP) != (adjType.isTop())))
            {
                if (adjType.isRight())
                {
                    return dir == adjDir.rotateY() && SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else
                {
                    return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
        }

        else if (adjBlock == BlockType.FRAMED_INNER_CORNER_SLOPE)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

            if (side == dir.rotateY() && adjDir == dir)
            {
                if (type == SlopeType.BOTTOM && (adjType == CornerType.BOTTOM || adjType == CornerType.HORIZONTAL_BOTTOM_RIGHT))
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if (type == SlopeType.TOP && (adjType == CornerType.TOP || adjType == CornerType.HORIZONTAL_TOP_RIGHT))
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
            else if (side == dir.rotateYCCW())
            {
                if (adjDir == dir)
                {
                    if (type == SlopeType.BOTTOM && adjType == CornerType.HORIZONTAL_BOTTOM_LEFT)
                    {
                        return SideSkipPredicate.compareState(world, pos, side, dir);
                    }
                    else if (type == SlopeType.TOP && adjType == CornerType.HORIZONTAL_TOP_LEFT)
                    {
                        return SideSkipPredicate.compareState(world, pos, side, dir);
                    }
                }
                else if (adjDir == dir.rotateYCCW())
                {
                    if (type == SlopeType.BOTTOM && adjType == CornerType.BOTTOM)
                    {
                        return SideSkipPredicate.compareState(world, pos, side, dir);
                    }
                    else if (type == SlopeType.TOP && adjType == CornerType.TOP)
                    {
                        return SideSkipPredicate.compareState(world, pos, side, dir);
                    }
                }
            }
            else if (side.getAxis() == Direction.Axis.Y && type == SlopeType.HORIZONTAL && ((side == Direction.UP) == (adjType.isTop())))
            {
                if (adjType.isRight())
                {
                    return dir == adjDir.rotateY() && SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else
                {
                    return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
        }

        else if (adjBlock == BlockType.FRAMED_PRISM_CORNER || adjBlock == BlockType.FRAMED_THREEWAY_CORNER)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if (type != SlopeType.HORIZONTAL && adjTop == (type == SlopeType.TOP))
            {
                if (side == dir.rotateY())
                {
                    return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if (side == dir.rotateYCCW())
                {
                    return adjDir == dir.rotateY() && SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
            else if (type == SlopeType.HORIZONTAL && side.getAxis() == Direction.Axis.Y && adjTop == (side == Direction.DOWN))
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }

        else if (adjBlock == BlockType.FRAMED_INNER_PRISM_CORNER || adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if (adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER) { adjDir = adjDir.rotateY(); }

            if (type != SlopeType.HORIZONTAL && adjTop == (type == SlopeType.TOP))
            {
                if (side == dir.rotateY())
                {
                    return adjDir == dir.rotateY() && SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if (side == dir.rotateYCCW())
                {
                    return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
            else if (type == SlopeType.HORIZONTAL && side.getAxis() == Direction.Axis.Y && adjTop == (side == Direction.UP))
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }

        return false;
    };

    public FramedSlopeBlock() { super("framed_slope", BlockType.FRAMED_SLOPE); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.SLOPE_TYPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = withSlopeType(getDefaultState(), context.getFace(), context.getPlacementHorizontalFacing(), context.getHitVec());
        return withWater(state, context.getWorld(), context.getPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBottom = VoxelShapes.or(
                makeCuboidShape(0,  0, 0, 16,  4, 16),
                makeCuboidShape(0,  4, 0, 16,  8, 12),
                makeCuboidShape(0,  8, 0, 16, 12,  8),
                makeCuboidShape(0, 12, 0, 16, 16,  4)
        ).simplify();

        VoxelShape shapeTop = VoxelShapes.or(
                makeCuboidShape(0,  0, 0, 16,  4,  4),
                makeCuboidShape(0,  4, 0, 16,  8,  8),
                makeCuboidShape(0,  8, 0, 16, 12, 12),
                makeCuboidShape(0, 12, 0, 16, 16, 16)
        ).simplify();

        VoxelShape shapeHorizontal = VoxelShapes.or(
                makeCuboidShape( 0, 0, 0,  4, 16, 16),
                makeCuboidShape( 4, 0, 0,  8, 16, 12),
                makeCuboidShape( 8, 0, 0, 12, 16,  8),
                makeCuboidShape(12, 0, 0, 16, 16,  4)
        ).simplify();

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            SlopeType type = state.get(PropertyHolder.SLOPE_TYPE);
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (type == SlopeType.BOTTOM)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
            else if (type == SlopeType.TOP)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeHorizontal));
            }
        }

        return builder.build();
    }
}