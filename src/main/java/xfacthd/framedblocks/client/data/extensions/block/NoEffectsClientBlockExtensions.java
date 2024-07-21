package xfacthd.framedblocks.client.data.extensions.block;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;

public final class NoEffectsClientBlockExtensions extends FramedBlockRenderProperties
{
    public static final NoEffectsClientBlockExtensions INSTANCE = new NoEffectsClientBlockExtensions();

    private NoEffectsClientBlockExtensions() { }

    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager)
    {
        return true;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager)
    {
        return true;
    }
}
