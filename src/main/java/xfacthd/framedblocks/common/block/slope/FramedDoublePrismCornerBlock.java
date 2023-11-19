package xfacthd.framedblocks.common.block.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedDoublePrismCornerBlock extends FramedDoubleThreewayCornerBlock
{
    public FramedDoublePrismCornerBlock()
    {
        super(BlockType.FRAMED_DOUBLE_PRISM_CORNER);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.OFFSET, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.OFFSET);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) { return null; }

        state = state.setValue(FramedProperties.OFFSET, context.getClickedPos().getY() % 2 == 0);
        return state;
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().getItem() == FBContent.ITEM_FRAMED_HAMMER.value())
        {
            level.setBlockAndUpdate(pos, state.setValue(FramedProperties.OFFSET, !state.getValue(FramedProperties.OFFSET)));
            return true;
        }
        return super.handleBlockLeftClick(state, level, pos, player);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean offset = state.getValue(FramedProperties.OFFSET);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.value().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top)
                        .setValue(FramedProperties.OFFSET, offset)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.BLOCK_FRAMED_PRISM_CORNER.value().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
                        .setValue(FramedProperties.OFFSET, !offset)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }



    public static BlockState itemModelSourcePrism()
    {
        return FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER.value()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}