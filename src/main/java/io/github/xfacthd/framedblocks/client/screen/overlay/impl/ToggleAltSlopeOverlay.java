package io.github.xfacthd.framedblocks.client.screen.overlay.impl;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.screen.overlay.BlockInteractOverlay;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.ConcatenatedListView;

import java.util.List;

public final class ToggleAltSlopeOverlay extends BlockInteractOverlay
{
    public static final String SLOPE_MESSAGE_VERT = Utils.translationKey("tooltip", "alt_slope");
    public static final String TOGGLE_MESSAGE_VERT = Utils.translationKey("tooltip", "alt_slope.toggle");
    public static final String SLOPE_MESSAGE_HOR = Utils.translationKey("tooltip", "alt_slope.alt");
    public static final String TOGGLE_MESSAGE_HOR = Utils.translationKey("tooltip", "alt_slope.alt.toggle");
    public static final Component SLOPE_VERT_HOR = Utils.translate("tooltip", "alt_slope.vertical.horizontal");
    public static final Component SLOPE_VERT_VERT = Utils.translate("tooltip", "alt_slope.vertical.vertical");
    public static final Component SLOPE_HOR_FRONT = Utils.translate("tooltip", "alt_slope.horizontal.front");
    public static final Component SLOPE_HOR_SIDE = Utils.translate("tooltip", "alt_slope.horizontal.side");
    private static final List<Component> LINES_FALSE = List.of(
            Component.translatable(SLOPE_MESSAGE_VERT, SLOPE_VERT_HOR),
            Component.translatable(TOGGLE_MESSAGE_VERT, SLOPE_VERT_VERT)
    );
    private static final List<Component> LINES_TRUE = List.of(
            Component.translatable(SLOPE_MESSAGE_VERT, SLOPE_VERT_VERT),
            Component.translatable(TOGGLE_MESSAGE_VERT, SLOPE_VERT_HOR)
    );
    private static final List<Component> LINES_FALSE_ALT = List.of(
            Component.translatable(SLOPE_MESSAGE_HOR, SLOPE_HOR_FRONT),
            Component.translatable(TOGGLE_MESSAGE_HOR, SLOPE_HOR_SIDE)
    );
    private static final List<Component> LINES_TRUE_ALT = List.of(
            Component.translatable(SLOPE_MESSAGE_HOR, SLOPE_HOR_SIDE),
            Component.translatable(TOGGLE_MESSAGE_HOR, SLOPE_HOR_FRONT)
    );
    private static final List<Component> LINES_FALSE_ALL = ConcatenatedListView.of(LINES_FALSE, LINES_FALSE_ALT);
    private static final List<Component> LINES_TRUE_ALL = ConcatenatedListView.of(LINES_TRUE, LINES_TRUE_ALT);

    private static final Identifier SYMBOL_TEXTURE = Utils.id("textures/overlay/alt_slope_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 20, 40, 80, 40);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 20, 0, 20, 40, 80, 40);
    private static final Texture TEXTURE_ALT_FALSE = new Texture(SYMBOL_TEXTURE, 40, 0, 20, 40, 80, 40);
    private static final Texture TEXTURE_ALT_TRUE = new Texture(SYMBOL_TEXTURE, 60, 0, 20, 40, 80, 40);

    public ToggleAltSlopeOverlay()
    {
        super(LINES_FALSE_ALL, LINES_TRUE_ALL, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getToggleAltSlopeMode);
    }

    @Override
    public boolean isValidTool(Player player, ItemStack stack)
    {
        return stack.getItem() == FBContent.ITEM_FRAMED_WRENCH.value();
    }

    @Override
    public boolean isValidTarget(Target target)
    {
        return target.state().getBlock() instanceof SlopeToggleBlock;
    }

    @Override
    public boolean getState(Target target)
    {
        return target.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public Texture getTexture(Target target, boolean state)
    {
        if (isHorizontalSlopeSurface(target.state()))
        {
            return state ? TEXTURE_ALT_TRUE : TEXTURE_ALT_FALSE;
        }
        return super.getTexture(target, state);
    }

    @Override
    public List<Component> getLines(Target target, boolean state)
    {
        if (isHorizontalSlopeSurface(target.state()))
        {
            return state ? LINES_TRUE_ALT : LINES_FALSE_ALT;
        }
        else
        {
            return state ? LINES_TRUE : LINES_FALSE;
        }
    }

    private static boolean isHorizontalSlopeSurface(BlockState state)
    {
        return state.getBlock() instanceof SlopeToggleBlock block && block.getSlopeOrientation(state) == SlopeToggleBlock.SlopeOrientation.HORIZONTAL;
    }
}
