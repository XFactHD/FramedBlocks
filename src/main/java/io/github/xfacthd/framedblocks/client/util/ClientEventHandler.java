package io.github.xfacthd.framedblocks.client.util;

import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.data.dynreg.BlockOverlayCache;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

public final class ClientEventHandler {
    public static void onClientDisconnect(@SuppressWarnings("unused") ClientPlayerNetworkEvent.LoggingOut event) {
        FramingSawRecipeCache.get(true).clear();
        BlockOverlayCache.get(true).clear();
        CacheCleaner.clearModelCaches(CacheCleaner.Reason.DISCONNECT);
    }

    private ClientEventHandler() { }
}
