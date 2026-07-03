package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
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

/// Marks a block as allowing its state to be locked in order to suppress state changes from neighbor updates.
/// Useful to allow blocks like stairs to reside in "impossible" states, like a corner without neighbors.
///
/// Blocks implementing this interface must have the [FramedProperties#STATE_LOCKED] property.
/// The actual update suppression needs to be handled manually by the block implementing this interface by calling
/// [#updateShapeLockable(BlockState, LevelReader, ScheduledTickAccess, BlockPos, Direction, BlockPos, BlockState, RandomSource, UpdateShapeHandler)]
/// from [Block#updateShape(BlockState, LevelReader, ScheduledTickAccess, BlockPos, Direction, BlockPos, BlockState, RandomSource)].
public interface ShapeLockableBlock extends IFramedBlock {
    String LOCK_MESSAGE = Utils.translationKey("msg", "lock_state");
    Component STATE_LOCKED = Utils.translate("msg", "lock_state.locked").withStyle(ChatFormatting.RED);
    Component STATE_UNLOCKED = Utils.translate("msg", "lock_state.unlocked").withStyle(ChatFormatting.GREEN);

    /// {@return whether the given state of this block is state-locked}
    ///
    /// @param state The state of this block
    @ApiStatus.NonExtendable
    default boolean isLocked(BlockState state) {
        return state.getValue(FramedProperties.STATE_LOCKED);
    }

    /// Cycle the locking state of this block.
    ///
    /// @param level  The level this block is in
    /// @param pos    The position of this block
    /// @param player The player interacting with this block
    /// @param stack  The stack used for interacting with this block
    /// @return whether the lock state of this block changed
    @ApiStatus.NonExtendable
    default boolean lockState(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (stack.getItem() != FramedConstants.Objects.FRAMED_KEY.value()) {
            return false;
        }

        if (!level.isClientSide()) {
            BlockState state = level.getBlockState(pos);
            boolean locked = state.getValue(FramedProperties.STATE_LOCKED);
            player.sendOverlayMessage(Component.translatable(LOCK_MESSAGE, locked ? STATE_UNLOCKED : STATE_LOCKED));

            level.setBlockAndUpdate(pos, state.cycle(FramedProperties.STATE_LOCKED));
        }
        return true;
    }

    /// Handle a shape update notification on this block. Must be called from
    /// [Block#updateShape(BlockState, LevelReader, ScheduledTickAccess, BlockPos, Direction, BlockPos, BlockState, RandomSource)].
    ///
    /// @param state       The state of the block being notified
    /// @param level       The level the blocks are in
    /// @param tickAccess  The tick access to schedule block ticks to
    /// @param pos         The position of the block being notified
    /// @param side        The affected side of the block being notified
    /// @param adjPos      The position of the adjacent block that triggered the update
    /// @param adjState    The state of the adjacent block that triggered the update
    /// @param random      The RNG to use for randomized reactions
    /// @param updateShape The default shape update handler to invoke if this block is not shape-locked
    /// @return the update state of this block
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
    ) {
        if (!state.getValue(FramedProperties.STATE_LOCKED)) {
            return updateShape.handle(state, level, tickAccess, pos, side, adjPos, adjState, random);
        }
        if (getBlockType().supportsWaterLogging() && state.getValue(BlockStateProperties.WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }

    /// {@return the blockstate properties to copy when this block is copied by a Framed Blueprint}
    Set<Property<?>> getPropertiesToCopy();

    /// Functional interface representing a reference to [Block#updateShape(BlockState, LevelReader, ScheduledTickAccess, BlockPos, Direction, BlockPos, BlockState, RandomSource)]
    @FunctionalInterface
    interface UpdateShapeHandler {
        /// {@return the state adjusted for reaction to the adjacent block on the given side}
        ///
        /// @param state      The state of the block being notified
        /// @param level      The level the blocks are in
        /// @param tickAccess The tick access to schedule block ticks to
        /// @param pos        The position of the block being notified
        /// @param side       The affected side of the block being notified
        /// @param adjPos     The position of the adjacent block that triggered the update
        /// @param adjState   The state of the adjacent block that triggered the update
        /// @param random     The RNG to use for randomized reactions
        /// @return the update state of this block
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
