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
import xfacthd.framedblocks.common.tileentity.FramedDoubleSlopeTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;

import javax.annotation.Nullable;

public class FramedDoubleSlopeBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        if (state.get(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return dir != null && dir.getAxis() != Direction.Axis.Y;
        }
        else
        {
            Direction facing = state.get(PropertyHolder.FACING_HOR);
            return (dir != null && dir.getAxis() == Direction.Axis.Y) || dir == facing || dir == facing.getOpposite();
        }
    };

    public FramedDoubleSlopeBlock() { super("framed_double_slope", BlockType.FRAMED_DOUBLE_SLOPE); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.SLOPE_TYPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return withSlopeType(getDefaultState(), context.getFace(), context.getPlacementHorizontalFacing(), context.getHitVec());
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        SlopeType type = state.get(PropertyHolder.SLOPE_TYPE);
        if (type != SlopeType.HORIZONTAL)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof FramedDoubleTileEntity)
            {
                FramedDoubleTileEntity dte = (FramedDoubleTileEntity) te;
                BlockState camoState = type == SlopeType.TOP ? dte.getCamoState() : dte.getCamoStateTwo();
                if (!camoState.isAir())
                {
                    return camoState.getSoundType();
                }

                camoState = type == SlopeType.TOP ? dte.getCamoStateTwo() : dte.getCamoState();
                if (!camoState.isAir())
                {
                    return camoState.getSoundType();
                }
            }
            return getSoundType(state);
        }
        return super.getSound(state, world, pos);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedDoubleSlopeTileEntity(); }
}