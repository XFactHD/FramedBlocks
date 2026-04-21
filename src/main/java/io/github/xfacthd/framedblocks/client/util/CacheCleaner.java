package io.github.xfacthd.framedblocks.client.util;

import io.github.xfacthd.framedblocks.api.model.CachingModel;
import io.github.xfacthd.framedblocks.client.model.FluidCubeModel;
import io.github.xfacthd.framedblocks.client.model.RuntimeMaterialBaker;
import io.github.xfacthd.framedblocks.client.model.overlaygen.BlockOverlayGenerator;
import io.github.xfacthd.framedblocks.client.model.overlaygen.OverlayQuadGenerator;
import io.github.xfacthd.framedblocks.client.render.special.ModelBasedOutlineRenderer;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class CacheCleaner {
    private static final Set<CachingModel> USED_CACHING_MODELS = Collections.newSetFromMap(new WeakHashMap<>());

    public static void registerLoadedCachingModel(CachingModel model) {
        synchronized (USED_CACHING_MODELS) {
            USED_CACHING_MODELS.add(model);
        }
    }

    public static void clearModelCaches(Reason reason) {
        synchronized (USED_CACHING_MODELS) {
            USED_CACHING_MODELS.forEach(CachingModel::clearCache);
        }

        clearExternalGeometryCaches(reason);
    }

    public static void clearExternalGeometryCaches(Reason reason) {
        FluidCubeModel.clearCaches();
        RuntimeMaterialBaker.clear(reason);
        ModelBasedOutlineRenderer.clearCaches();
        OverlayQuadGenerator.clearCaches();
        BlockOverlayGenerator.clearCaches(reason);
    }

    public enum Reason {
        RELOAD,
        DISCONNECT,
        SETTINGS_CHANGED,
        MANUAL,
    }

    private CacheCleaner() { }
}
