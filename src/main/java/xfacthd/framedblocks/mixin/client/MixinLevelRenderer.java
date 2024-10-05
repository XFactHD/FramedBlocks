package xfacthd.framedblocks.mixin.client;

import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.client.util.FramedClientUtils;

@Debug(export = true)
@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private GraphicsStatus framedblocks$lastGraphicsMode;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void framedblocks$captureInitialGraphicsMode(Minecraft mc, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, RenderBuffers buffers, CallbackInfo ci)
    {
        framedblocks$lastGraphicsMode = mc.options.graphicsMode().get();
    }

    @Inject(method = "allChanged", at = @At("HEAD"))
    private void framedblocks$handleRedrawOnGraphicsModeChange(CallbackInfo ci)
    {
        GraphicsStatus graphicsMode = minecraft.options.graphicsMode().get();
        if (graphicsMode != framedblocks$lastGraphicsMode)
        {
            framedblocks$lastGraphicsMode = graphicsMode;
            FramedClientUtils.clearModelCaches();
        }
    }
}
