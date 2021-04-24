package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.block.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.framedblocks.client.util.DataHolder;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.Utils;

@Mixin(BreakableBlock.class)
public abstract class MixinBreakableBlock extends Block
{
    public MixinBreakableBlock(Properties properties) { super(properties); }

    @Inject(method = {"isSideInvisible(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/Direction;)Z"}, at = @At("HEAD"), cancellable = true)
    private void isSideInvisibleFramed(BlockState state, BlockState adjState, Direction side, CallbackInfoReturnable<Boolean> cir)
    {
        if (Utils.OPTIFINE_LOADED.getValue() || Utils.SODIUM_LOADED.getValue()) { return; } //Should fix crash with OptiFine and Sodium

        if (adjState.getBlock() instanceof IFramedBlock && ((IFramedBlock)adjState.getBlock()).getCtmPredicate().test(adjState, side.getOpposite()))
        {
            TileEntity te = DataHolder.world.get().getTileEntity(DataHolder.pos.get().offset(side));
            if (te instanceof FramedTileEntity)
            {
                if (((FramedTileEntity)te).hidesAdjacentFace(state, side))
                {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}