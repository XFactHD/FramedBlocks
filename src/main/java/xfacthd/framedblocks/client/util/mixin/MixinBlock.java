package xfacthd.framedblocks.client.util.mixin;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

@Mixin(Block.class)
public abstract class MixinBlock extends AbstractBlock
{
    public MixinBlock(Properties properties) { super(properties); }

    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void framedblocks_shouldSideBeRendered(BlockState state, IBlockReader world, BlockPos pos, Direction face, CallbackInfoReturnable<Boolean> cir)
    {
        //noinspection deprecation
        if (state.getBlock() instanceof IFramedBlock || state.isAir() || !(world instanceof ChunkRenderCache)) { return; }

        BlockPos adjPos = pos.relative(face);
        TileEntity te = ((ChunkRenderCache)world).getBlockEntity(adjPos, Chunk.CreateEntityType.CHECK);
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

    /**
     * Replicates the 1.18 implementation of {@link Block#shouldRenderFace(BlockState, IBlockReader, BlockPos, Direction)},
     * which returns early when the first {@link VoxelShape} is empty
     * TODO: PR to Forge
     */
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
            method = "shouldRenderFace",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/block/BlockState;getFaceOcclusionShape(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)Lnet/minecraft/util/math/shapes/VoxelShape;",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void framedblocks_emptyShapeEarlyExit(BlockState p_176225_0_, IBlockReader p_176225_1_, BlockPos p_176225_2_, Direction p_176225_3_, CallbackInfoReturnable<Boolean> cir, BlockPos blockpos, BlockState blockstate, Block.RenderSideCacheKey block$rendersidecachekey, Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap, byte b0, VoxelShape voxelshape)
    {
        if (voxelshape.isEmpty())
        {
            cir.setReturnValue(true);
        }
    }
}