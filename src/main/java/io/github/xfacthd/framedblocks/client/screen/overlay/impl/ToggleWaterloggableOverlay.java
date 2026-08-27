package io.github.xfacthd.framedblocks.client.screen.overlay.impl;

import io.github.xfacthd.framedblocks.api.screen.overlay.BlockInteractOverlay;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.interactive.pressureplate.FramedPressurePlateBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.pressureplate.FramedWeightedPressurePlateBlock;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public final class ToggleWaterloggableOverlay extends BlockInteractOverlay {
    public static final Component MSG_IS_WATERLOGGABLE = Utils.translate("tooltip", "is_waterloggable.true");
    public static final Component MSG_IS_NOT_WATERLOGGABLE = Utils.translate("tooltip", "is_waterloggable.false");
    public static final Component MSG_MAKE_WATERLOGGABLE = Utils.translate("tooltip", "make_waterloggable.true");
    public static final Component MSG_MAKE_NOT_WATERLOGGABLE = Utils.translate("tooltip", "make_waterloggable.false");
    private static final List<Component> LINES_FALSE = List.of(MSG_IS_NOT_WATERLOGGABLE, MSG_MAKE_WATERLOGGABLE);
    private static final List<Component> LINES_TRUE = List.of(MSG_IS_WATERLOGGABLE, MSG_MAKE_NOT_WATERLOGGABLE);

    private static final Identifier SYMBOL_TEXTURE = Utils.id("textures/overlay/waterloggable_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 20, 20, 40, 20);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 20, 0, 20, 20, 40, 20);

    public ToggleWaterloggableOverlay() {
        super(LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getToggleWaterlogMode);
    }

    @Override
    public boolean isValidTool(Player player, ItemStack stack) {
        return stack.is(FBContent.ITEM_FRAMED_HAMMER.value());
    }

    @Override
    public boolean isValidTarget(Target target) {
        Block block = target.state().getBlock();
        return block instanceof FramedPressurePlateBlock || block instanceof FramedWeightedPressurePlateBlock;
    }

    @Override
    public boolean getState(Target target) {
        return target.state().hasProperty(BlockStateProperties.WATERLOGGED);
    }
}
