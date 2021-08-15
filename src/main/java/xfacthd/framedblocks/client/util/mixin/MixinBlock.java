package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

//@Mixin(Block.class) //TODO: reactivate when Mixin is available
public abstract class MixinBlock extends BlockBehaviour
{
    public MixinBlock(Properties properties) { super(properties); }

    /*@Inject(method = {"shouldRenderFace"}, at = @At("HEAD"), cancellable = true)
    private static void shouldSideBeRenderedFramed(BlockState state, BlockGetter level, BlockPos pos, Direction face, CallbackInfoReturnable<Boolean> cir)
    {
        //noinspection deprecation
        if (state.getBlock() instanceof IFramedBlock || state.isAir()) { return; }

        BlockPos adjPos = pos.relative(face);
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            if (state.getBlock() instanceof HalfTransparentBlock && SideSkipPredicate.CTM.test(level, adjPos, world.getBlockState(adjPos), state, face.getOpposite()))
            {
                cir.setReturnValue(false);
            }
            else if (be.isSolidSide(face.getOpposite()))
            {
                cir.setReturnValue(false);
            }
        }
    }*/
}