package io.github.xfacthd.framedblocks.common.data.dynreg;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class DynamicRegistrySetup
{
    public static void onCreateDatapackRegistries(DataPackRegistryEvent.NewRegistry event)
    {
        event.dataPackRegistry(FramedConstants.BLOCK_OVERLAY_REGISTRY_KEY, BlockOverlay.DIRECT_CODEC, BlockOverlay.DIRECT_CODEC);
    }

    private DynamicRegistrySetup() { }
}
