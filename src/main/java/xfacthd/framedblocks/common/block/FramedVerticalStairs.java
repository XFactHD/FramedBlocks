package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IWorld;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.*;

import java.util.stream.Stream;

public class FramedVerticalStairs extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        StairsType type = state.get(PropertyHolder.STAIRS_TYPE);
        if (type == StairsType.VERTICAL)
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);
            return side == dir || side == dir.rotateYCCW();
        }
        return false;
    };

    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }

        StairsType type = state.get(PropertyHolder.STAIRS_TYPE);
        Direction dir = state.get(PropertyHolder.FACING_HOR);

        if (adjState.getBlock() == FBContent.blockFramedVerticalStairs.get())
        {
            StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);

            if ((!type.isBottom() && !adjType.isTop() && side == Direction.DOWN) || (!type.isTop() && !adjType.isBottom() && side == Direction.UP))
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }

            if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }

            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            Direction adjDir = adjState.get(BlockStateProperties.HORIZONTAL_FACING);
            boolean adjBottom = adjState.get(BlockStateProperties.HALF) == Half.BOTTOM;

            if (type == StairsType.VERTICAL && ((side == Direction.UP && !adjBottom) || (side == Direction.DOWN && adjBottom)))
            {
                StairsShape adjShape = adjState.get(BlockStateProperties.STAIRS_SHAPE);
                if ((adjDir == dir && adjShape == StairsShape.INNER_LEFT) || (adjDir == dir.rotateYCCW() && adjShape == StairsShape.INNER_RIGHT))
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
            }
            else
            {
                if (type.isTop() == adjBottom && ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir)))
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedPanel.get() && type == StairsType.VERTICAL)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if ((side == dir.rotateY() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.rotateYCCW()))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedDoublePanel.get() && type == StairsType.VERTICAL)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
            if (side == dir.rotateY() && (adjDir == dir || adjDir == dir.getOpposite()))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            if (side == dir.getOpposite() && (adjDir == dir.rotateYCCW() || adjDir == dir.rotateY()))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir.rotateYCCW());
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedSlabCorner.get() && type != StairsType.VERTICAL)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if ((side.getAxis() == Direction.Axis.Y || side == dir.getOpposite() || side == dir.rotateY()) && type.isTop() != adjTop && dir == adjDir)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedCornerPillar.get())
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if (type == StairsType.VERTICAL)
            {
                if ((side == dir.rotateY() || side == dir.getOpposite()) && adjDir == dir)
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
            }
            else if (side.getAxis() == Direction.Axis.Y)
            {
                if ((side == Direction.UP) == type.isTop() && adjDir == dir)
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get() && type != StairsType.VERTICAL && side.getAxis() != Direction.Axis.Y)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);

            if ((side == dir.rotateY() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.rotateYCCW()))
            {
                return adjTop != type.isTop() && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        return false;
    };

    public FramedVerticalStairs() { super(BlockType.FRAMED_VERTICAL_STAIRS); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.STAIRS_TYPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState().with(PropertyHolder.FACING_HOR, context.getPlacementHorizontalFacing());
        return getStateFromContext(state, context.getWorld(), context.getPos());
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        if (facing == dir.getOpposite() || facing == dir.rotateY()) { return state; }

        return getStateFromContext(state, world, pos);
    }

    private BlockState getStateFromContext(BlockState state, IWorld world, BlockPos pos)
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);

        BlockState front = world.getBlockState(pos.offset(dir));
        BlockState left = world.getBlockState(pos.offset(dir.rotateYCCW()));

        if (!(front.getBlock() instanceof StairsBlock) && !(left.getBlock() instanceof StairsBlock))
        {
            return state.with(PropertyHolder.STAIRS_TYPE, StairsType.VERTICAL);
        }
        else
        {
            StairsType type;

            boolean topCorner = false;
            boolean bottomCorner = false;

            if (front.getBlock() instanceof StairsBlock && front.get(BlockStateProperties.HORIZONTAL_FACING) == dir.rotateYCCW())
            {
                topCorner = front.get(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCorner = front.get(BlockStateProperties.HALF) == Half.TOP;
            }

            if (left.getBlock() instanceof StairsBlock && left.get(BlockStateProperties.HORIZONTAL_FACING) == dir)
            {
                topCorner |= left.get(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCorner |= left.get(BlockStateProperties.HALF) == Half.TOP;
            }

            BlockState above = world.getBlockState(pos.up());
            BlockState below = world.getBlockState(pos.down());

            if (topCorner && !above.matchesBlock(this)) { type = StairsType.TOP_CORNER; }
            else if (bottomCorner && !below.matchesBlock(this)) { type = StairsType.BOTTOM_CORNER; }
            else { type = StairsType.VERTICAL; }

            return state.with(PropertyHolder.STAIRS_TYPE, type);
        }
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape vertShape = VoxelShapes.combineAndSimplify(
                Block.makeCuboidShape(0, 0, 8, 16, 16, 16),
                Block.makeCuboidShape(8, 0, 0, 16, 16, 8),
                IBooleanFunction.OR
        );

        VoxelShape topCornerShape = Stream.of(
                Block.makeCuboidShape(8, 0, 8, 16, 16, 16),
                Block.makeCuboidShape(8, 0, 0, 16, 8, 8),
                Block.makeCuboidShape(0, 0, 8, 8, 8, 16)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

        VoxelShape bottomCornerShape = Stream.of(
                Block.makeCuboidShape(8, 0, 8, 16, 16, 16),
                Block.makeCuboidShape(8, 8, 0, 16, 16, 8),
                Block.makeCuboidShape(0, 8, 8, 8, 16, 16)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

        for (BlockState state : states)
        {
            StairsType type = state.get(PropertyHolder.STAIRS_TYPE);
            Direction dir = state.get(PropertyHolder.FACING_HOR).getOpposite();

            if (type == StairsType.TOP_CORNER)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, topCornerShape));
            }
            else if (type == StairsType.BOTTOM_CORNER)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, bottomCornerShape));
            }
            else
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, vertShape));
            }
        }

        return builder.build();
    }
}