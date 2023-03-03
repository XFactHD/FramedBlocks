package xfacthd.framedblocks.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.gui.ForgeIngameGui;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;

public final class StateLockOverlay extends BlockInteractOverlay
{
    public static final String LOCK_MESSAGE = "tooltip." + FramedConstants.MOD_ID + ".lock_state";

    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height)
    {
        ItemStack stack = player().getMainHandItem();
        if (!stack.is(FBContent.itemFramedKey.get())) { return; }

        BlockState state = getTargettedBlock();
        if (!(state.getBlock() instanceof IFramedBlock block) || !block.getBlockType().canLockState()) { return; }

        boolean locked = state.getValue(FramedProperties.STATE_LOCKED);

        Component text = new TranslatableComponent(LOCK_MESSAGE, locked ? IFramedBlock.STATE_LOCKED : IFramedBlock.STATE_UNLOCKED);
        GuiComponent.drawCenteredString(poseStack, gui.getFont(), text, width / 2, (height / 2) + 30, -1);

        RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
        GuiComponent.blit(poseStack, (width / 2) - 9, (height / 2) + 40, 0, locked ? 1 : 21, 167, 18, 17, 256, 256);
    }
}
