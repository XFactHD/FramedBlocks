package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

/**
 * Marks a block as allowing its state to be locked in order to suppress state changes from neighbor updates.
 * Useful to allow blocks like stairs to reside in impossible states, like a corner without neighbors.
 * <p>
 * Blocks implementing this interface must have the {@link FramedProperties#STATE_LOCKED} property.
 * The actual update suppression needs to be handled manually by the block implementing this interface by calling
 * {@link #updateShapeLockable(BlockState, LevelReader, ScheduledTickAccess, BlockPos, Direction, BlockPos, BlockState, RandomSource, UpdateShapeHandler)}
 * from {@link Block#updateShape(BlockState, LevelReader, ScheduledTickAccess, BlockPos, Direction, BlockPos, BlockState, RandomSource)}.
 */
public interface ShapeLockableBlock extends IFramedBlock
{
    String LOCK_MESSAGE = Utils.translationKey("msg", "lock_state");
    Component STATE_LOCKED = Utils.translate("msg", "lock_state.locked").withStyle(ChatFormatting.RED);
    Component STATE_UNLOCKED = Utils.translate("msg", "lock_state.unlocked").withStyle(ChatFormatting.GREEN);

    @ApiStatus.NonExtendable
    default boolean isLocked(BlockState state)
    {
        return state.getValue(FramedProperties.STATE_LOCKED);
    }

    @ApiStatus.NonExtendable
    default boolean lockState(Level level, BlockPos pos, Player player, ItemStack stack)
    {
        if (stack.getItem() != Utils.FRAMED_KEY.value())
        {
            return false;
        }

        if (!level.isClientSide())
        {
            BlockState state = level.getBlockState(pos);
            boolean locked = state.getValue(FramedProperties.STATE_LOCKED);
            player.displayClientMessage(Component.translatable(LOCK_MESSAGE, locked ? STATE_UNLOCKED : STATE_LOCKED), true);

            level.setBlockAndUpdate(pos, state.cycle(FramedProperties.STATE_LOCKED));
        }
        return true;
    }

    @ApiStatus.NonExtendable
    default BlockState updateShapeLockable(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random,
            UpdateShapeHandler updateShape
    )
    {
        if (!state.getValue(FramedProperties.STATE_LOCKED))
        {
            return updateShape.handle(state, level, tickAccess, pos, side, adjPos, adjState, random);
        }
        if (getBlockType().supportsWaterLogging() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }

    /** {@return the blockstate properties to copy when this block is copied by a Framed Blueprint} */
    Set<Property<?>> getPropertiesToCopy();

    @FunctionalInterface
    interface UpdateShapeHandler
    {
        BlockState handle(
                BlockState state,
                LevelReader level,
                ScheduledTickAccess tickAccess,
                BlockPos pos,
                Direction side,
                BlockPos adjPos,
                BlockState adjState,
                RandomSource random
        );
    }
}
