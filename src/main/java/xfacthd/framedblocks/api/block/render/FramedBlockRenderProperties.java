package xfacthd.framedblocks.api.block.render;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;

public class FramedBlockRenderProperties implements IClientBlockExtensions
{
    public static final FramedBlockRenderProperties INSTANCE = new FramedBlockRenderProperties();

    protected FramedBlockRenderProperties() { }

    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine engine)
    {
        BlockHitResult hit = (BlockHitResult) target;
        boolean suppressed = suppressParticles(state, level, hit.getBlockPos());
        if (!suppressed && level.getBlockEntity(hit.getBlockPos()) instanceof FramedBlockEntity be)
        {
            ParticleHelper.Client.addHitEffects(state, level, hit, be.getCamo().getState(), engine);
            return true;
        }
        return suppressed;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine engine)
    {
        boolean suppressed = suppressParticles(state, level, pos);
        if (!suppressed && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            ParticleHelper.Client.addDestroyEffects(state, level, pos, be.getCamo().getState(), engine);
            return true;
        }
        return suppressed;
    }



    protected static boolean suppressParticles(BlockState state, Level level, BlockPos pos)
    {
        if (state.getBlock() instanceof IFramedBlock block && block.getBlockType().allowMakingIntangible())
        {
            return block.isIntangible(state, level, pos, null);
        }
        return false;
    }
}
