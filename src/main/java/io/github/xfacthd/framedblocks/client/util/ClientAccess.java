package io.github.xfacthd.framedblocks.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;

import java.util.Objects;

public final class ClientAccess {
    public static void resetDestroyDelay() {
        if (Objects.requireNonNull(Minecraft.getInstance().player).isCreative()) {
            return;
        }

        MultiPlayerGameMode gameMode = Objects.requireNonNull(Minecraft.getInstance().gameMode);
        gameMode.framedblocks$setDestroyDelay(MultiPlayerGameMode.DEFAULT_DESTROY_COOLDOWN_TICKS);
    }

    private ClientAccess() { }
}
