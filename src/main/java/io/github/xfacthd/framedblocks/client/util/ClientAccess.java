package io.github.xfacthd.framedblocks.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;

import java.util.Objects;

public final class ClientAccess {
    // For some reason vanilla does not have a constant for this???
    private static final int DEFAULT_DESTROY_DELAY = 5;

    public static void resetDestroyDelay() {
        if (Objects.requireNonNull(Minecraft.getInstance().player).isCreative()) {
            return;
        }

        MultiPlayerGameMode gameMode = Objects.requireNonNull(Minecraft.getInstance().gameMode);
        gameMode.framedblocks$setDestroyDelay(DEFAULT_DESTROY_DELAY);
    }

    private ClientAccess() { }
}
