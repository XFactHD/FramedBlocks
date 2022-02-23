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
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.blockentity.*;

public class FramedDoubleCornerBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (type.isHorizontal())
        {
            if (side == null) { return false; }

            return  side == dir || side == dir.getOpposite() ||
                   (side == dir.getCounterClockWise() && !type.isRight()) || (side == dir.getClockWise() && type.isRight()) ||
                   (side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop());
        }
        else
        {
            return (side != null && Utils.isY(side)) || side == dir || side == dir.getCounterClockWise();
        }
    };

    public FramedDoubleCornerBlock() { super(BlockType.FRAMED_DOUBLE_CORNER); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.CORNER_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction side = context.getClickedFace();
        Vec3 hitPoint = Utils.fraction(context.getClickLocation());
        if (!Utils.isY(side))
        {
            if (hitPoint.y() < (3D / 16D))
            {
                side = Direction.UP;
            }
            else if (hitPoint.y() > (13D / 16D))
            {
                side = Direction.DOWN;
            }
        }

        return withCornerType(state, context, side, hitPoint, context.getHorizontalDirection());
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getCamoSound(BlockState state, LevelReader level, BlockPos pos)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity dbe)
        {
            BlockState camoState = (type.isHorizontal() || type.isTop()) ? dbe.getCamoState() : dbe.getCamoStateTwo();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }

            camoState = (type.isHorizontal() || type.isTop()) ? dbe.getCamoStateTwo() : dbe.getCamoState();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }
        }
        return getSoundType(state);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleCornerBlockEntity(pos, state);
    }
}