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
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP);

        return side == dir || side == dir.getCounterClockWise() || (dir == Direction.DOWN && !top) || (dir == Direction.UP && top);
    };

    public FramedDoubleThreewayCornerBlock(BlockType blockType)
    {
        super(blockType);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction facing = context.getHorizontalDirection();
        BlockState state = defaultBlockState().setValue(PropertyHolder.FACING_HOR, facing);
        return withTop(state, context.getClickedFace(), context.getClickLocation());
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        boolean top = state.getValue(PropertyHolder.TOP);
        if (world.getBlockEntity(pos) instanceof FramedDoubleTileEntity dte)
        {
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