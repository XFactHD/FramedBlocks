package io.github.xfacthd.framedblocks.api.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.util.sound.SoundEventType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import org.jspecify.annotations.Nullable;

/// Describes a group of sound events of a particular type. Primarily intended for grouping different sound events
/// which play the same actual sound to avoid playing this sound twice when hitting or breaking double blocks.
///
/// @param type  The type of sound event in the context of a block
/// @param group The name of the group
public record SoundEventGroup(SoundEventType type, String group) {
    public static final Codec<SoundEventGroup> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SoundEventType.CODEC.fieldOf("type").forGetter(SoundEventGroup::type),
            Codec.STRING.fieldOf("group").forGetter(SoundEventGroup::group)
    ).apply(inst, SoundEventGroup::new));

    /// {@return the group of the given sound event or null if the event is not in a group}
    ///
    /// @param event The sound event to get the group for
    public static @Nullable SoundEventGroup getGroup(SoundEvent event) {
        return BuiltInRegistries.SOUND_EVENT.wrapAsHolder(event).getData(FramedDataMaps.INSTANCE.soundEventGroups());
    }
}
