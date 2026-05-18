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

public interface IFramedBlockItem {
    Component HEADER_SELECTED_STATE = Utils.translate("label", "state_cycling.selected_state").withStyle(style -> style.withColor(0xFFE0E0E0));

    /// Returns the [StateCycleSpec] to use for cycling through the states of the block(s) placed by this item.
    /// The return value of this method must be constant.
    StateCycleSpec getStateCycleSpec();

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

    @ApiStatus.NonExtendable
    default boolean isStateCyclingActive(Player player) {
        return getStateCycleSpec().canCycle() && InternalAPI.INSTANCE.isStateCyclingActive(player, (BlockItem) this);
    }

    static boolean isStateCyclingActive(ItemStack stack, Player player) {
        return stack.getItem() instanceof IFramedBlockItem item && item.isStateCyclingActive(player);
    }

    @ApiStatus.NonExtendable
    default InteractionResult handlePlace(BlockPlaceContext context, Function<BlockPlaceContext, InteractionResult> superHandler) {
        InteractionResult result = superHandler.apply(context);
        if (result == InteractionResult.SUCCESS) {
            playPlaceSound(context);
        }
        return result;
    }

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

    @ApiStatus.NonExtendable
    default SoundEvent getCamoPlaceSound(BlockState state, Level level, BlockPos pos, Player player, PlaceSoundGetter superGetter) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity) {
            // Dummy out the automatically played place sound
            return SoundEvents.EMPTY;
        }
        return superGetter.get(state, level, pos, player);
    }

    @ApiStatus.NonExtendable
    default void appendDefaultHoverText(ItemStack stack, Item.TooltipContext ctx, Consumer<Component> appender) {
        CamoPrinter.printCamoList(appender, stack.get(FramedConstants.Objects.DC_TYPE_CAMO_LIST), false);

        Player player = ctx.player();
        if (player != null && isStateCyclingActive(player)) {
            appender.accept(HEADER_SELECTED_STATE);
            getStateCycleSpec().appendHoverText(player, (BlockItem) this, appender);
        }
    }

    interface PlaceSoundGetter {
        SoundEvent get(BlockState state, Level level, BlockPos pos, Player player);
    }
}
