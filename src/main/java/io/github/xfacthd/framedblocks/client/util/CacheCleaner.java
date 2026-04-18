package io.github.xfacthd.framedblocks.client.util;

import io.github.xfacthd.framedblocks.api.model.CachingFramedModel;
import io.github.xfacthd.framedblocks.client.model.FluidCubeModel;
import io.github.xfacthd.framedblocks.client.model.RuntimeMaterialBaker;
import io.github.xfacthd.framedblocks.client.model.overlaygen.BlockOverlayGenerator;
import io.github.xfacthd.framedblocks.client.model.overlaygen.OverlayQuadGenerator;
import io.github.xfacthd.framedblocks.client.render.special.ModelBasedOutlineRenderer;

public final class CacheCleaner {
    public static void clearModelCaches(Reason reason) {
        CachingFramedModel.clearAllCaches();

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
