package xfacthd.framedblocks.client.util.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.client.render.GhostBlockRenderer;

/**
 * Inspired by https://github.com/MinecraftForge/MinecraftForge/pull/7225/
 */

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
    @Final
    @Shadow
    private RenderBuffers renderBuffers;

    @Inject(method = "renderChunkLayer", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;unbindVertexArray()V"))
    private void renderTransparentLayer(RenderType layer, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projMat, CallbackInfo ci)
    {
        if (layer != RenderType.translucent()) { return; }
        GhostBlockRenderer.drawGhostBlock(renderBuffers.bufferSource(), poseStack);
    }
}