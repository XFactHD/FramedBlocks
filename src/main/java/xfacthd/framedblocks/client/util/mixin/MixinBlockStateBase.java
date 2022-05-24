package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase
{
    @Shadow public abstract Block getBlock();

    @Inject(method = "getMapColor", at = @At("HEAD"), cancellable = true)
    private void framedblocks_getDynamicMapColor(BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<MaterialColor> cir)
    {
        if (getBlock() instanceof IFramedBlock && pLevel.getBlockEntity(pPos) instanceof FramedBlockEntity be)
        {
            MaterialColor color = be.getMapColor();
            if (color != null)
            {
                cir.setReturnValue(color);
            }
        }
    }
}
