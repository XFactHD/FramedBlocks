package xfacthd.framedblocks.common.block.cube;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;

public class FramedStorageBlock extends FramedBlock
{
    public FramedStorageBlock(BlockType type)
    {
        super(type);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
    {
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hit);
        if (result != InteractionResult.PASS)
        {
            return result;
        }

        if (!level.isClientSide())
        {
            if (level.getBlockEntity(pos) instanceof FramedStorageBlockEntity be)
            {
                be.open((ServerPlayer) player);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (newState.getBlock() != state.getBlock() && level.getBlockEntity(pos) instanceof FramedStorageBlockEntity be)
        {
            be.getDrops().forEach(stack -> popResource(level, pos, stack));
            be.clearContent();
            level.updateNeighbourForOutputSignal(pos, this);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);

        var customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null && level.getBlockEntity(pos) instanceof FramedStorageBlockEntity be)
        {
            be.setCustomName(customName);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof FramedStorageBlockEntity be)
        {
            return be.getAnalogOutputSignal();
        }
        return 0;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedStorageBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }
}
