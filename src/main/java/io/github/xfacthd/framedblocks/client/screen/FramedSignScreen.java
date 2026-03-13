package io.github.xfacthd.framedblocks.client.screen;

import io.github.xfacthd.framedblocks.client.screen.pip.SignBlockPictureInPictureRenderer;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SignBlock;
import org.joml.Vector3f;

public final class FramedSignScreen extends AbstractSignEditScreen
{
    private static final Component TITLE_NORMAL = Component.translatable("sign.edit");
    private static final Component TITLE_HANGING = Component.translatable("hanging_sign.edit");

    private final SignBlock signBlock;
    private final int signTopY;
    private final int signBottomY;
    private final float signScale;
    private final float signYOffset;
    private final Vector3f signTextScale;

    private FramedSignScreen(
            FramedSignBlockEntity sign,
            boolean isFrontText,
            Component title,
            int signTopY,
            int signBottomY,
            float signScale,
            float signYOffset,
            Vector3f signTextScale
    )
    {
        super(sign, isFrontText, Minecraft.getInstance().isTextFilteringEnabled(), title);
        this.signBlock = (SignBlock) sign.getBlockState().getBlock();
        this.signTopY = signTopY;
        this.signBottomY = signBottomY;
        this.signScale = signScale;
        this.signYOffset = signYOffset;
        this.signTextScale = signTextScale;
    }

    @Override
    protected void renderSignBackground(GuiGraphics graphics)
    {
        int centerX = width / 2;
        int x0 = centerX - 48;
        int x1 = centerX + 48;
        float yRot = signBlock.getYRotationDegrees(sign.getBlockState());
        graphics.submitPictureInPictureRenderState(SignBlockPictureInPictureRenderer.RenderState.create(
                (FramedSignBlockEntity) sign, yRot, x0, signTopY, x1, signBottomY, signScale, graphics.peekScissorStack()
        ));
    }

    @Override
    public float getSignYOffset()
    {
        return signYOffset;
    }

    @Override
    public Vector3f getSignTextScale()
    {
        return signTextScale;
    }

    private static FramedSignScreen normal(FramedSignBlockEntity sign, boolean isFrontText, int signTopY, int signBottomY)
    {
        return new FramedSignScreen(sign, isFrontText, TITLE_NORMAL, signTopY, signBottomY, SignEditScreen.MAGIC_SCALE_NUMBER * 1.5F, 90, SignEditScreen.TEXT_SCALE);
    }

    public static FramedSignScreen standing(FramedSignBlockEntity sign, boolean isFrontText)
    {
        return normal(sign, isFrontText, 66, 170);
    }

    public static FramedSignScreen wall(FramedSignBlockEntity sign, boolean isFrontText)
    {
        return normal(sign, isFrontText, 65, 139);
    }

    public static FramedSignScreen hanging(FramedSignBlockEntity sign, boolean isFrontText)
    {
        return new FramedSignScreen(sign, isFrontText, TITLE_HANGING, 70, 147, SignEditScreen.MAGIC_SCALE_NUMBER * 1.15F, 125, HangingSignEditScreen.TEXT_SCALE);
    }
}
