package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedChestTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class FramedChestBlock extends FramedBlock
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        InteractionResult result = super.use(state, world, pos, player, hand, hit);
        if (result != InteractionResult.PASS) { return result; }

        if (!world.isClientSide())
        {
            if (world.getBlockEntity(pos) instanceof FramedChestTileEntity te)
            {
                if (state.getValue(PropertyHolder.CHEST_STATE) != ChestState.OPENING)
                {
                    world.setBlockAndUpdate(pos, state.setValue(PropertyHolder.CHEST_STATE, ChestState.OPENING));
                    world.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
                }

                te.open();
                NetworkHooks.openGui((ServerPlayer) player, te, pos);
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> drops = super.getDrops(state, builder);

        BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (te instanceof FramedChestTileEntity)
        {
            ((FramedChestTileEntity) te).addDrops(drops);
        }

        return drops;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedChestTileEntity(pos, state); }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type)
    {
        if (world.isClientSide()) { return null; }

        //FIXME: fucking generics
        return (level, pos, blockState, te) -> FramedChestTileEntity.tick(level, pos, blockState, (FramedChestTileEntity) te);
    }
}