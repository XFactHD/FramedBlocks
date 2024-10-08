package xfacthd.framedblocks.mixin.client;

import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.client.util.FramedClientUtils;

// Break sounds are not important, apply after other mixins and bail if someone else already modified the target
@Mixin(value = LevelRenderer.class, priority = 10000)
public class MixinLevelRenderer
{
    @Shadow
    private ClientLevel level;
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private GraphicsStatus framedblocks$lastGraphicsMode;

    @Redirect(
            method = "levelEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"
            ),
            require = 0
    )
    private boolean framedblocks$playCustomBreakSound(BlockState state, int type, BlockPos pos, int data)
    {
        if (state.getBlock() instanceof IFramedBlock block && block.playBreakSound(state, level, pos))
        {
            return true;
        }
        return state.isAir();
    }

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
