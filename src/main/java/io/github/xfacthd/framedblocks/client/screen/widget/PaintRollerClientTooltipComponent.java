package io.github.xfacthd.framedblocks.client.screen.widget;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.component.PaintRollerContents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public record PaintRollerClientTooltipComponent(Component typeLine, Component countLine, float fillPercent) implements ClientTooltipComponent {
    public static final String LABEL_OVERLAY_TYPE = Utils.translationKey("label", "paint_roller.stored.type");
    public static final String LABEL_OVERLAY_COUNT = Utils.translationKey("label", "paint_roller.stored.count");
    public static final Component VALUE_OVERLAY_TYPE_NONE = Utils.translate("value", "paint_roller.stored.type.none")
            .withStyle(ChatFormatting.WHITE)
            .withStyle(ChatFormatting.ITALIC);
    private static final int LINE_PADDING = 1;
    private static final int LINE_COUNT = 2;
    private static final int BAR_WIDTH = 96;
    private static final int BAR_HEIGHT = 9;
    private static final int BOTTOM_PADDING = 2;

    public PaintRollerClientTooltipComponent(PaintRollerContents contents) {
        Component typeName;
        if (contents.hasOverlay()) {
            Holder<BlockOverlay> overlay = Objects.requireNonNull(contents.overlay());
            typeName = Component.translatable(BlockOverlay.getDescriptionId(overlay)).withStyle(ChatFormatting.WHITE);
        } else {
            typeName = VALUE_OVERLAY_TYPE_NONE;
        }
        Component typeLine = Component.translatable(LABEL_OVERLAY_TYPE, typeName).withStyle(ChatFormatting.GOLD);
        Component countText = Component.literal(String.valueOf(contents.count())).withStyle(ChatFormatting.WHITE);
        Component countLine = Component.translatable(LABEL_OVERLAY_COUNT, countText).withStyle(ChatFormatting.GOLD);
        this(typeLine, countLine, contents.getFillPercent());
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        graphics.text(font, typeLine, x, y, 0xFFFFFFFF);
        graphics.text(font, countLine, x, y + font.lineHeight + LINE_PADDING, 0xFFFFFFFF);
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        int xo1 = x + BAR_WIDTH;
        int yo0 = y + (font.lineHeight + 1) * 2;
        int yo1 = yo0 + BAR_HEIGHT;
        int xi0 = x + 1;
        int xi1 = xi0 + (int) (fillPercent * (BAR_WIDTH - 2));
        int xi2 = x + BAR_WIDTH - 1;
        graphics.fill(x, yo0, xo1, yo1, 0xFF404040);
        graphics.fill(xi0, yo0 + 1, xi1, yo1 - 1, 0xFF00FF00);
        graphics.fill(xi1, yo0 + 1, xi2, yo1 - 1, 0xFF000000);
    }

    @Override
    public int getHeight(Font font) {
        return (font.lineHeight + LINE_PADDING) * LINE_COUNT + BAR_HEIGHT + BOTTOM_PADDING;
    }

    @Override
    public int getWidth(Font font) {
        int maxTextWidth = Math.max(font.width(typeLine), font.width(countLine));
        return Math.max(maxTextWidth, BAR_WIDTH);
    }

    @Override
    public boolean showTooltipWithItemInHand() {
        return true;
    }
}
