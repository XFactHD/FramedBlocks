package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedDoublePanelTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;

public class FramedDoublePanelBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE_PANEL = (state, dir) ->
    {
        Direction facing = state.get(PropertyHolder.FACING_NE);
        return dir == facing || dir == facing.getOpposite();
    };

    public FramedDoublePanelBlock() { super("framed_double_panel", BlockType.FRAMED_DOUBLE_PANEL); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_NE);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return new ItemStack(FBContent.blockFramedPanel);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedDoublePanelTileEntity(); }
}