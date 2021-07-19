package xfacthd.framedblocks.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.framedblocks.common.container.FramedChestContainer;

public class FramedChestScreen extends ContainerScreen<FramedChestContainer>
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    public FramedChestScreen(FramedChestContainer container, PlayerInventory inv, ITextComponent title)
    {
        super(container, inv, title);

        this.ySize = 168;
        this.playerInventoryTitleY = ySize - 94;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        //noinspection deprecation
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        //noinspection ConstantConditions
        minecraft.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);

        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;

        blit(matrixStack, left, top, 0, 0, xSize, 71);
        blit(matrixStack, left, top + 71, 0, 126, xSize, 96);
    }
}