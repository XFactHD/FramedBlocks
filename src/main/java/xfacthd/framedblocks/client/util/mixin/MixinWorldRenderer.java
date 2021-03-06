package xfacthd.framedblocks.client.util.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.client.render.GhostBlockRenderer;

/**
 * Inspired by https://github.com/MinecraftForge/MinecraftForge/pull/7225/
 */

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer
{
    @Final
    @Shadow
    private RenderTypeBuffers renderTypeTextures;

    @Inject(method = "renderBlockLayer", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearCurrentColor()V"))
    private void renderTransparentLayer(RenderType layer, MatrixStack matrix, double camX, double camY, double camZ, CallbackInfo ci)
    {
        if (layer != RenderType.getTranslucent()) { return; }
        GhostBlockRenderer.drawGhostBlock(renderTypeTextures.getBufferSource(), matrix);
    }
}