package io.github.xfacthd.framedblocks.client.screen.overlay.impl;

import io.github.xfacthd.framedblocks.api.screen.overlay.BlockInteractOverlay;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.door.FramedTrapDoorBlock;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class TrapdoorTextureRotationOverlay extends BlockInteractOverlay {
    public static final Component ROTATING_FALSE = Utils.translate("tooltip", "trapdoor_texture_rotation.false");
    public static final Component ROTATING_TRUE = Utils.translate("tooltip", "trapdoor_texture_rotation.true");
    public static final Component ROTATING_TOGGLE = Utils.translate("tooltip", "trapdoor_texture_rotation.toggle");
    private static final List<Component> LINES_FALSE = List.of(ROTATING_FALSE, ROTATING_TOGGLE);
    private static final List<Component> LINES_TRUE = List.of(ROTATING_TRUE, ROTATING_TOGGLE);

    private static final Identifier SYMBOL_TEXTURE = Utils.id("textures/overlay/camo_rotation_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 22, 22, 44, 22);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 22, 0, 22, 22, 44, 22);

    public TrapdoorTextureRotationOverlay() {
        super(LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getTrapdoorTextureRotationMode);
    }

    @Override
    public boolean isValidTool(Player player, ItemStack stack) {
        return stack.getItem() == FBContent.ITEM_FRAMED_HAMMER.value();
    }

    @Override
    public boolean isValidTarget(Target target) {
        return target.state().getBlock() instanceof FramedTrapDoorBlock;
    }

    @Override
    public boolean getState(Target target) {
        return target.state().getValue(PropertyHolder.ROTATE_TEXTURE);
    }
}
