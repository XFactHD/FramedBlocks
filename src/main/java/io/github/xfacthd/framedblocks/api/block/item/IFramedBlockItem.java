package io.github.xfacthd.framedblocks.api.block.item;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.camo.CamoPrinter;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.sound.SoundEventType;
import io.github.xfacthd.framedblocks.api.util.sound.SoundUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/// Required super-interface of all [BlockItem]s of framed blocks.
public interface IFramedBlockItem {
    /// The header line displayed above the property list of the selected placement state when manual state cycling is active
    Component HEADER_SELECTED_STATE = Utils.translate("label", "state_cycling.selected_state").withStyle(style -> style.withColor(0xFFE0E0E0));

    /// Returns the [StateCycleSpec] to use for cycling through the states of the block(s) placed by this item.
    /// The return value of this method must be constant.
    StateCycleSpec getStateCycleSpec();

    /// Compute the placement state of this [BlockItem] in the given context, taking manual state cycling into account.
    /// Must be called from [BlockItem#getPlacementState(BlockPlaceContext)].
    ///
    /// @param context      The context used for placing the block
    /// @param superHandler A reference to the block item's [BlockItem#getPlacementState(BlockPlaceContext)] method
    /// @return the placement state or null if placement is not possible
    @ApiStatus.NonExtendable
    default @Nullable BlockState getPlacementState(BlockPlaceContext context, Function<BlockPlaceContext, @Nullable BlockState> superHandler) {
        if (context.getPlayer() != null && isStateCyclingActive(context.getPlayer())) {
            BlockState state = getStateCycleSpec().getPlacementState(context);
            if (state != null && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
                FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
                state = state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
            }
            return state;
        }
        return superHandler.apply(context);
    }

    /// {@return whether the player has enabled manual state cycling for this block item}
    ///
    /// @param player The player to check against
    @ApiStatus.NonExtendable
    default boolean isStateCyclingActive(Player player) {
        return getStateCycleSpec().canCycle() && InternalAPI.INSTANCE.isStateCyclingActive(player, (BlockItem) this);
    }

    /// {@return whether the player has enabled manual state cycling for the given stack's item}
    ///
    /// @param stack  The stack whose item to check
    /// @param player The player to check against
    static boolean isStateCyclingActive(ItemStack stack, Player player) {
        return stack.getItem() instanceof IFramedBlockItem item && item.isStateCyclingActive(player);
    }

    /// Handle placement of this item's block. Must be called from [BlockItem#place(BlockPlaceContext)].
    ///
    /// @param context      The context to use for placing the block
    /// @param superHandler A reference to the block item's [BlockItem#place(BlockPlaceContext)] method
    /// @return the result of the placement attempt
    @ApiStatus.NonExtendable
    default InteractionResult handlePlace(BlockPlaceContext context, Function<BlockPlaceContext, InteractionResult> superHandler) {
        InteractionResult result = superHandler.apply(context);
        if (result == InteractionResult.SUCCESS) {
            playPlaceSound(context);
        }
        return result;
    }

    ///
    @ApiStatus.OverrideOnly
    default void playPlaceSound(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockEntity(pos) instanceof IFramedBlockEntity be)) {
            return;
        }

        SoundType soundOne = resolveSound(be.getCamo().getContent());
        SoundUtils.playPlaceSound(context, soundOne, false);

        if (be instanceof FramedDoubleBlockEntity dbe) {
            SoundType soundTwo = resolveSound(dbe.getCamoTwo().getContent());
            if (!SoundUtils.isSameSound(soundOne, soundTwo, SoundEventType.PLACE)) {
                SoundUtils.playPlaceSound(context, soundTwo, false);
            }
        }
    }

    /// {@return whether this item should use the block's default placement sound instead of the camo sound when no camo is applied}
    default boolean useCustomEmptyPlaceSound() {
        return false;
    }

    @SuppressWarnings("deprecation")
    private SoundType resolveSound(CamoContent<?> camo) {
        if (useCustomEmptyPlaceSound() && camo.isEmpty()) {
            return ((BlockItem) this).getBlock().defaultBlockState().getSoundType();
        }
        return camo.getSoundType();
    }

    /// Override the block's default placement sound to latte replace it with the placement sound of one or more camos.
    ///
    /// @param state       The state that was placed
    /// @param level       The level the state was placed in
    /// @param pos         The position the state was placed at
    /// @param player      The player who placed the block
    /// @param superGetter A reference to the block item's [BlockItem#getPlaceSound(BlockState, Level, BlockPos, Player)] method
    /// @return the sound event to be played via vanilla code
    @ApiStatus.NonExtendable
    default SoundEvent getCamoPlaceSound(BlockState state, Level level, BlockPos pos, Player player, PlaceSoundGetter superGetter) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity) {
            // Dummy out the automatically played place sound
            return SoundEvents.EMPTY;
        }
        return superGetter.get(state, level, pos, player);
    }

    /// Append the camos stored on the given stack of this item and the selected placement state (if manual
    /// state cycling is enabled for this item) to the tooltip.
    ///
    /// @param stack    The stack for which the tooltip is being displayed
    /// @param ctx      The context to use for computing the tooltip lines
    /// @param appender The appender to pass the tooltip lines to
    @ApiStatus.NonExtendable
    default void appendDefaultHoverText(ItemStack stack, Item.TooltipContext ctx, Consumer<Component> appender) {
        CamoPrinter.printCamoList(appender, stack.get(FramedConstants.Objects.DC_TYPE_CAMO_LIST), false);

        Player player = ctx.player();
        if (player != null && isStateCyclingActive(player)) {
            appender.accept(HEADER_SELECTED_STATE);
            getStateCycleSpec().appendHoverText(player, (BlockItem) this, appender);
        }
    }

    /// Functional interface representing a reference to [BlockItem#getPlaceSound(BlockState, Level, BlockPos, Player)].
    @FunctionalInterface
    interface PlaceSoundGetter {
        /// {@return the placement sound of the given block}
        ///
        /// @param state  The state that was placed
        /// @param level  The level the state was placed in
        /// @param pos    The position the state was placed at
        /// @param player The player who placed the block
        SoundEvent get(BlockState state, Level level, BlockPos pos, Player player);
    }
}
