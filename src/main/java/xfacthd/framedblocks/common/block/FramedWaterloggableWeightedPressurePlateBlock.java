package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedWaterloggableWeightedPressurePlateBlock extends FramedWeightedPressurePlateBlock implements SimpleWaterloggedBlock
{
    FramedWaterloggableWeightedPressurePlateBlock(BlockType type, int maxWeight, Properties props)
    {
        super(type, maxWeight, props);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        BlockState newState = super.updateShape(state, facing, facingState, level, pos, facingPos);
        if (!newState.isAir() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return newState;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        if (state.getValue(BlockStateProperties.WATERLOGGED))
        {
            return Fluids.WATER.getSource(false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }
}
