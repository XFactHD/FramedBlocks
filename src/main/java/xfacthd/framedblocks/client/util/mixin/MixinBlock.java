package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.SideSkipPredicate;

@Mixin(Block.class)
public abstract class MixinBlock extends BlockBehaviour
{
    public MixinBlock(Properties properties) { super(properties); }

    @Inject(method = {"shouldRenderFace"}, at = @At("HEAD"), cancellable = true)
    private static void framedblocks_shouldRenderFace(BlockState state, BlockGetter level, BlockPos pos, Direction face, BlockPos adjPos, CallbackInfoReturnable<Boolean> cir)
    {
        if (state.getBlock() instanceof IFramedBlock || state.isAir() || !(level instanceof RenderChunkRegion chunk)) { return; }

        if (chunk.getBlockEntity(adjPos, LevelChunk.EntityCreationType.CHECK) instanceof FramedBlockEntity be)
        {
            if (state.getBlock() instanceof HalfTransparentBlock && SideSkipPredicate.CTM.test(level, adjPos, level.getBlockState(adjPos), state, face.getOpposite()))
            {
                cir.setReturnValue(false);
            }
            else if (be.isSolidSide(face.getOpposite()))
            {
                cir.setReturnValue(false);
            }
        }
    }
}