package io.github.xfacthd.framedblocks.client.screen.overlay.impl;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.screen.overlay.BlockInteractOverlay;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class PrismOffsetOverlay extends BlockInteractOverlay {
    public static final Component PRISM_OFFSET_FALSE = Utils.translate("tooltip", "prism_offset.false");
    public static final Component PRISM_OFFSET_TRUE = Utils.translate("tooltip", "prism_offset.true");
    public static final Component MSG_SWITCH_OFFSET = Utils.translate("msg", "prism_offset.switch");
    private static final List<Component> LINES_FALSE = List.of(PRISM_OFFSET_FALSE, MSG_SWITCH_OFFSET);
    private static final List<Component> LINES_TRUE = List.of(PRISM_OFFSET_TRUE, MSG_SWITCH_OFFSET);

    private static final Identifier SYMBOL_TEXTURE = Utils.id("textures/overlay/prism_offset_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 19, 19, 38, 19);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 19, 0, 19, 19, 38, 19);

    public PrismOffsetOverlay() {
        super(LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getPrismOffsetMode);
    }

    @Override
    public boolean isValidTool(Player player, ItemStack stack) {
        return stack.getItem() == FBContent.ITEM_FRAMED_HAMMER.value();
    }

    @Override
    public boolean isValidTarget(Target target) {
        return target.state().hasProperty(FramedProperties.OFFSET);
    }

    @Override
    public boolean getState(Target target) {
        return target.state().getValue(FramedProperties.OFFSET);
    }
}
