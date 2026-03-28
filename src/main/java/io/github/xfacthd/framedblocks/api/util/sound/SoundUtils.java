package io.github.xfacthd.framedblocks.api.util.sound;

import io.github.xfacthd.framedblocks.api.datamaps.SoundEventGroup;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public final class SoundUtils {
    public static void playPlaceSound(BlockPlaceContext context, SoundType soundType, boolean serverOnly) {
        context.getLevel().playSound(
                serverOnly ? null : context.getPlayer(),
                context.getClickedPos(),
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1F) / 2F,
                soundType.getPitch() * .8F
        );
    }

    public static void playStepSound(Entity entity, SoundType soundType, float volumeMult, float pitchMult) {
        entity.playSound(soundType.getStepSound(), soundType.getVolume() * volumeMult, soundType.getPitch() * pitchMult);
    }

    public static void playFallSound(Entity entity, SoundType soundType) {
        entity.playSound(soundType.getFallSound(), soundType.getVolume() * .5F, soundType.getPitch() * .75F);
    }

    public static boolean isSameSound(SoundType soundTypeOne, SoundType soundTypeTwo, SoundEventType eventType) {
        if (soundTypeOne == soundTypeTwo) {
            return true;
        }

        SoundEvent soundOne = eventType.resolve(soundTypeOne);
        SoundEvent soundTwo = eventType.resolve(soundTypeTwo);
        if (soundOne == soundTwo) {
            return true;
        }

        SoundEventGroup groupOne = SoundEventGroup.getGroup(soundOne);
        if (groupOne == null || groupOne.type() != eventType) {
            return false;
        }
        return groupOne.equals(SoundEventGroup.getGroup(soundTwo));
    }

    public static final class Client {
        public static void playHitSound(SoundManager soundManager, BlockPos pos, SoundType soundType) {
            soundManager.play(new SimpleSoundInstance(
                    soundType.getHitSound(),
                    SoundSource.BLOCKS,
                    (soundType.getVolume() + 1F) / 8F,
                    soundType.getPitch() * .5F,
                    SoundInstance.createUnseededRandom(),
                    pos
            ));
        }

        public static void playBreakSound(Level level, BlockPos pos, SoundType soundType) {
            SoundEvent sound = soundType.getBreakSound();
            level.playLocalSound(pos, sound, SoundSource.BLOCKS, (soundType.getVolume() + 1F) / 2F, soundType.getPitch() * .8F, false);
        }

        private Client() { }
    }

    private SoundUtils() { }
}
