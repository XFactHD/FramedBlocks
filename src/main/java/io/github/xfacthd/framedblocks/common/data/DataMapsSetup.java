package io.github.xfacthd.framedblocks.common.data;

import io.github.xfacthd.framedblocks.api.datamaps.BlockCamoRotatorPrototype;
import io.github.xfacthd.framedblocks.api.datamaps.FramedDataMaps;
import io.github.xfacthd.framedblocks.api.datamaps.SoundEventGroup;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.camo.block.rotator.BlockCamoRotators;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public final class DataMapsSetup implements FramedDataMaps
{
    public static final DataMapType<Block, BlockCamoRotatorPrototype> BLOCK_CAMO_ROTATORS = DataMapType.builder(
            Utils.id("block_camo_rotators"),
            Registries.BLOCK,
            BlockCamoRotatorPrototype.CODEC
    ).synced(BlockCamoRotatorPrototype.CODEC, true).build();
    public static final DataMapType<SoundEvent, SoundEventGroup> SOUND_EVENT_GROUPS = DataMapType.builder(
            Utils.id("sound_event_groups"),
            Registries.SOUND_EVENT,
            SoundEventGroup.CODEC
    ).synced(SoundEventGroup.CODEC, true).build();

    public static void onRegisterDataMapTypes(RegisterDataMapTypesEvent event)
    {
        event.register(BLOCK_CAMO_ROTATORS);
        event.register(SOUND_EVENT_GROUPS);
    }

    public static void onDataMapsUpdated(DataMapsUpdatedEvent event)
    {
        if (event.getRegistryKey() == Registries.BLOCK)
        {
            // Data maps arrive on the client after default data components are bound
            if (event.getCause() == DataMapsUpdatedEvent.UpdateCause.CLIENT_SYNC)
            {
                BlockCamoRotators.reload();
            }
        }
    }

    @Override
    public DataMapType<Block, BlockCamoRotatorPrototype> blockCamoRotators()
    {
        return BLOCK_CAMO_ROTATORS;
    }

    @Override
    public DataMapType<SoundEvent, SoundEventGroup> soundEventGroups()
    {
        return SOUND_EVENT_GROUPS;
    }
}
