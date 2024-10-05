package xfacthd.framedblocks.mixin.client;

import net.minecraft.client.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.client.util.FramedClientUtils;

@Mixin(Options.class)
public class MixinOptions
{
    @Inject(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;allChanged()V"))
    private static void framedblocks$captureGraphicsModeChange(OptionInstance<GraphicsStatus> option, GraphicsStatus status, CallbackInfo ci)
    {
        FramedClientUtils.clearModelCaches();
    }
}
