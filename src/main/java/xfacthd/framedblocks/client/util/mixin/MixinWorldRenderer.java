package xfacthd.framedblocks.client.util.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.*;
import net.minecraft.util.math.vector.Matrix4f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.client.render.GhostBlockRenderer;

/**
 * Inspired by <a href="https://github.com/MinecraftForge/MinecraftForge/pull/8603/">MinecraftForge#8603</a>
 */

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer
{
    @Final
    @Shadow
    private RenderTypeBuffers renderBuffers;

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V",
                    remap = false
            ),
            remap = true
    )
    @SuppressWarnings("DefaultAnnotationParam")
    private void framedblocks_renderAfterParticles(MatrixStack matrix, float partialTick, long finishNanoTime, boolean renderBlockOutline, ActiveRenderInfo camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projMat, CallbackInfo ci)
    {
        GhostBlockRenderer.drawGhostBlock(renderBuffers.bufferSource(), matrix);
    }
}