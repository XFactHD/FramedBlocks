package io.github.xfacthd.framedblocks.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.model.util.TintUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.Rect;
import io.github.xfacthd.framedblocks.common.data.dynreg.BlockOverlayCache;
import io.github.xfacthd.framedblocks.common.menu.PaintRollerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public final class PaintRollerScreen extends AbstractContainerScreen<PaintRollerMenu> {
    private static final Identifier BACKGROUND = Utils.id("textures/gui/paint_roller.png");
    private static final Identifier ENTRY_BACKGROUND = Utils.id("minecraft", "toast/tutorial");
    private static final int LIST_BORDER = 8;
    private static final int LIST_BORDER_TOP = 18;
    private static final int LIST_OUTER_WIDTH = 144;
    private static final int LIST_INNER_WIDTH = LIST_OUTER_WIDTH - (LIST_BORDER * 2);
    private static final int LIST_OUTER_HEIGHT = 176;
    private static final int LIST_INNER_HEIGHT = LIST_OUTER_HEIGHT - LIST_BORDER - LIST_BORDER_TOP;
    private static final int ENTRY_HEIGHT = 24;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLLER_DRAG_HEIGHT = LIST_INNER_HEIGHT - SCROLLER_HEIGHT;
    private static final int ENTRY_PADDING = 4;
    private static final int DETAILS_OFF_X = 24;
    private static final int ITEM_OFF_Y = 12;
    private static final int ICON_SIZE = 16;
    public static final String LABEL_SOURCE_ITEM = Utils.translationKey("label", "paint_roller.source_item");

    private final BlockOverlayCache overlayCache = BlockOverlayCache.get(true);
    private final TextureAtlas blockAtlas = minecraft.getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
    private final int totalListHeight;
    private final boolean needScroller;
    private final int maxScrollOffset;
    private Rect listArea = Rect.EMPTY;
    private Rect listContent = Rect.EMPTY;
    private Rect scrollBar = Rect.EMPTY;
    private int scrollOffset = 0;

    public PaintRollerScreen(PaintRollerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, LIST_OUTER_WIDTH, LIST_OUTER_HEIGHT);
        this.totalListHeight = overlayCache.getOverlays().size() * ENTRY_HEIGHT;
        this.needScroller = totalListHeight > LIST_INNER_HEIGHT;
        this.maxScrollOffset = totalListHeight - LIST_INNER_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        listArea = new Rect(leftPos + LIST_BORDER, topPos + LIST_BORDER_TOP, LIST_INNER_WIDTH, LIST_INNER_HEIGHT);
        if (needScroller) {
            listContent = new Rect(listArea.x0(), listArea.y0(), listArea.width() - SCROLLER_WIDTH, listArea.height());
            scrollBar = new Rect(listArea.x1() - SCROLLER_WIDTH, listArea.y0(), SCROLLER_WIDTH, LIST_INNER_HEIGHT);
        } else {
            listContent = listArea;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);

        graphics.enableScissor(listContent.x0(), listContent.y0(), listContent.x1(), listContent.y1());
        int entryY = listContent.y0() - scrollOffset;
        for (Holder<BlockOverlay> overlay : overlayCache.getOverlays()) {
            if (entryY + ENTRY_HEIGHT > listContent.y0() && entryY <= listContent.y1()) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENTRY_BACKGROUND, listContent.x0(), entryY, listContent.width(), ENTRY_HEIGHT);

                TextureAtlasSprite sprite = blockAtlas.getSprite(overlay.value().solidTexture());
                int tint = TintUtils.getOverlayDefaultTint(overlay.value());
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, listContent.x0() + ENTRY_PADDING, entryY + ENTRY_PADDING, ICON_SIZE, ICON_SIZE, tint);

                Component name = BlockOverlay.getName(overlay);
                Component styledName = name.copy().withStyle(style -> style.withColor(CommonColors.DARK_GRAY).withoutShadow());
                graphics.drawScrollingString(graphics.textRenderer(), font, styledName, listContent.x0() + DETAILS_OFF_X, listContent.x1() - ENTRY_PADDING, entryY + ENTRY_PADDING);

                ItemStack srcItem = overlay.value().sourceItem().value().getDefaultInstance();
                graphics.pose().pushMatrix();
                graphics.pose().translate(listContent.x0() + DETAILS_OFF_X, entryY + ITEM_OFF_Y);
                graphics.pose().scale(.5F, .5F);
                graphics.fakeItem(srcItem, 0, 0);
                graphics.pose().popMatrix();

                if (listContent.contains(mouseX, mouseY) && mouseY >= entryY && mouseY < entryY + ENTRY_HEIGHT) {
                    Component itemName = srcItem.getItemName().copy().withStyle(ChatFormatting.WHITE);
                    List<Component> lines = List.of(
                            name,
                            Component.translatable(LABEL_SOURCE_ITEM, itemName).withStyle(ChatFormatting.GOLD)
                    );
                    graphics.setTooltipForNextFrame(font, lines, Optional.empty(), mouseX, mouseY);
                }
            }
            entryY += ENTRY_HEIGHT;
        }
        graphics.disableScissor();

        if (needScroller) {
            int scrollX = listContent.x1();
            float scrollFactor = scrollOffset / (float) (totalListHeight - LIST_INNER_HEIGHT);
            int scrollY = listContent.y0() + (int) ((LIST_INNER_HEIGHT - SCROLLER_HEIGHT) * scrollFactor);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Utils.id("minecraft", "container/villager/scroller"), scrollX, scrollY, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT && scrollBar.contains((int) event.x(), (int) event.y())) {
            setDragging(true);
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (listArea.contains((int) x, (int) y)) {
            scrollOffset = Math.clamp(scrollOffset - (int) (scrollY * 10), 0, maxScrollOffset);
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (isDragging()) {
            double yRel = Math.clamp(event.y() - scrollBar.y0() - (SCROLLER_HEIGHT / 2D), 0, SCROLLER_DRAG_HEIGHT);
            scrollOffset = (int) Math.round(maxScrollOffset * (yRel / SCROLLER_DRAG_HEIGHT));
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }
}
