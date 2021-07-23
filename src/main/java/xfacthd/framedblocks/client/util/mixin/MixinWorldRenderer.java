package xfacthd.framedblocks.client.util.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import xfacthd.framedblocks.client.render.GhostBlockRenderer;

/**
 * Inspired by https://github.com/MinecraftForge/MinecraftForge/pull/7225/
 */

//@Mixin(LevelRenderer.class) //TODO: reactivate when Mixin is available
public class MixinWorldRenderer
{
    /*@Final
    @Shadow
    private RenderBuffers renderBuffers;

    @Inject(method = "renderChunkLayer", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearCurrentColor()V"))
    private void renderTransparentLayer(RenderType layer, PoseStack matrix, double camX, double camY, double camZ, CallbackInfo ci)
    {
        if (layer != RenderType.translucent()) { return; }
        GhostBlockRenderer.drawGhostBlock(renderBuffers.bufferSource(), matrix);
    }*/
}