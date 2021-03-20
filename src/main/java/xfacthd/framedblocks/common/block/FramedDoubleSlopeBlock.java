package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedDoubleSlopeTileEntity;

import java.util.function.BiPredicate;

public class FramedDoubleSlopeBlock extends AbstractFramedDoubleBlock
{
    public static final BiPredicate<BlockState, Direction> CTM_PREDICATE_SLOPE = (state, dir) ->
    {
        return false; //TODO: implement
    };

    public FramedDoubleSlopeBlock() { super("framed_double_slope", BlockType.FRAMED_DOUBLE_SLOPE); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        //TODO: implement
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedDoubleSlopeTileEntity(); }
}