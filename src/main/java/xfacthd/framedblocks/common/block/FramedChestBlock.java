package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedChestTileEntity;
import xfacthd.framedblocks.common.util.Utils;

import javax.annotation.Nullable;

public class FramedChestBlock extends FramedStorageBlock
{
    public FramedChestBlock() { super(BlockType.FRAMED_CHEST); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.CHEST_STATE, BlockStateProperties.WATERLOGGED, PropertyHolder.LATCH_TYPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState().with(PropertyHolder.FACING_HOR, context.getPlacementHorizontalFacing().getOpposite());
        return withWater(state, context.getWorld(), context.getPos());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem().isIn(Utils.WRENCH) && player.isSneaking())
        {
            if (!world.isRemote())
            {
                state = state.with(PropertyHolder.LATCH_TYPE, state.get(PropertyHolder.LATCH_TYPE).next());
                world.setBlockState(pos, state);
            }
            return ActionResultType.func_233537_a_(world.isRemote());
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedChestTileEntity(); }
}