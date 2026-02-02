package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.util.sound.SoundEventType;
import io.github.xfacthd.framedblocks.common.data.FramedDataMaps;
import io.github.xfacthd.framedblocks.common.data.datamaps.SoundEventGroup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public final class FramedDataMapProvider extends DataMapProvider
{
    public FramedDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries);
    }

    @Override
    protected void gather(HolderLookup.Provider provider)
    {
        SoundEventGroup stoneLikeStep = new SoundEventGroup(SoundEventType.STEP, "stone_like");
        SoundEventGroup stoneLikePlace = new SoundEventGroup(SoundEventType.PLACE, "stone_like");
        SoundEventGroup stoneLikeHit = new SoundEventGroup(SoundEventType.HIT, "stone_like");
        SoundEventGroup stoneLikeFall = new SoundEventGroup(SoundEventType.FALL, "stone_like");

        builder(FramedDataMaps.SOUND_EVENT_GROUPS)
                .add(key(SoundEvents.STONE_STEP), stoneLikeStep, false)
                .add(key(SoundEvents.STONE_PLACE), stoneLikePlace, false)
                .add(key(SoundEvents.STONE_HIT), stoneLikeHit, false)
                .add(key(SoundEvents.STONE_FALL), stoneLikeFall, false)
                .add(key(SoundEvents.GLASS_STEP), stoneLikeStep, false)
                .add(key(SoundEvents.GLASS_PLACE), stoneLikePlace, false)
                .add(key(SoundEvents.GLASS_HIT), stoneLikeHit, false)
                .add(key(SoundEvents.GLASS_FALL), stoneLikeFall, false);
    }

    private static ResourceKey<SoundEvent> key(SoundEvent event)
    {
        return BuiltInRegistries.SOUND_EVENT.getResourceKey(event).orElseThrow();
    }
}
