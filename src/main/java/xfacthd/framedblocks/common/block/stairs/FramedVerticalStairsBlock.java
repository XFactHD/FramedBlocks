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
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
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
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfOrHorizontalFacing()
                .withCustom((state, modCtx) -> getStateFromContext(state, modCtx.getLevel(), modCtx.getClickedPos()))
                .build();
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

    private static BlockState getStateFromContext(BlockState state, LevelAccessor level, BlockPos pos)
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
            boolean topCornerFront = false;
            boolean bottomCornerFront = false;

            if (front.getBlock() instanceof StairBlock && front.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir.getCounterClockWise())
            {
                topCornerFront = front.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCornerFront = front.getValue(BlockStateProperties.HALF) == Half.TOP;
            }
            else if (front.getBlock() instanceof FramedHalfStairsBlock && front.getValue(FramedProperties.FACING_HOR) == dir.getCounterClockWise())
            {
                boolean top = front.getValue(FramedProperties.TOP);

                if (!front.getValue(PropertyHolder.RIGHT))
                {
                    topCornerFront = !top;
                    bottomCornerFront = top;
                }
            }

            boolean topCornerLeft = false;
            boolean bottomCornerLeft = false;

            if (left.getBlock() instanceof StairBlock && left.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir)
            {
                topCornerLeft = left.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCornerLeft = left.getValue(BlockStateProperties.HALF) == Half.TOP;
            }
            else if (left.getBlock() instanceof FramedHalfStairsBlock && left.getValue(FramedProperties.FACING_HOR) == dir)
            {
                boolean top = left.getValue(FramedProperties.TOP);

                if (left.getValue(PropertyHolder.RIGHT))
                {
                    topCornerLeft = !top;
                    bottomCornerLeft = top;
                }
            }

            BlockState above = level.getBlockState(pos.above());
            BlockState below = level.getBlockState(pos.below());

            StairsType type = StairsType.VERTICAL;
            if ((topCornerFront || topCornerLeft) && !above.is(state.getBlock()))
            {
                if (!topCornerLeft)
                {
                    type = StairsType.TOP_FWD;
                }
                else if (!topCornerFront)
                {
                    type = StairsType.TOP_CCW;
                }
                else
                {
                    type = StairsType.TOP_BOTH;
                }
            }
            else if ((bottomCornerFront || bottomCornerLeft) && !below.is(state.getBlock()))
            {
                if (!bottomCornerLeft)
                {
                    type = StairsType.BOTTOM_FWD;
                }
                else if (!bottomCornerFront)
                {
                    type = StairsType.BOTTOM_CCW;
                }
                else
                {
                    type = StairsType.BOTTOM_BOTH;
                }
            }

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

        VoxelShape topFwdShape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 8, 16, 16),
                Block.box(8, 0, 0, 16, 8, 8)
        );

        VoxelShape topCcwShape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 16, 16, 8),
                Block.box(0, 0, 8, 8, 8, 16)
        );

        VoxelShape topBothShape = ShapeUtils.orUnoptimized(
                Block.box(8, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 8, 8),
                Block.box(0, 0, 8, 8, 8, 16)
        );

        VoxelShape bottomFwdShape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 8, 16, 16),
                Block.box(8, 8, 0, 16, 16, 8)
        );

        VoxelShape bottomCcwShape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 16, 16, 8),
                Block.box(0, 8, 8, 8, 16, 16)
        );

        VoxelShape bottomBothShape = ShapeUtils.orUnoptimized(
                Block.box(8, 0, 8, 16, 16, 16),
                Block.box(8, 8, 0, 16, 16, 8),
                Block.box(0, 8, 8, 8, 16, 16)
        );

        ShapeUtils.makeHorizontalRotations(vertShape, Direction.SOUTH, map, StairsType.VERTICAL, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(topFwdShape, Direction.NORTH, map, StairsType.TOP_FWD, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(topCcwShape, Direction.NORTH, map, StairsType.TOP_CCW, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(topBothShape, Direction.SOUTH, map, StairsType.TOP_BOTH, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(bottomFwdShape, Direction.NORTH, map, StairsType.BOTTOM_FWD, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(bottomCcwShape, Direction.NORTH, map, StairsType.BOTTOM_CCW, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(bottomBothShape, Direction.SOUTH, map, StairsType.BOTTOM_BOTH, ShapeKey::new);
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