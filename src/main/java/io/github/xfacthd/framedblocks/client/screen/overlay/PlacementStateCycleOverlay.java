package io.github.xfacthd.framedblocks.client.screen.overlay;

import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.client.gui.GuiLayer;

import java.util.ArrayList;
import java.util.List;

public final class PlacementStateCycleOverlay implements GuiLayer {
    private static final int PADDING = 9;

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Player player = Minecraft.getInstance().player;
        if (player == null || (!(player.getMainHandItem().getItem() instanceof IFramedBlockItem blockItem)) || !blockItem.isStateCyclingActive(player)) {
            return;
        }

        List<Component> lines = new ArrayList<>();
        blockItem.getStateCycleSpec().appendHoverText(player, (BlockItem) blockItem, lines::add);
        if (lines.isEmpty()) {
            return;
        }

        lines.addFirst(IFramedBlockItem.HEADER_SELECTED_STATE);

        Font font = Minecraft.getInstance().font;
        int lineHeight = font.lineHeight + 1;

        int width = 0;
        for (Component line : lines) {
            width = Math.max(width, font.width(line));
        }
        int height = lineHeight * lines.size() - 1;
        int x = PADDING;
        int y = graphics.guiHeight() - PADDING - 1 - height;
        TooltipRenderUtil.extractTooltipBackground(graphics, x, y, width, height, null);
        y += 1;
        for (Component line : lines) {
            graphics.text(font, line, x, y, 0xFF404040);
            y += lineHeight;
        }
    }
}
