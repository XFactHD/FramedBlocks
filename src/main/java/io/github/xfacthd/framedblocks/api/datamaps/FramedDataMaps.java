package io.github.xfacthd.framedblocks.api.datamaps;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface FramedDataMaps {
    FramedDataMaps INSTANCE = Utils.loadService(FramedDataMaps.class);

    DataMapType<Block, BlockCamoRotatorPrototype> blockCamoRotators();

    DataMapType<SoundEvent, SoundEventGroup> soundEventGroups();
}
