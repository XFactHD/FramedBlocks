package io.github.xfacthd.framedblocks.client.screen;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.menu.FramedStorageMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class FramedStorageScreen extends AbstractContainerScreen<FramedStorageMenu> {
    private static final Identifier CHEST_GUI_TEXTURE = Utils.id("minecraft", "textures/gui/container/generic_54.png");

    private final int invHeight;

    public FramedStorageScreen(FramedStorageMenu menu, Inventory inv, Component title) {
        int rows = menu.getRowCount();
        super(menu, inv, title, DEFAULT_IMAGE_WIDTH, 114 + rows * 18);
        this.inventoryLabelY = imageHeight - 94;
        this.invHeight = rows * 18 + 17;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
        super.extractBackground(graphics, x, y, partialTicks);

        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, CHEST_GUI_TEXTURE, left, top, 0, 0, imageWidth, invHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CHEST_GUI_TEXTURE, left, top + invHeight, 0, 126, imageWidth, 96, 256, 256);
    }
}
