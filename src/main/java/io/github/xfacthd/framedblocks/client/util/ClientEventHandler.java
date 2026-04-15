package io.github.xfacthd.framedblocks.client.util;

import io.github.xfacthd.framedblocks.api.render.fakelevel.ColorResolvingLevel;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.data.dynreg.BlockOverlayCache;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

public final class ClientEventHandler {
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        if (event.getConnection() == null) {
            // For some reason, vanilla performs a disconnect before joining an SP world or a server or closing the game
            return;
        }

        FramingSawRecipeCache.get(true).clear();
        BlockOverlayCache.get(true).clear();
        CacheCleaner.clearModelCaches(CacheCleaner.Reason.DISCONNECT);
        ColorResolvingLevel.clearBiomeReference();
    }

    private ClientEventHandler() { }
}
