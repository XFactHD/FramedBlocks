package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.framedblocks.client.util.DataHolder;

@Mixin(Block.class)
public abstract class MixinBlock extends AbstractBlock
{
    public MixinBlock(Properties properties) { super(properties); }

    @Inject(method = {"shouldRenderFace"}, at = @At("HEAD"))
    private static void shouldSideBeRenderedFramed(BlockState state, IBlockReader world, BlockPos pos, Direction face, CallbackInfoReturnable<Boolean> cir)
    {
        DataHolder.world.set(world);
        DataHolder.pos.set(pos);
    }
}