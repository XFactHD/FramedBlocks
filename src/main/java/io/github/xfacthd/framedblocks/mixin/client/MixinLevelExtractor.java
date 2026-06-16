package io.github.xfacthd.framedblocks.mixin.client;

import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelExtractor.class)
public class MixinLevelExtractor {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private boolean framedblocks$lastCutoutLeaves;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void framedblocks$captureInitialCutoutLeaves(Minecraft minecraft, LevelRenderState levelRenderState, LevelRenderer levelRenderer, CallbackInfo ci) {
        framedblocks$lastCutoutLeaves = minecraft.options.cutoutLeaves().get();
    }

    @Inject(method = "allChanged", at = @At("HEAD"))
    private void framedblocks$handleRedrawOnCutoutLeavesChange(CallbackInfo ci) {
        boolean cutoutLeaves = minecraft.options.cutoutLeaves().get();
        if (cutoutLeaves != framedblocks$lastCutoutLeaves) {
            framedblocks$lastCutoutLeaves = cutoutLeaves;
            CacheCleaner.clearModelCaches(CacheCleaner.Reason.SETTINGS_CHANGED);
        }
    }
}
