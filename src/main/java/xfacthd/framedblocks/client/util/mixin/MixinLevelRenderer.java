package xfacthd.framedblocks.client.util.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.client.render.GhostBlockRenderer;

/**
 * Inspired by <a href="https://github.com/MinecraftForge/MinecraftForge/pull/8603/">MinecraftForge#8603</a>
 */

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
    @Final
    @Shadow
    private RenderBuffers renderBuffers;

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V",
                    remap = false
            ),
            remap = true
    )
    @SuppressWarnings("DefaultAnnotationParam")
    private void framedblocks_renderAfterParticles(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projMat, CallbackInfo ci)
    {
        GhostBlockRenderer.drawGhostBlock(renderBuffers.bufferSource(), poseStack);
    }
}