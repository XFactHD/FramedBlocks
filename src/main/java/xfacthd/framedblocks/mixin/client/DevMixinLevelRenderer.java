package xfacthd.framedblocks.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.client.render.debug.FramedBlockDebugRenderer;

@Mixin(LevelRenderer.class)
@SuppressWarnings("MethodMayBeStatic")
public class DevMixinLevelRenderer
{
    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V",
                    ordinal = 1
            )
    )
    private void framedblocks$renderAfterBlockEntitiesBeforeBatchEnd(
            DeltaTracker delta,
            boolean blockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightTex,
            Matrix4f frustumMat,
            Matrix4f projMat,
            CallbackInfo ci,
            @Local PoseStack poseStack
    )
    {
        FramedBlockDebugRenderer.render(delta, camera, poseStack);
    }
}
