package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockState
{
    @Shadow public abstract Block getBlock();

    @Inject(method = "getMapColor", at = @At("HEAD"), cancellable = true)
    private void framedblocks_getDynamicMapColor(IBlockReader pWorld, BlockPos pPos, CallbackInfoReturnable<MaterialColor> cir)
    {
        if (!(getBlock() instanceof IFramedBlock))
        {
            return;
        }

        TileEntity te = pWorld.getBlockEntity(pPos);
        if (te instanceof FramedTileEntity)
        {
            MaterialColor color = ((FramedTileEntity) te).getMapColor();
            if (color != null)
            {
                cir.setReturnValue(color);
            }
        }
    }
}
