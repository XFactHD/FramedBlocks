package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.StairsType;

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

    public FramedVerticalStairsBlock()
    {
        super(BlockType.FRAMED_VERTICAL_STAIRS);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.STATE_LOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.STAIRS_TYPE, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.GLOWING, FramedProperties.STATE_LOCKED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState().setValue(PropertyHolder.FACING_HOR, context.getHorizontalDirection());
        return getStateFromContext(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        if (facing != dir.getOpposite() && facing != dir.getClockWise())
        {
            state = getStateFromContext(state, level, pos);
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public void onStateChangeClient(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState, FramedBlockEntity be)
    {
        super.onStateChangeClient(level, pos, oldState, newState, be);

        if (needCullingUpdateAfterStateChange(level, oldState, newState))
        {
            be.updateCulling(false, false);
        }
    }

    private BlockState getStateFromContext(BlockState state, LevelAccessor level, BlockPos pos)
    {
        if (state.getValue(FramedProperties.STATE_LOCKED)) { return state; }

        Direction dir = state.getValue(PropertyHolder.FACING_HOR);

        BlockState front = level.getBlockState(pos.relative(dir));
        BlockState left = level.getBlockState(pos.relative(dir.getCounterClockWise()));

        if (isNoStair(front) && isNoStair(left))
        {
            return state.setValue(PropertyHolder.STAIRS_TYPE, StairsType.VERTICAL);
        }
        else
        {
            StairsType type;

            boolean topCorner = false;
            boolean bottomCorner = false;

            if (front.getBlock() instanceof StairBlock && front.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir.getCounterClockWise())
            {
                topCorner = front.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCorner = front.getValue(BlockStateProperties.HALF) == Half.TOP;
            }
            else if (front.getBlock() instanceof FramedHalfStairsBlock && front.getValue(FramedProperties.FACING_HOR) == dir.getCounterClockWise())
            {
                boolean top = front.getValue(FramedProperties.TOP);

                if (!front.getValue(PropertyHolder.RIGHT))
                {
                    topCorner = !top;
                    bottomCorner = top;
                }
            }

            if (left.getBlock() instanceof StairBlock && left.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir)
            {
                topCorner |= left.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCorner |= left.getValue(BlockStateProperties.HALF) == Half.TOP;
            }
            else if (left.getBlock() instanceof FramedHalfStairsBlock && left.getValue(FramedProperties.FACING_HOR) == dir)
            {
                boolean top = left.getValue(FramedProperties.TOP);

                if (left.getValue(PropertyHolder.RIGHT))
                {
                    topCorner = !top;
                    bottomCorner = top;
                }
            }

            BlockState above = level.getBlockState(pos.above());
            BlockState below = level.getBlockState(pos.below());

            if (topCorner && !above.is(this)) { type = StairsType.TOP_CORNER; }
            else if (bottomCorner && !below.is(this)) { type = StairsType.BOTTOM_CORNER; }
            else { type = StairsType.VERTICAL; }

            return state.setValue(PropertyHolder.STAIRS_TYPE, type);
        }
    }

    private static boolean isNoStair(BlockState state)
    {
        return !(state.getBlock() instanceof StairBlock) && !(state.getBlock() instanceof FramedHalfStairsBlock);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape vertShape = Shapes.join(
                Block.box(0, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 16, 8),
                BooleanOp.OR
        );

        VoxelShape topCornerShape = Stream.of(
                Block.box(8, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 8, 8),
                Block.box(0, 0, 8, 8, 8, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        VoxelShape bottomCornerShape = Stream.of(
                Block.box(8, 0, 8, 16, 16, 16),
                Block.box(8, 8, 0, 16, 16, 8),
                Block.box(0, 8, 8, 8, 16, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

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