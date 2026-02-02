package io.github.xfacthd.framedblocks.common.data.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.util.sound.SoundEventType;
import io.github.xfacthd.framedblocks.common.data.FramedDataMaps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import org.jspecify.annotations.Nullable;

public record SoundEventGroup(SoundEventType type, String group)
{
    public static final Codec<SoundEventGroup> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SoundEventType.CODEC.fieldOf("type").forGetter(SoundEventGroup::type),
            Codec.STRING.fieldOf("group").forGetter(SoundEventGroup::group)
    ).apply(inst, SoundEventGroup::new));

    public static boolean isSameSound(SoundType soundTypeOne, SoundType soundTypeTwo, SoundEventType eventType)
    {
        if (soundTypeOne == soundTypeTwo) return true;

        SoundEvent soundOne = eventType.resolve(soundTypeOne);
        SoundEvent soundTwo = eventType.resolve(soundTypeTwo);
        if (soundOne == soundTwo) return true;

        SoundEventGroup groupOne = getGroup(soundOne);
        return groupOne != null && groupOne.type == eventType && groupOne.equals(getGroup(soundTwo));
    }

    @Nullable
    public static SoundEventGroup getGroup(SoundEvent event)
    {
        return BuiltInRegistries.SOUND_EVENT.wrapAsHolder(event).getData(FramedDataMaps.SOUND_EVENT_GROUPS);
    }
}
