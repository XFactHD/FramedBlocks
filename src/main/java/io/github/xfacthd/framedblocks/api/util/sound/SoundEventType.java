package io.github.xfacthd.framedblocks.api.util.sound;

import com.mojang.serialization.Codec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.SoundType;

import java.util.Locale;
import java.util.function.Function;

public enum SoundEventType implements StringRepresentable
{
    BREAK(SoundType::getBreakSound),
    STEP(SoundType::getStepSound),
    PLACE(SoundType::getPlaceSound),
    HIT(SoundType::getHitSound),
    FALL(SoundType::getFallSound),
    ;

    public static final Codec<SoundEventType> CODEC = StringRepresentable.fromEnum(SoundEventType::values);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Function<SoundType, SoundEvent> eventResolver;

    SoundEventType(Function<SoundType, SoundEvent> eventResolver)
    {
        this.eventResolver = eventResolver;
    }

    public SoundEvent resolve(SoundType type)
    {
        return eventResolver.apply(type);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}
