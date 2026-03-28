package io.github.xfacthd.framedblocks.api.internal.duck;

import io.github.xfacthd.framedblocks.api.block.cache.StateCache;

public interface StateCacheAccessor {
    default void framedblocks$initCache(StateCache cache) {
        throw new AssertionError();
    }

    default StateCache framedblocks$getCache() {
        throw new AssertionError();
    }
}
