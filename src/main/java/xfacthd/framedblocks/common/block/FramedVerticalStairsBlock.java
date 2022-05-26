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
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.*;

import java.util.stream.Stream;

public class FramedVerticalStairsBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        if (type == StairsType.VERTICAL)
        {
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);
            return side == dir || side == dir.getCounterClockWise();
        }
        return false;
    };

    public FramedVerticalStairsBlock() { super(BlockType.FRAMED_VERTICAL_STAIRS); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.STAIRS_TYPE, BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID, PropertyHolder.GLOWING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState().setValue(PropertyHolder.FACING_HOR, context.getHorizontalDirection());
        return getStateFromContext(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        if (facing == dir.getOpposite() || facing == dir.getClockWise()) { return state; }

        return getStateFromContext(state, world, pos);
    }

    private BlockState getStateFromContext(BlockState state, IWorld world, BlockPos pos)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);

        BlockState front = world.getBlockState(pos.relative(dir));
        BlockState left = world.getBlockState(pos.relative(dir.getCounterClockWise()));

        if (isNoStair(front) && isNoStair(left))
        {
            return state.setValue(PropertyHolder.STAIRS_TYPE, StairsType.VERTICAL);
        }
        else
        {
            StairsType type;

            boolean topCorner = false;
            boolean bottomCorner = false;

            if (front.getBlock() instanceof StairsBlock && front.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir.getCounterClockWise())
            {
                topCorner = front.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCorner = front.getValue(BlockStateProperties.HALF) == Half.TOP;
            }
            else if (front.getBlock() instanceof FramedHalfStairsBlock && front.getValue(PropertyHolder.FACING_HOR) == dir.getCounterClockWise())
            {
                boolean top = front.getValue(PropertyHolder.TOP);

                if (!front.getValue(PropertyHolder.RIGHT))
                {
                    topCorner = !top;
                    bottomCorner = top;
                }
            }

            if (left.getBlock() instanceof StairsBlock && left.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir)
            {
                topCorner |= left.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCorner |= left.getValue(BlockStateProperties.HALF) == Half.TOP;
            }
            else if (left.getBlock() instanceof FramedHalfStairsBlock && left.getValue(PropertyHolder.FACING_HOR) == dir)
            {
                boolean top = left.getValue(PropertyHolder.TOP);

                if (left.getValue(PropertyHolder.RIGHT))
                {
                    topCorner = !top;
                    bottomCorner = top;
                }
            }

            BlockState above = world.getBlockState(pos.above());
            BlockState below = world.getBlockState(pos.below());

            if (topCorner && !above.is(this)) { type = StairsType.TOP_CORNER; }
            else if (bottomCorner && !below.is(this)) { type = StairsType.BOTTOM_CORNER; }
            else { type = StairsType.VERTICAL; }

            return state.setValue(PropertyHolder.STAIRS_TYPE, type);
        }
    }

    private static boolean isNoStair(BlockState state)
    {
        return !(state.getBlock() instanceof StairsBlock) && !(state.getBlock() instanceof FramedHalfStairsBlock);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape vertShape = VoxelShapes.join(
                Block.box(0, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 16, 8),
                IBooleanFunction.OR
        );

        VoxelShape topCornerShape = Stream.of(
                Block.box(8, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 8, 8),
                Block.box(0, 0, 8, 8, 8, 16)
        ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

        VoxelShape bottomCornerShape = Stream.of(
                Block.box(8, 0, 8, 16, 16, 16),
                Block.box(8, 8, 0, 16, 16, 8),
                Block.box(0, 8, 8, 8, 16, 16)
        ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

        for (BlockState state : states)
        {
            StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
            Direction dir = state.getValue(PropertyHolder.FACING_HOR).getOpposite();

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