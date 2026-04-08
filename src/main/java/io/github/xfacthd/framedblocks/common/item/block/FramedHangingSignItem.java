package io.github.xfacthd.framedblocks.common.item.block;

import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public final class FramedHangingSignItem extends HangingSignItem implements IFramedBlockItem {
    public FramedHangingSignItem(Properties properties) {
        super(FBContent.BLOCK_FRAMED_HANGING_SIGN.value(), FBContent.BLOCK_FRAMED_WALL_HANGING_SIGN.value(), properties.stacksTo(16));
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        return handlePlace(context, super::place);
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player entity) {
        return getCamoPlaceSound(state, level, pos, entity, super::getPlaceSound);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag) {
        appendDefaultHoverText(stack, ctx, appender);
    }
}
