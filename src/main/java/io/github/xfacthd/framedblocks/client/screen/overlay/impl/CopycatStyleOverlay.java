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

public final class CopycatStyleOverlay extends BlockInteractOverlay {
    public static final Component LINE_USE_STANDARD = Utils.translate("tooltip", "copycat_style.use_standard");
    public static final Component LINE_USE_COPYCAT = Utils.translate("tooltip", "copycat_style.use_copycat");
    public static final Component LINE_SET_STANDARD = Utils.translate("tooltip", "copycat_style.set_standard");
    public static final Component LINE_SET_COPYCAT = Utils.translate("tooltip", "copycat_style.set_copycat");
    private static final List<Component> LINES_FALSE = List.of(LINE_USE_STANDARD, LINE_SET_COPYCAT);
    private static final List<Component> LINES_TRUE = List.of(LINE_USE_COPYCAT, LINE_SET_STANDARD);

    private static final Identifier SYMBOL_TEXTURE = Utils.id("textures/overlay/copycat_style_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 22, 22, 44, 22);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 22, 0, 22, 22, 44, 22);

    public CopycatStyleOverlay() {
        super(LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getCopycatStyleMode);
    }

    @Override
    public boolean isValidTool(Player player, ItemStack stack) {
        return stack.getItem() == FBContent.ITEM_FRAMED_HAMMER.value();
    }

    @Override
    public boolean isValidTarget(Target target) {
        return target.state().hasProperty(FramedProperties.COPYCAT_STYLE);
    }

    @Override
    public boolean getState(Target target) {
        return target.state().getValue(FramedProperties.COPYCAT_STYLE);
    }
}
