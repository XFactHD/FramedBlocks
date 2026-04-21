package io.github.xfacthd.framedblocks.api.model;

public interface CachingModel {
    /**
     * Override this method to implement clearing of the cache on a given model.
     */
    default void clearCache() {}
}
