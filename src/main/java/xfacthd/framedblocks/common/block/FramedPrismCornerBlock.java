package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.Utils;

public class FramedPrismCornerBlock extends FramedThreewayCornerBlock
{
    public FramedPrismCornerBlock(BlockType type)
    {
        super(type);
        setDefaultState(getDefaultState().with(PropertyHolder.TOP, false).with(PropertyHolder.OFFSET, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(PropertyHolder.OFFSET);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) { return null; }

        if (getBlockType() == BlockType.FRAMED_PRISM_CORNER)
        {
            state = state.with(PropertyHolder.OFFSET, context.getPos().getY() % 2 != 0);
        }
        else if (getBlockType() == BlockType.FRAMED_INNER_PRISM_CORNER)
        {
            state = state.with(PropertyHolder.OFFSET, context.getPos().getY() % 2 == 0);
        }

        return state;
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
}