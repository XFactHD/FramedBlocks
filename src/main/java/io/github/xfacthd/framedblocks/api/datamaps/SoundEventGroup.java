package io.github.xfacthd.framedblocks.api.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.util.sound.SoundEventType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import org.jspecify.annotations.Nullable;

public record SoundEventGroup(SoundEventType type, String group) {
    public static final Codec<SoundEventGroup> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SoundEventType.CODEC.fieldOf("type").forGetter(SoundEventGroup::type),
            Codec.STRING.fieldOf("group").forGetter(SoundEventGroup::group)
    ).apply(inst, SoundEventGroup::new));

    public static @Nullable SoundEventGroup getGroup(SoundEvent event) {
        return BuiltInRegistries.SOUND_EVENT.wrapAsHolder(event).getData(FramedDataMaps.INSTANCE.soundEventGroups());
    }
}
