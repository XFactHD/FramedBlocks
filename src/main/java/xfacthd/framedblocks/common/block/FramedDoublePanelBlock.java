package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedDoublePanelTileEntity;

import java.util.function.BiPredicate;

public class FramedDoublePanelBlock extends AbstractFramedDoubleBlock
{
    public static final BiPredicate<BlockState, Direction> CTM_PREDICATE_PANEL = (state, dir) ->
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
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedDoublePanelTileEntity(); }
}