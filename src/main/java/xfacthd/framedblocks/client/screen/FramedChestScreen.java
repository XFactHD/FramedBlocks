package xfacthd.framedblocks.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import xfacthd.framedblocks.common.container.FramedChestContainer;

public class FramedChestScreen extends AbstractContainerScreen<FramedChestContainer>
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    public FramedChestScreen(FramedChestContainer container, Inventory inv, Component title)
    {
        super(container, inv, title);

        this.imageHeight = 168;
        this.inventoryLabelY = getYSize() - 94;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int x, int y)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CHEST_GUI_TEXTURE);

        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        blit(poseStack, left, top, 0, 0, imageWidth, 71);
        blit(poseStack, left, top + 71, 0, 126, imageWidth, 96);
    }
}