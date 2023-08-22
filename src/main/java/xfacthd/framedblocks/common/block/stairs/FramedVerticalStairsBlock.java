package xfacthd.framedblocks.common.block.stairs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.StairsType;

public class FramedVerticalStairsBlock extends FramedBlock
{
    public FramedVerticalStairsBlock()
    {
        super(BlockType.FRAMED_VERTICAL_STAIRS);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.STATE_LOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.STAIRS_TYPE, BlockStateProperties.WATERLOGGED,
                FramedProperties.SOLID, FramedProperties.STATE_LOCKED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = withCornerFacing(
                defaultBlockState(),
                context.getClickedFace(),
                context.getHorizontalDirection(),
                context.getClickLocation()
        );
        return getStateFromContext(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (facing != dir.getOpposite() && facing != dir.getClockWise())
        {
            state = getStateFromContext(state, level, pos);
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    private BlockState getStateFromContext(BlockState state, LevelAccessor level, BlockPos pos)
    {
        if (state.getValue(FramedProperties.STATE_LOCKED))
        {
            return state;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);

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

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }



    public record ShapeKey(Direction dir, StairsType type) { }

    public static final ShapeCache<ShapeKey> SHAPES = new ShapeCache<>(map ->
    {
        VoxelShape vertShape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 16, 8)
        );

        VoxelShape topCornerShape = ShapeUtils.orUnoptimized(
                Block.box(8, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 8, 8),
                Block.box(0, 0, 8, 8, 8, 16)
        );

        VoxelShape bottomCornerShape = ShapeUtils.orUnoptimized(
                Block.box(8, 0, 8, 16, 16, 16),
                Block.box(8, 8, 0, 16, 16, 8),
                Block.box(0, 8, 8, 8, 16, 16)
        );

        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            map.put(new ShapeKey(dir, StairsType.VERTICAL), ShapeUtils.rotateShape(Direction.SOUTH, dir, vertShape));
            map.put(new ShapeKey(dir, StairsType.TOP_CORNER), ShapeUtils.rotateShape(
                    Direction.SOUTH, dir, topCornerShape
            ));
            map.put(new ShapeKey(dir, StairsType.BOTTOM_CORNER), ShapeUtils.rotateShape(
                    Direction.SOUTH, dir, bottomCornerShape
            ));
        }
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
            builder.put(state, SHAPES.get(new ShapeKey(dir, type)));
        }

        return ShapeProvider.of(builder.build());
    }
}