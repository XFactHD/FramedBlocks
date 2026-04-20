package io.github.xfacthd.framedblocks.api.model;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public interface CachingModel {
    Set<CachingModel> USED_CACHING_MODELS = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * Override this method to implement clearing of the cache on a given model.
     */
    default void clearCache() {}

    /**
     * Must be called when the model populates its cache for the first time to ensure it invalidates on world
     * exit.
     */
    default void markCacheDirty() {
        synchronized (USED_CACHING_MODELS) {
            USED_CACHING_MODELS.add(this);
        }
    }

    static void clearAllCaches() {
        synchronized (USED_CACHING_MODELS) {
            USED_CACHING_MODELS.forEach(CachingModel::clearCache);
            USED_CACHING_MODELS.clear();
        }
    }
}
