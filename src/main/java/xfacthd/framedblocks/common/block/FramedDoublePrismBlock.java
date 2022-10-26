package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.CtmPredicate;
import xfacthd.framedblocks.common.blockentity.FramedDoublePrismBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedDoublePrismBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        return side != null && side.getAxis() != axis;
    };

    public FramedDoublePrismBlock() { super(BlockType.FRAMED_DOUBLE_PRISM); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, BlockStateProperties.AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedPrismBlock.getStateForPlacement(context, defaultBlockState(), getBlockType());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoublePrismBlockEntity(pos, state);
    }
}