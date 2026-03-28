package io.github.xfacthd.framedblocks.client.data.extensions.block;

import io.github.xfacthd.framedblocks.api.block.render.FramedClientBlockExtensions;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

public final class NoEffectsClientBlockExtensions extends FramedClientBlockExtensions {
    public static final NoEffectsClientBlockExtensions INSTANCE = new NoEffectsClientBlockExtensions();

    private NoEffectsClientBlockExtensions() { }

    @Override
    public boolean addHitEffects(BlockState state, Level level, @Nullable HitResult target, ParticleEngine manager) {
        return true;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager) {
        return true;
    }

    @Override
    public boolean playHitSound(BlockState state, Level level, BlockPos pos, Direction hitFace, SoundManager soundManager) {
        return true;
    }

    @Override
    public boolean playBreakSound(BlockState state, Level level, BlockPos pos) {
        return false;
    }
}
