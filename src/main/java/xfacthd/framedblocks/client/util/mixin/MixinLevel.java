package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.block.FramedDoorBlock;
import xfacthd.framedblocks.common.block.FramedTrapDoorBlock;

import javax.annotation.Nullable;

@Mixin(Level.class)
public abstract class MixinLevel
{
    @Shadow
    public abstract boolean isClientSide();

    @Shadow @Nullable public abstract BlockEntity getBlockEntity(BlockPos pPos);

    @Inject(
            method = "markAndNotifyBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;onBlockStateChange(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V",
                    shift = At.Shift.AFTER,
                    remap = true
            ),
            remap = false
    )
    @SuppressWarnings("DefaultAnnotationParam")
    private void framedblocks_postNeighborUpdate(BlockPos pPos, LevelChunk levelchunk, BlockState blockstate, BlockState pState, int pFlags, int pRecursionLeft, CallbackInfo ci)
    {
        Block block = pState.getBlock();
        if (isClientSide() && block instanceof IFramedBlock framedBlock && getBlockEntity(pPos) instanceof FramedBlockEntity be)
        {
            if (block instanceof FramedDoorBlock || block instanceof FramedTrapDoorBlock)
            {
                framedBlock.onStateChangeClient(levelchunk.getLevel(), pPos, blockstate, pState, be);
            }
        }
    }
}