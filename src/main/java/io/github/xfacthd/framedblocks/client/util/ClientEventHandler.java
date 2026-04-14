package io.github.xfacthd.framedblocks.client.util;

import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.data.dynreg.BlockOverlayCache;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.util.Objects;

public final class ClientEventHandler {
    public static void onClientConnect(@SuppressWarnings("unused") ClientPlayerNetworkEvent.LoggingIn event) {
        Level level = Objects.requireNonNull(Minecraft.getInstance().level);
        BlockOverlayCache.get(true).update(level.registryAccess());
    }

    public static void onClientDisconnect(@SuppressWarnings("unused") ClientPlayerNetworkEvent.LoggingOut event) {
        FramingSawRecipeCache.get(true).clear();
        BlockOverlayCache.get(true).clear();
        CacheCleaner.clearModelCaches(CacheCleaner.Reason.DISCONNECT);
    }

    private ClientEventHandler() { }
}
