package io.github.xfacthd.framedblocks.client.screen.overlay;

import io.github.xfacthd.framedblocks.api.screen.overlay.BlockInteractOverlay;
import io.github.xfacthd.framedblocks.api.screen.overlay.OverlayDisplayMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;

final class BlockInteractOverlayWrapper {
    private static final int LINE_DIST = 3;
    private static final int ICON_MARGIN = 20;
    private static final int TOOLTIP_MARGIN = 10;
    private static final int PADDING = 10;
    private static final int DEFAULT_Y_OFF = 80;

    private final Identifier name;
    private final BlockInteractOverlay overlay;
    private int textWidth = 0;
    boolean textWidthValid = false;

    BlockInteractOverlayWrapper(Identifier name, BlockInteractOverlay overlay) {
        this.name = name;
        this.overlay = overlay;
    }

    boolean render(GuiGraphicsExtractor graphics, Player player, OverlayDisplayMode cfgMode) {
        OverlayDisplayMode mode = cfgMode.constrain(overlay.getDisplayMode());
        if (mode == OverlayDisplayMode.HIDDEN) {
            return false;
        }

        ItemStack stack = player.getMainHandItem();
        if (!overlay.isValidTool(player, stack)) {
            return false;
        }

        BlockInteractOverlay.Target target = getTargettedBlock(player);
        if (target == null || !overlay.isValidTarget(target)) {
            return false;
        }

        boolean state = overlay.getState(target);
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        BlockInteractOverlay.Texture tex = overlay.getTexture(target, state);
        int texX = centerX + ICON_MARGIN;
        int texY = centerY - (tex.height() / 2);
        tex.draw(graphics, texX, texY);
        overlay.renderAfterIcon(graphics, tex, texX, texY, target);

        if (isDetailedVisible(mode, player)) {
            List<Component> lines = overlay.getLines(target, state);
            renderDetailed(graphics, tex, lines, centerX, screenHeight, target);
        }

        return true;
    }

    private static boolean isDetailedVisible(OverlayDisplayMode mode, Player player) {
        return switch (mode) {
            case HIDDEN, ICON -> false;
            case DETAILED_TOGGLE -> player.isShiftKeyDown();
            case DETAILED_ALWAYS -> true;
        };
    }

    private void renderDetailed(
            GuiGraphicsExtractor graphics,
            BlockInteractOverlay.Texture tex,
            List<Component> lines,
            int centerX,
            int screenHeight,
            BlockInteractOverlay.Target target
    ) {
        Font font = Minecraft.getInstance().font;
        if (!textWidthValid) {
            updateTextWidth(font);
        }

        Hud hud = Minecraft.getInstance().gui.hud;
        int lineHeight = font.lineHeight + LINE_DIST;
        int count = lines.size();
        int contentHeight = count * lineHeight - LINE_DIST;

        int width = textWidth + tex.width() + PADDING;
        int height = Math.max(contentHeight, tex.height());
        int minY = screenHeight / 2 + tex.height() / 2;
        int maxY = screenHeight - Math.min(hud.leftHeight, hud.rightHeight);
        int x = centerX - (width / 2);
        int y = Math.max(screenHeight - DEFAULT_Y_OFF - height, minY + TOOLTIP_MARGIN);
        if (y + height > maxY - TOOLTIP_MARGIN) {
            return;
        }

        drawTooltipBackground(graphics, x, y, width, height);

        int textX = x + tex.width() + PADDING;
        int yBaseOff = tex.height() > contentHeight ? ((tex.height() - contentHeight) / 2) : 0;
        for (int i = 0; i < count; i++) {
            Component text = lines.get(i);
            int yOff = yBaseOff + lineHeight * i;
            graphics.text(font, text, textX, y + yOff, -1);
        }

        int texY = y + (height / 2) - (tex.height() / 2);
        tex.draw(graphics, x, texY);
        overlay.renderAfterIcon(graphics, tex, x, texY, target);
    }

    private void updateTextWidth(Font font) {
        textWidth = 0;
        for (Component line : overlay.getAllLines()) {
            textWidth = Math.max(textWidth, font.width(line));
        }
        textWidthValid = true;
    }

    Identifier getName() {
        return name;
    }

    private static BlockInteractOverlay.@Nullable Target getTargettedBlock(Player player) {
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit instanceof BlockHitResult blockHit) {
            Level level = player.level();
            BlockPos pos = blockHit.getBlockPos();
            return new BlockInteractOverlay.Target(level, pos, level.getBlockState(pos), blockHit.getDirection(), player);
        }
        return null;
    }

    private static void drawTooltipBackground(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        TooltipRenderUtil.extractTooltipBackground(graphics, x - 2, y - 2, width + 4, height + 4, null);
    }
}
