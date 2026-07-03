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

/// Provides various helpers for handling sounds.
public final class SoundUtils {
    /// Play the block place sound of the given sound type.
    ///
    /// @param context    The context in which the block was placed.
    /// @param soundType  The sound type to play
    /// @param serverOnly Whether the sound plays on both sides or only on the server
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

    /// Play the block step sound of the given sound type
    ///
    /// @param entity     The entity walking over the block
    /// @param soundType  The sound type to play
    /// @param volumeMult The multiplier to apply to the sound's volume
    /// @param pitchMult  The multiplier to apply to the sound's pitch
    public static void playStepSound(Entity entity, SoundType soundType, float volumeMult, float pitchMult) {
        entity.playSound(soundType.getStepSound(), soundType.getVolume() * volumeMult, soundType.getPitch() * pitchMult);
    }

    /// Play the block fall sound of the given sound type.
    ///
    /// @param entity    The entity falling onto the block
    /// @param soundType The sound type to play
    public static void playFallSound(Entity entity, SoundType soundType) {
        entity.playSound(soundType.getFallSound(), soundType.getVolume() * .5F, soundType.getPitch() * .75F);
    }

    /// {@return whether the given event type of the two sound types are considered equal by their sound event group}
    ///
    /// @param soundTypeOne The first sound type to compare
    /// @param soundTypeTwo The second sound type to compare
    /// @param eventType    The sound event type to compare them in
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
        /// Play the block hit sound of the given sound type.
        ///
        /// @param soundManager The sound manager to play the sound with
        /// @param pos          The position to play the sound at
        /// @param soundType    The sound type to play
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

        /// Play the block break sound of the givne sound type
        ///
        /// @param level     The level the block was in
        /// @param pos       The position the block was at
        /// @param soundType The sound type to play
        public static void playBreakSound(Level level, BlockPos pos, SoundType soundType) {
            SoundEvent sound = soundType.getBreakSound();
            level.playLocalSound(pos, sound, SoundSource.BLOCKS, (soundType.getVolume() + 1F) / 2F, soundType.getPitch() * .8F, false);
        }

        private Client() { }
    }

    private SoundUtils() { }
}
