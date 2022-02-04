package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.block.*;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

@Mixin(Block.class)
public abstract class MixinBlock extends AbstractBlock
{
    public MixinBlock(Properties properties) { super(properties); }

    @Inject(method = {"shouldSideBeRendered"}, at = @At("HEAD"), cancellable = true)
    private static void framedblocks_shouldSideBeRendered(BlockState state, IBlockReader world, BlockPos pos, Direction face, CallbackInfoReturnable<Boolean> cir)
    {
        //noinspection deprecation
        if (state.getBlock() instanceof IFramedBlock || state.isAir() || !(world instanceof ChunkRenderCache)) { return; }

        BlockPos adjPos = pos.offset(face);
        TileEntity te = ((ChunkRenderCache)world).getTileEntity(adjPos, Chunk.CreateEntityType.CHECK);
        if (te instanceof FramedTileEntity)
        {
            FramedTileEntity fte = (FramedTileEntity) te;

            if (!fte.isIntangible(null))
            {
                if (state.getBlock() instanceof BreakableBlock && SideSkipPredicate.CTM.test(world, adjPos, world.getBlockState(adjPos), state, face.getOpposite()))
                {
                    cir.setReturnValue(false);
                }
                else if (fte.isSolidSide(face.getOpposite()))
                {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}