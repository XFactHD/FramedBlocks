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
import xfacthd.framedblocks.common.util.Utils;

import java.util.function.BiPredicate;

public class FramedThreewayCornerBlock extends FramedBlock
{
    public static final BiPredicate<BlockState, Direction> CTM_PREDICATE = (state, dir) ->
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

    public FramedThreewayCornerBlock(String name, BlockType type) { super(name, type); }

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
        state = state.with(PropertyHolder.FACING_HOR, facing);

        state = withWater(state, context.getWorld(), context.getPos());
        return withTop(state, context.getFace(), context.getHitVec());
    }

    public static ImmutableMap<BlockState, VoxelShape> generatePrismShapes(ImmutableList<BlockState> states)
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
                        makeCuboidShape(0, 4, 0, 4, 8, 8),
                        makeCuboidShape(0, 0, 0, 4, 4, 4),
                        makeCuboidShape(4, 12, 0, 8, 16, 12),
                        makeCuboidShape(4, 8, 0, 8, 12, 8),
                        makeCuboidShape(4, 4, 0, 8, 8, 4),
                        makeCuboidShape(8, 12, 0, 12, 16, 8),
                        makeCuboidShape(8, 8, 0, 12, 12, 4),
                        makeCuboidShape(12, 12, 0, 16, 16, 4)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = VoxelShapes.or(
                        makeCuboidShape(0, 0, 0, 4, 4, 16),
                        makeCuboidShape(0, 4, 0, 4, 8, 12),
                        makeCuboidShape(0, 8, 0, 4, 12, 8),
                        makeCuboidShape(0, 12, 0, 4, 16, 4),
                        makeCuboidShape(4, 0, 0, 8, 4, 12),
                        makeCuboidShape(4, 4, 0, 8, 8, 8),
                        makeCuboidShape(4, 8, 0, 8, 12, 4),
                        makeCuboidShape(8, 0, 0, 12, 4, 8),
                        makeCuboidShape(8, 4, 0, 12, 8, 4),
                        makeCuboidShape(12, 0, 0, 16, 4, 4)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerPrismShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (state.get(PropertyHolder.TOP))
            {
                VoxelShape shapeTop = VoxelShapes.or(
                        makeCuboidShape(0, 8, 12, 12, 12, 16),
                        makeCuboidShape(0, 12, 0, 16, 16, 16),
                        makeCuboidShape(0, 8, 0, 16, 12, 12),
                        makeCuboidShape(0, 4, 0, 16, 8, 8),
                        makeCuboidShape(0, 0, 0, 16, 4, 4),
                        makeCuboidShape(0, 0, 4, 4, 4, 16),
                        makeCuboidShape(0, 4, 8, 8, 8, 16),
                        makeCuboidShape(8, 4, 8, 12, 8, 12),
                        makeCuboidShape(4, 0, 4, 8, 4, 8),
                        makeCuboidShape(4, 0, 8, 8, 4, 12),
                        makeCuboidShape(8, 0, 4, 12, 4, 8)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = VoxelShapes.or(
                        makeCuboidShape(0, 4, 12, 12, 8, 16),
                        makeCuboidShape(0, 0, 0, 16, 4, 16),
                        makeCuboidShape(0, 4, 0, 16, 8, 12),
                        makeCuboidShape(0, 8, 0, 16, 12, 8),
                        makeCuboidShape(0, 12, 0, 16, 16, 4),
                        makeCuboidShape(0, 12, 4, 4, 16, 16),
                        makeCuboidShape(0, 8, 8, 8, 12, 16),
                        makeCuboidShape(8, 8, 8, 12, 12, 12),
                        makeCuboidShape(4, 12, 4, 8, 16, 8),
                        makeCuboidShape(4, 12, 8, 8, 16, 12),
                        makeCuboidShape(8, 12, 4, 12, 16, 8)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
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