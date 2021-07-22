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

        this.imageHeight = 168;
        this.inventoryLabelY = getYSize() - 94;
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