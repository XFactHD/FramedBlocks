package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.api.block.IFramedBlock;

@Mixin(Level.class)
public abstract class MixinLevel
{
    @Shadow
    public abstract boolean isClientSide();

    @Inject(
            method = "markAndNotifyBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;onBlockStateChange(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void framedblocks_postNeighborUpdate(BlockPos pPos, LevelChunk levelchunk, BlockState blockstate, BlockState pState, int pFlags, int pRecursionLeft, CallbackInfo ci)
    {
        if (isClientSide() && pState.getBlock() instanceof IFramedBlock block)
        {
            block.onStateChangeClient(levelchunk.getLevel(), pPos, blockstate, pState);
        }
    }
}
