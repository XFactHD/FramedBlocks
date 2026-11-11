package io.github.xfacthd.framedblocks.client.model.baked;

import net.minecraft.util.TriState;

/// Defines the shading behavior to use depending on the source model part's AO value.
record ShadingMode(TriState defaultAO, boolean applyFullbright, boolean overrideShadeDirection) {
    static final ShadingMode FORCED_EMISSIVE = new ShadingMode(TriState.FALSE, true, true);
    static final ShadingMode EMISSIVE_CAMO = new ShadingMode(TriState.FALSE, true, false);
    static final ShadingMode GLOWING_CAMO = new ShadingMode(TriState.FALSE, false, false);
    static final ShadingMode DEFAULT = new ShadingMode(TriState.TRUE, false, false);

    /// {@return the AO config adjusted by this default AO}
    TriState computeAmbientOcclusion(TriState partAO) {
        if (overrideShadeDirection || partAO == TriState.DEFAULT) {
            return defaultAO;
        }
        return partAO;
    }
}
