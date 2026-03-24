package io.github.xfacthd.framedblocks.mixin.client;

import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.GameRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private boolean framedblocks$lastCutoutLeaves;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void framedblocks$captureInitialCutoutLeaves(
            Minecraft mc,
            EntityRenderDispatcher entityRenderDispatcher,
            BlockEntityRenderDispatcher blockEntityRenderDispatcher,
            RenderBuffers buffers,
            GameRenderState gameRenderState,
            FeatureRenderDispatcher featureRenderDispatcher,
            CallbackInfo ci
    )
    {
        framedblocks$lastCutoutLeaves = mc.options.cutoutLeaves().get();
    }

    @Inject(method = "allChanged", at = @At("HEAD"))
    private void framedblocks$handleRedrawOnCutoutLeavesChange(CallbackInfo ci)
    {
        boolean cutoutLeaves = minecraft.options.cutoutLeaves().get();
        if (cutoutLeaves != framedblocks$lastCutoutLeaves)
        {
            framedblocks$lastCutoutLeaves = cutoutLeaves;
            CacheCleaner.clearModelCaches(CacheCleaner.Reason.SETTINGS_CHANGED);
        }
    }
}
