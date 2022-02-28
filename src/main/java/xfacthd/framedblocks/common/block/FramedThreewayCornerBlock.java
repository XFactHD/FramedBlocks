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

public class FramedThreewayCornerBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        boolean top = state.getValue(PropertyHolder.TOP);
        if (top && dir == Direction.UP)
        {
            return true;
        }
        else if (!top && dir == Direction.DOWN)
        {
            return true;
        }

        Direction facing = state.getValue(PropertyHolder.FACING_HOR);
        if (facing == dir) { return true; }

        BlockType type = ((FramedBlock) state.getBlock()).getBlockType();
        if (type == BlockType.FRAMED_INNER_PRISM_CORNER) { return facing.getCounterClockWise() == dir; }
        else { return facing.getClockWise() == dir; }
    };

    public FramedThreewayCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();

        Direction facing = context.getHorizontalDirection();
        if (getBlockType() == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            facing = facing.getCounterClockWise();
        }
        state = state.setValue(PropertyHolder.FACING_HOR, facing);

        state = withWater(state, context.getLevel(), context.getClickedPos());
        return withTop(state, context.getClickedFace(), context.getClickLocation());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateThreewayShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

            if (state.getValue(PropertyHolder.TOP))
            {
                VoxelShape shapeTop = VoxelShapes.or(
                        box(0, 12, 0, 4, 16, 16),
                        box(0, 8, 0, 4, 12, 12),
                        box(0, 4, 0, 8, 8, 8),
                        box(0, 0, 0, 4, 4, 4),
                        box(4, 12, 0, 8, 16, 12),
                        box(4, 8, 0, 8, 12, 12),
                        box(8, 12, 0, 12, 16, 8),
                        box(8, 8, 0, 12, 12, 8),
                        box(12, 12, 0, 16, 16, 4)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = VoxelShapes.or(
                        box(0, 0, 0, 4, 4, 16),
                        box(0, 4, 0, 4, 8, 12),
                        box(0, 8, 0, 8, 12, 8),
                        box(0, 12, 0, 4, 16, 4),
                        box(4, 0, 0, 8, 4, 12),
                        box(4, 4, 0, 8, 8, 12),
                        box(8, 0, 0, 12, 4, 8),
                        box(8, 4, 0, 12, 8, 8),
                        box(12, 0, 0, 16, 4, 4)
                ).optimize();

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
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

            if (state.getValue(PropertyHolder.TOP))
            {
                VoxelShape shapeTop = VoxelShapes.or(
                        box(4, 8, 12, 16, 12, 16),
                        box(0, 12, 0, 16, 16, 16),
                        box(0, 8, 0, 16, 12, 12),
                        box(0, 4, 0, 16, 8, 8),
                        box(0, 0, 0, 16, 4, 4),
                        box(12, 0, 4, 16, 4, 16),
                        box(8, 4, 8, 16, 8, 16),
                        box(4, 0, 4, 8, 4, 8),
                        box(8, 0, 8, 12, 4, 12),
                        box(8, 0, 4, 12, 4, 8)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = VoxelShapes.or(
                        box(4, 4, 12, 16, 8, 16),
                        box(0, 0, 0, 16, 4, 16),
                        box(0, 4, 0, 16, 8, 12),
                        box(0, 8, 0, 16, 12, 8),
                        box(0, 12, 0, 16, 16, 4),
                        box(12, 12, 4, 16, 16, 16),
                        box(8, 8, 8, 16, 12, 16),
                        box(4, 12, 4, 8, 16, 8),
                        box(8, 12, 8, 12, 16, 12),
                        box(8, 12, 4, 12, 16, 8)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }
}