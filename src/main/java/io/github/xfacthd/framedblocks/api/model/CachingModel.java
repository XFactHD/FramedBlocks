package io.github.xfacthd.framedblocks.api.model;

import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;

public interface CachingModel {
    /// Override this method to implement clearing of the cache on a given model.
    ///
    /// Implementations of this method must not trigger [registration][#register(CachingModel)] of a model for cleanup.
    default void clearCache() { }

    /// Register the provided model for cache cleanup
    static void register(CachingModel model) {
        InternalClientAPI.INSTANCE.registerLoadedCachingModel(model);
    }
}
