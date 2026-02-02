package io.github.xfacthd.framedblocks.api.block.render;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.sound.SoundEventType;
import io.github.xfacthd.framedblocks.api.util.sound.SoundUtils;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class FramedClientDoubleBlockExtensions extends FramedClientBlockExtensions
{
    public static final FramedClientDoubleBlockExtensions INSTANCE = new FramedClientDoubleBlockExtensions();

    private FramedClientDoubleBlockExtensions() { }

    @Override
    public boolean addHitEffects(BlockState state, Level level, @Nullable HitResult target, ParticleEngine engine)
    {
        BlockHitResult hit = (BlockHitResult) Objects.requireNonNull(target);
        boolean suppressed = suppressParticles(state, level, hit.getBlockPos());
        if (!suppressed && level.getBlockEntity(hit.getBlockPos()) instanceof FramedDoubleBlockEntity be)
        {
            ParticleHelper.Client.addHitEffects(state, level, hit, be.getCamo().getContent(), engine);
            ParticleHelper.Client.addHitEffects(state, level, hit, be.getCamoTwo().getContent(), engine);
            return true;
        }
        return suppressed;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine engine)
    {
        boolean suppressed = suppressParticles(state, level, pos);
        if (!suppressed && level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            ParticleHelper.Client.addDestroyEffects(state, level, pos, be.getCamo().getContent(), engine);
            ParticleHelper.Client.addDestroyEffects(state, level, pos, be.getCamoTwo().getContent(), engine);
            return true;
        }
        return suppressed;
    }

    @Override
    public boolean playHitSound(BlockState state, Level level, BlockPos pos, Direction hitFace, SoundManager soundManager)
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            SoundType soundOne = be.getCamo().getContent().getSoundType();
            SoundUtils.Client.playHitSound(soundManager, pos, soundOne);

            SoundType soundTwo = be.getCamoTwo().getContent().getSoundType();
            if (!SoundUtils.isSameSound(soundOne, soundTwo, SoundEventType.HIT))
            {
                SoundUtils.Client.playHitSound(soundManager, pos, soundTwo);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean playBreakSound(BlockState state, Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            SoundType soundOne = be.getCamo().getContent().getSoundType();
            SoundUtils.Client.playBreakSound(level, pos, soundOne);

            SoundType soundTwo = be.getCamoTwo().getContent().getSoundType();
            if (!SoundUtils.isSameSound(soundOne, soundTwo, SoundEventType.BREAK))
            {
                SoundUtils.Client.playBreakSound(level, pos, soundTwo);
            }

            return true;
        }
        return false;
    }
}
