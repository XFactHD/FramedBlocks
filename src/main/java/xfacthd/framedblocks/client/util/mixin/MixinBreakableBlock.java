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

    @Inject(method = {"skipRendering"}, at = @At("HEAD"), cancellable = true)
    private void isSideInvisibleFramed(BlockState state, BlockState adjState, Direction side, CallbackInfoReturnable<Boolean> cir)
    {
        if (Utils.OPTIFINE_LOADED.get() || Utils.SODIUM_LOADED.get()) { return; } //Should fix crash with OptiFine and Sodium

        if (adjState.getBlock() instanceof IFramedBlock block && block.getCtmPredicate().test(adjState, side.getOpposite()))
        {
            TileEntity te = DataHolder.world.get().getBlockEntity(DataHolder.pos.get().relative(side));
            if (te instanceof FramedTileEntity fte)
            {
                if (fte.hidesAdjacentFace(state, side))
                {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}