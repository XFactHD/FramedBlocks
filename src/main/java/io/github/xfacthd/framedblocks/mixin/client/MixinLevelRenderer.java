package io.github.xfacthd.framedblocks.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.xfacthd.framedblocks.client.render.block.FramedChestRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.TransientBlockRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Inject(
            method = "performTransientBlockRemovals",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;getInstance()Lnet/minecraft/client/Minecraft;",
                    shift = At.Shift.BEFORE
            )
    )
    @SuppressWarnings("MethodMayBeStatic")
    private void framedblocks$cleanupTransientChestLids(CallbackInfo ci, @Local TransientBlockRenderState.Removal removal) {
        FramedChestRenderer.removeClosedChests(removal.sectionNode(), removal.compileTaskStartTimeNs());
    }
}
