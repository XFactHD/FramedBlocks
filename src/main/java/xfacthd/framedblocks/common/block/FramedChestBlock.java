package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedChestTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class FramedChestBlock extends FramedBlock
{
    public FramedChestBlock() { super(BlockType.FRAMED_CHEST); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.CHEST_STATE, BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState().setValue(PropertyHolder.FACING_HOR, context.getHorizontalDirection().getOpposite());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ActionResultType result = super.use(state, world, pos, player, hand, hit);
        if (result != ActionResultType.PASS) { return result; }

        if (!world.isClientSide())
        {
            if (world.getBlockEntity(pos) instanceof FramedChestTileEntity te)
            {
                if (state.getValue(PropertyHolder.CHEST_STATE) != ChestState.OPENING)
                {
                    world.setBlockAndUpdate(pos, state.setValue(PropertyHolder.CHEST_STATE, ChestState.OPENING));
                    world.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
                }

                te.open();
                NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
            }
        }
        return ActionResultType.sidedSuccess(world.isClientSide());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> drops = super.getDrops(state, builder);

        TileEntity te = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
        if (te instanceof FramedChestTileEntity)
        {
            ((FramedChestTileEntity) te).addDrops(drops);
        }

        return drops;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedChestTileEntity(); }
}