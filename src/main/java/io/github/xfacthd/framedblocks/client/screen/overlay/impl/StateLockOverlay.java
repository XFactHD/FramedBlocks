package io.github.xfacthd.framedblocks.client.screen.overlay.impl;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.ShapeLockableBlock;
import io.github.xfacthd.framedblocks.api.screen.overlay.BlockInteractOverlay;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class StateLockOverlay extends BlockInteractOverlay
{
    public static final String LOCK_MESSAGE = Utils.translationKey("tooltip", "lock_state");
    private static final List<Component> LINES_FALSE = List.of(
            Component.translatable(LOCK_MESSAGE, ShapeLockableBlock.STATE_UNLOCKED)
    );
    private static final List<Component> LINES_TRUE = List.of(
            Component.translatable(LOCK_MESSAGE, ShapeLockableBlock.STATE_LOCKED)
    );

    private static final Identifier SYMBOL_TEXTURE = Utils.id("textures/overlay/state_lock_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 22, 22, 44, 22);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 22, 0, 22, 22, 44, 22);

    public StateLockOverlay()
    {
        super(LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getStateLockMode);
    }

    @Override
    public boolean isValidTool(Player player, ItemStack stack)
    {
        return stack.is(FBContent.ITEM_FRAMED_KEY.value());
    }

    @Override
    public boolean isValidTarget(Target target)
    {
        return target.state().getBlock() instanceof ShapeLockableBlock;
    }

    @Override
    public boolean getState(Target target)
    {
        return target.state().getValue(FramedProperties.STATE_LOCKED);
    }
}
