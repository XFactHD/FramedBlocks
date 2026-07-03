package io.github.xfacthd.framedblocks.api.datamaps;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.ApiStatus;

/// Provides access to the datamap types used by FramedBlocks.
@ApiStatus.NonExtendable
public interface FramedDataMaps {
    FramedDataMaps INSTANCE = Utils.loadService(FramedDataMaps.class);

    /// {@return the datamap type for attaching camo rotators to blocks}
    DataMapType<Block, BlockCamoRotatorPrototype> blockCamoRotators();

    /// {@return the datamap type for grouping sound events}
    DataMapType<SoundEvent, SoundEventGroup> soundEventGroups();
}
