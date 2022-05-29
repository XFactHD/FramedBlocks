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
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.blockentity.FramedDoubleThreewayCornerBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.api.util.CtmPredicate;

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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction facing = context.getHorizontalDirection();
        BlockState state = defaultBlockState().setValue(PropertyHolder.FACING_HOR, facing);
        return withTop(state, context.getClickedFace(), context.getClickLocation());
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getCamoSound(BlockState state, LevelReader level, BlockPos pos)
    {
        boolean top = state.getValue(PropertyHolder.TOP);
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity dbe)
        {
            CamoContainer camo = top ? dbe.getCamo() : dbe.getCamoTwo();
            if (!camo.isEmpty())
            {
                return camo.getState().getSoundType();
            }

            camo = top ? dbe.getCamoTwo() : dbe.getCamo();
            if (!camo.isEmpty())
            {
                return camo.getState().getSoundType();
            }
        }
        return getSoundType(state);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleThreewayCornerBlockEntity(pos, state);
    }
}