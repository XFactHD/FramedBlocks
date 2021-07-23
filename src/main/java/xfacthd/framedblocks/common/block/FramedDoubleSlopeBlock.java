package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedDoubleSlopeTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;

import javax.annotation.Nullable;

public class FramedDoubleSlopeBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return dir != null && dir.getAxis() != Direction.Axis.Y;
        }
        else
        {
            Direction facing = state.getValue(PropertyHolder.FACING_HOR);
            return (dir != null && dir.getAxis() == Direction.Axis.Y) || dir == facing || dir == facing.getOpposite();
        }
    };

    public FramedDoubleSlopeBlock() { super(BlockType.FRAMED_DOUBLE_SLOPE); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.SLOPE_TYPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return withSlopeType(defaultBlockState(), context.getClickedFace(), context.getHorizontalDirection(), context.getClickLocation());
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getCamoSound(BlockState state, LevelReader world, BlockPos pos)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (type != SlopeType.HORIZONTAL)
        {
            if (world.getBlockEntity(pos) instanceof FramedDoubleTileEntity dte)
            {
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
        return super.getCamoSound(state, world, pos);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlopeTileEntity(pos, state);
    }
}