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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        //Don't call super, this block doesn't need the SOLID property
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.CHEST_STATE, PropertyHolder.LATCH_TYPE, BlockStateProperties.WATERLOGGED);
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
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem().is(Utils.WRENCH) && player.isShiftKeyDown())
        {
            if (!world.isClientSide())
            {
                state = state.setValue(PropertyHolder.LATCH_TYPE, state.getValue(PropertyHolder.LATCH_TYPE).next());
                world.setBlockAndUpdate(pos, state);
            }
            return ActionResultType.sidedSuccess(world.isClientSide());
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedChestTileEntity(); }
}