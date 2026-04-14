package io.github.xfacthd.framedblocks.client.screen.overlay.impl;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.screen.overlay.BlockInteractOverlay;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ReinforcementOverlay extends BlockInteractOverlay {
    public static final String REINFORCE_MESSAGE = Utils.translationKey("tooltip", "reinforce_state");
    public static final Component STATE_NOT_REINFORCED = Utils.translate("tooltip", "reinforce_state.false")
            .withStyle(ChatFormatting.RED);
    public static final Component STATE_REINFORCED = Utils.translate("tooltip", "reinforce_state.true")
            .withStyle(ChatFormatting.GREEN);
    private static final List<Component> LIST_FALSE = List.of(
            Component.translatable(REINFORCE_MESSAGE, STATE_NOT_REINFORCED)
    );
    private static final List<Component> LIST_TRUE = List.of(
            Component.translatable(REINFORCE_MESSAGE, STATE_REINFORCED)
    );

    private static final Identifier SYMBOL_TEXTURE = Utils.id("textures/overlay/reinforcement_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 22, 22, 44, 22);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 22, 0, 22, 22, 44, 22);

    public ReinforcementOverlay() {
        super(LIST_FALSE, LIST_TRUE, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getReinforcementMode);
    }

    @Override
    public boolean isValidTool(Player player, ItemStack stack) {
        return stack.is(FramedConstants.Objects.FRAMED_REINFORCEMENT.value());
    }

    @Override
    public boolean isValidTarget(Target target) {
        return target.state().getBlock() instanceof IFramedBlock;
    }

    @Override
    public boolean getState(Target target) {
        if (target.level().getBlockEntity(target.pos()) instanceof IFramedBlockEntity be) {
            return be.isReinforced();
        }
        return false;
    }
}
