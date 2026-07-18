package io.github.xfacthd.framedblocks.client.util;

import io.github.xfacthd.framedblocks.api.model.CachingModel;
import io.github.xfacthd.framedblocks.client.model.ResourceCubeModel;
import io.github.xfacthd.framedblocks.client.model.RuntimeMaterialBaker;
import io.github.xfacthd.framedblocks.client.model.overlaygen.BlockOverlayGenerator;
import io.github.xfacthd.framedblocks.client.model.overlaygen.OverlayQuadGenerator;
import io.github.xfacthd.framedblocks.client.model.template.GeometryTemplateSpecImpl;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class CacheCleaner {
    private static final Set<CachingModel> LOADED_CACHING_MODELS = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Set<CachingModel> PERSISTENT_CACHING_MODELS = new ReferenceOpenHashSet<>();

    public static void registerLoadedCachingModel(CachingModel model) {
        synchronized (LOADED_CACHING_MODELS) {
            LOADED_CACHING_MODELS.add(model);
        }
    }

    public static void registerPersistentCachingModel(CachingModel model) {
        synchronized (PERSISTENT_CACHING_MODELS) {
            PERSISTENT_CACHING_MODELS.add(model);
        }
    }

    public static void clearModelCaches(Reason reason) {
        synchronized (LOADED_CACHING_MODELS) {
            LOADED_CACHING_MODELS.forEach(CachingModel::clearCache);
        }

        clearExternalGeometryCaches(reason);
    }

    public static void clearExternalGeometryCaches(Reason reason) {
        synchronized (PERSISTENT_CACHING_MODELS) {
            PERSISTENT_CACHING_MODELS.forEach(CachingModel::clearCache);
        }

        ResourceCubeModel.clearInterner();
        RuntimeMaterialBaker.clear(reason);
        OverlayQuadGenerator.clearCaches();
        BlockOverlayGenerator.clearCaches(reason);
        GeometryTemplateSpecImpl.reset(reason);
    }

    public enum Reason {
        RELOAD,
        DISCONNECT,
        SETTINGS_CHANGED,
        MANUAL,
    }

    private CacheCleaner() { }
}
