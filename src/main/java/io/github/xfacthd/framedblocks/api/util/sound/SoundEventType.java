package io.github.xfacthd.framedblocks.api.util.sound;

import com.mojang.serialization.Codec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.SoundType;

import java.util.Locale;
import java.util.function.Function;

/// Represents the different types of sound events which can be played for a block.
public enum SoundEventType implements StringRepresentable {
    /// The sound played when a block is broken.
    BREAK(SoundType::getBreakSound),
    /// The sound played when an entity walks over a block.
    STEP(SoundType::getStepSound),
    /// The sound played when a block is placed.
    PLACE(SoundType::getPlaceSound),
    /// The sound played when a block is punched.
    HIT(SoundType::getHitSound),
    /// The sound played when an entity falls onto a block.
    FALL(SoundType::getFallSound),
    ;

    public static final Codec<SoundEventType> CODEC = StringRepresentable.fromEnum(SoundEventType::values);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Function<SoundType, SoundEvent> eventResolver;

    SoundEventType(Function<SoundType, SoundEvent> eventResolver) {
        this.eventResolver = eventResolver;
    }

    /// {@return the sound event of this event type from the given sound type}
    ///
    /// @param type The sound type to resolve the event from
    public SoundEvent resolve(SoundType type) {
        return eventResolver.apply(type);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
