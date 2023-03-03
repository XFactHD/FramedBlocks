package xfacthd.framedblocks.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import xfacthd.framedblocks.api.util.*;

public final class ToggleYSlopeOverlay extends BlockInteractOverlay
{
    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/gui/yslope_symbols.png");
    public static final String SLOPE_MESSAGE = "tooltip." + FramedConstants.MOD_ID + ".y_slope";
    public static final String TOGGLE_MESSAGE = "tooltip." + FramedConstants.MOD_ID +  ".y_slope.toggle";
    public static final Component SLOPE_HOR = Utils.translate("tooltip", "y_slope.horizontal");
    public static final Component SLOPE_VERT = Utils.translate("tooltip", "y_slope.vertical");

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height)
    {
        ItemStack stack = player().getMainHandItem();
        if (!stack.is(Utils.WRENCH)) { return; }

        BlockState state = getTargettedBlock();
        if (!state.hasProperty(FramedProperties.Y_SLOPE)) { return; }

        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        Component textOne = Component.translatable(SLOPE_MESSAGE, ySlope ? SLOPE_VERT : SLOPE_HOR);
        GuiComponent.drawCenteredString(poseStack, gui.getFont(), textOne, width / 2, (height / 2) + 30, -1);

        RenderSystem.setShaderTexture(0, SYMBOL_TEXTURE);
        GuiComponent.blit(poseStack, (width / 2) - 9, (height / 2) + 40, 0, ySlope ? 21 : 1, 1, 18, 17, 40, 20);

        Component textTwo = Component.translatable(TOGGLE_MESSAGE, ySlope ? SLOPE_HOR : SLOPE_VERT);
        GuiComponent.drawCenteredString(poseStack, gui.getFont(), textTwo, width / 2, (height / 2) + 60, -1);
    }
}
