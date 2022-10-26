package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.CtmPredicate;
import xfacthd.framedblocks.common.blockentity.FramedDoubleSlopedPrismBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoubleSlopedPrismBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
            side != state.getValue(PropertyHolder.ORIENTATION);

    public FramedDoubleSlopedPrismBlock() { super(BlockType.FRAMED_DOUBLE_SLOPED_PRISM); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, PropertyHolder.ORIENTATION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedSlopedPrismBlock.getStateForPlacement(context, defaultBlockState(), getBlockType());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlopedPrismBlockEntity(pos, state);
    }
}
