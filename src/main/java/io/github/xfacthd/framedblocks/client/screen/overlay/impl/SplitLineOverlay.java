package io.github.xfacthd.framedblocks.client.screen.overlay.impl;

import io.github.xfacthd.framedblocks.api.screen.overlay.BlockInteractOverlay;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class SplitLineOverlay extends BlockInteractOverlay {
    public static final Component SPLIT_LINE_FALSE = Utils.translate("tooltip", "split_line.false");
    public static final Component SPLIT_LINE_TRUE = Utils.translate("tooltip", "split_line.true");
    public static final Component MSG_SWITCH_SPLIT_LINE = Utils.translate("msg", "split_line.switch");
    private static final List<Component> LINES_FALSE = List.of(SPLIT_LINE_FALSE, MSG_SWITCH_SPLIT_LINE);
    private static final List<Component> LINES_TRUE = List.of(SPLIT_LINE_TRUE, MSG_SWITCH_SPLIT_LINE);

    private static final Identifier SYMBOL_TEXTURE = Utils.id("textures/overlay/split_line_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 20, 20, 40, 20);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 20, 0, 20, 20, 40, 20);

    public SplitLineOverlay() {
        super(LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getSplitLineMode);
    }

    @Override
    public boolean isValidTool(Player player, ItemStack stack) {
        return stack.getItem() == FBContent.ITEM_FRAMED_WRENCH.value();
    }

    @Override
    public boolean isValidTarget(Target target) {
        return target.state().getBlock() == FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.value();
    }

    @Override
    public boolean getState(Target target) {
        return target.state().getValue(PropertyHolder.ROTATE_SPLIT_LINE);
    }
}
