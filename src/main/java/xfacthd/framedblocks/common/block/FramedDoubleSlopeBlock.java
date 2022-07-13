package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.blockentity.FramedDoubleSlopeBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.property.SlopeType;

import javax.annotation.Nullable;

public class FramedDoubleSlopeBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return dir != null && !Utils.isY(dir);
        }
        else
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            return (dir != null && Utils.isY(dir)) || dir == facing || dir == facing.getOpposite();
        }
    };

    public FramedDoubleSlopeBlock() { super(BlockType.FRAMED_DOUBLE_SLOPE); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return withSlopeType(defaultBlockState(), context.getClickedFace(), context.getHorizontalDirection(), context.getClickLocation());
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getCamoSound(BlockState state, LevelReader level, BlockPos pos)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (type != SlopeType.HORIZONTAL)
        {
            if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity dbe)
            {
                BlockState camoState = type == SlopeType.TOP ? dbe.getCamoState() : dbe.getCamoStateTwo();
                if (!camoState.isAir())
                {
                    return camoState.getSoundType();
                }

                camoState = type == SlopeType.TOP ? dbe.getCamoStateTwo() : dbe.getCamoState();
                if (!camoState.isAir())
                {
                    return camoState.getSoundType();
                }
            }
            return getSoundType(state);
        }
        return super.getCamoSound(state, level, pos);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(PropertyHolder.SLOPE_TYPE);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) { return rotate(state, Direction.UP, rot); }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlopeBlockEntity(pos, state);
    }
}