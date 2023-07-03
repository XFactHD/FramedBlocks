package xfacthd.framedblocks.api.block.render;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import xfacthd.framedblocks.api.block.IFramedBlock;

public class FramedBlockRenderProperties implements IClientBlockExtensions
{
    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager)
    {
        return suppressParticles(state, level, ((BlockHitResult) target).getBlockPos());
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager)
    {
        return suppressParticles(state, level, pos);
    }

    private static boolean suppressParticles(BlockState state, Level level, BlockPos pos)
    {
        if (state.getBlock() instanceof IFramedBlock block && block.getBlockType().allowMakingIntangible())
        {
            return block.isIntangible(state, level, pos, null);
        }
        return false;
    }
}
