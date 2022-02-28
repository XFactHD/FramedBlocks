package xfacthd.framedblocks.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.framedblocks.common.container.FramedStorageContainer;

public class FramedStorageScreen extends ContainerScreen<FramedStorageContainer>
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    public FramedStorageScreen(FramedStorageContainer container, PlayerInventory inv, ITextComponent title)
    {
        super(container, inv, title);

        this.imageHeight = 168;
        this.inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        //noinspection deprecation
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        //noinspection ConstantConditions
        minecraft.getTextureManager().bind(CHEST_GUI_TEXTURE);

        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;

        blit(matrixStack, left, top, 0, 0, imageWidth, 71);
        blit(matrixStack, left, top + 71, 0, 126, imageWidth, 96);
    }
}