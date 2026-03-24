package io.github.xfacthd.framedblocks.common.block.interactive.pressureplate;

import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class FramedWaterloggablePressurePlateBlock extends FramedPressurePlateBlock implements SimpleWaterloggedBlock
{
    FramedWaterloggablePressurePlateBlock(BlockType type, BlockSetType blockSet, Properties props)
    {
        super(type, blockSet, props);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random
    )
    {
        BlockState newState = super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
        if (!newState.isAir() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return newState;
    }

    @Override
    protected FluidState getFluidState(BlockState state)
    {
        if (state.getValue(BlockStateProperties.WATERLOGGED))
        {
            return Fluids.WATER.getSource(false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player)
    {
        return new ItemStack(getCounterpart());
    }
}
