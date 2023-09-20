package xfacthd.framedblocks.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xfacthd.framedblocks.api.block.IFramedBlock;

// Break sounds are not important, apply after other mixins and bail if someone else already modified the target
@Mixin(value = LevelRenderer.class, priority = 10000)
public class MixinLevelRenderer
{
    @Shadow
    private ClientLevel level;

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
}
