package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedChestBlockEntity;
import xfacthd.framedblocks.common.data.*;

import javax.annotation.Nullable;

public class FramedChestBlock extends FramedStorageBlock
{
    public FramedChestBlock() { super(BlockType.FRAMED_CHEST); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.CHEST_STATE, BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState().setValue(PropertyHolder.FACING_HOR, context.getHorizontalDirection().getOpposite());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedChestBlockEntity(pos, state); }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if (level.isClientSide() || state.getValue(PropertyHolder.CHEST_STATE) != ChestState.CLOSING) { return null; }

        return Utils.createBlockEntityTicker(type, FBContent.blockEntityTypeFramedChest.get(), FramedChestBlockEntity::tick);
    }
}