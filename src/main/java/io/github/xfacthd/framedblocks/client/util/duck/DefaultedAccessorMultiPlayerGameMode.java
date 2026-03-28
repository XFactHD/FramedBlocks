package io.github.xfacthd.framedblocks.client.util.duck;

import io.github.xfacthd.framedblocks.mixin.client.AccessorMultiPlayerGameMode;

@SuppressWarnings("unused") // Used via interface injection
public interface DefaultedAccessorMultiPlayerGameMode extends AccessorMultiPlayerGameMode {
    @Override
    default void framedblocks$setDestroyDelay(int delay) {
        throw new AssertionError();
    }
}
