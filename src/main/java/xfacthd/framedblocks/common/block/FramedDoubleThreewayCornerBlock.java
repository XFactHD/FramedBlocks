package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedDoubleThreewayCornerTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;

public class FramedDoubleThreewayCornerBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        boolean top = state.get(PropertyHolder.TOP);

        return side == dir || side == dir.rotateYCCW() || (dir == Direction.DOWN && !top) || (dir == Direction.UP && top);
    };

    public FramedDoubleThreewayCornerBlock() { this("framed_double_threeway_corner", BlockType.FRAMED_DOUBLE_THREEWAY_CORNER); }

    public FramedDoubleThreewayCornerBlock(String name, BlockType blockType)
    {
        super(name, blockType);
        setDefaultState(getDefaultState().with(PropertyHolder.TOP, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction facing = context.getPlacementHorizontalFacing();
        BlockState state = getDefaultState().with(PropertyHolder.FACING_HOR, facing);
        return withTop(state, context.getFace(), context.getHitVec());
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        boolean top = state.get(PropertyHolder.TOP);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedDoubleTileEntity)
        {
            FramedDoubleTileEntity dte = (FramedDoubleTileEntity) te;
            BlockState camoState = top ? dte.getCamoState() : dte.getCamoStateTwo();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }

            camoState = top ? dte.getCamoStateTwo() : dte.getCamoState();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }
        }
        return getSoundType(state);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedDoubleThreewayCornerTileEntity(); }
}