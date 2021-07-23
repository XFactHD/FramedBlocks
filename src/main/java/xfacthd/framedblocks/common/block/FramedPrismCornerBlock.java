package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.Utils;

public class FramedPrismCornerBlock extends FramedThreewayCornerBlock
{
    public FramedPrismCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.TOP, false).setValue(PropertyHolder.OFFSET, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.OFFSET);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) { return null; }

        if (getBlockType() == BlockType.FRAMED_PRISM_CORNER)
        {
            state = state.setValue(PropertyHolder.OFFSET, context.getClickedPos().getY() % 2 != 0);
        }
        else if (getBlockType() == BlockType.FRAMED_INNER_PRISM_CORNER)
        {
            state = state.setValue(PropertyHolder.OFFSET, context.getClickedPos().getY() % 2 == 0);
        }

        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void attack(BlockState state, Level world, BlockPos pos, Player player)
    {
        if (world.isClientSide()) { return; }

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == FBContent.itemFramedHammer.get())
        {
            world.setBlockAndUpdate(pos, state.setValue(PropertyHolder.OFFSET, !state.getValue(PropertyHolder.OFFSET)));
        }
    }

    public static ImmutableMap<BlockState, VoxelShape> generatePrismShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

            if (state.getValue(PropertyHolder.TOP))
            {
                VoxelShape shapeTop = Shapes.or(
                        box(0, 12, 0, 4, 16, 16),
                        box(0, 8, 0, 4, 12, 12),
                        box(0, 4, 0, 4, 8, 8),
                        box(0, 0, 0, 4, 4, 4),
                        box(4, 12, 0, 8, 16, 12),
                        box(4, 8, 0, 8, 12, 8),
                        box(4, 4, 0, 8, 8, 4),
                        box(8, 12, 0, 12, 16, 8),
                        box(8, 8, 0, 12, 12, 4),
                        box(12, 12, 0, 16, 16, 4)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = Shapes.or(
                        box(0, 0, 0, 4, 4, 16),
                        box(0, 4, 0, 4, 8, 12),
                        box(0, 8, 0, 4, 12, 8),
                        box(0, 12, 0, 4, 16, 4),
                        box(4, 0, 0, 8, 4, 12),
                        box(4, 4, 0, 8, 8, 8),
                        box(4, 8, 0, 8, 12, 4),
                        box(8, 0, 0, 12, 4, 8),
                        box(8, 4, 0, 12, 8, 4),
                        box(12, 0, 0, 16, 4, 4)
                ).optimize();

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
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

            if (state.getValue(PropertyHolder.TOP))
            {
                VoxelShape shapeTop = Shapes.or(
                        box(0, 8, 12, 12, 12, 16),
                        box(0, 12, 0, 16, 16, 16),
                        box(0, 8, 0, 16, 12, 12),
                        box(0, 4, 0, 16, 8, 8),
                        box(0, 0, 0, 16, 4, 4),
                        box(0, 0, 4, 4, 4, 16),
                        box(0, 4, 8, 8, 8, 16),
                        box(8, 4, 8, 12, 8, 12),
                        box(4, 0, 4, 8, 4, 8),
                        box(4, 0, 8, 8, 4, 12),
                        box(8, 0, 4, 12, 4, 8)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = Shapes.or(
                        box(0, 4, 12, 12, 8, 16),
                        box(0, 0, 0, 16, 4, 16),
                        box(0, 4, 0, 16, 8, 12),
                        box(0, 8, 0, 16, 12, 8),
                        box(0, 12, 0, 16, 16, 4),
                        box(0, 12, 4, 4, 16, 16),
                        box(0, 8, 8, 8, 12, 16),
                        box(8, 8, 8, 12, 12, 12),
                        box(4, 12, 4, 8, 16, 8),
                        box(4, 12, 8, 8, 16, 12),
                        box(8, 12, 4, 12, 16, 8)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }
}