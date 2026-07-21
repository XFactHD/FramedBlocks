package io.github.xfacthd.framedblocks.api.model;

import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;

/// Super-interface for models or model-like data structures which hold cached geometry.
public interface CachingModel {
    /// Override this method to implement clearing of the cache on a given model.
    ///
    /// Implementations of this method must not trigger [registration][#register(CachingModel)] of a model for cleanup.
    void clearCache();

    /// Register the provided model for cache cleanup.
    ///
    /// Models registered through this method will not have [#clearCache()] called on
    /// them after a resource reload.
    /// This method must only be used for models which are discarded during a
    /// resource reload.
    ///
    /// @param model The model to register
    static void register(CachingModel model) {
        InternalClientAPI.INSTANCE.registerLoadedCachingModel(model);
    }

    /// Register the provided model for cache cleanup.
    ///
    /// Models registered through this method will have [#clearCache()] called on
    /// them after a resource reload.
    /// This method must only be used for models which persist through a resource
    /// reload.
    ///
    /// @param model The model to register
    static void registerPersistent(CachingModel model) {
        InternalClientAPI.INSTANCE.registerPersistentCachingModel(model);
    }
}
