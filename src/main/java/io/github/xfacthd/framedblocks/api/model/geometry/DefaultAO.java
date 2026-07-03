package io.github.xfacthd.framedblocks.api.model.geometry;

import net.minecraft.util.TriState;

/// Defines the AO behavior to use depending on the source model part's AO value.
public enum DefaultAO {
    /// Always disable AO, regardless of the source part's AO value.
    FORCE_DISABLE {
        @Override
        public TriState apply(TriState partAO) {
            return TriState.FALSE;
        }
    },
    /// Disable AO unless the source part indicates otherwise.
    DISABLE {
        @Override
        public TriState apply(TriState partAO) {
            return partAO != TriState.DEFAULT ? partAO : TriState.FALSE;
        }
    },
    /// Always use the source part's AO value.
    DEFAULT {
        @Override
        public TriState apply(TriState partAO) {
            return partAO;
        }
    },
    /// Enable AO unless the source part indicates otherwise.
    ENABLE {
        @Override
        public TriState apply(TriState partAO) {
            return partAO != TriState.DEFAULT ? partAO : TriState.TRUE;
        }
    },
    /// Always enable AO, regardless of the source part's AO value.
    FORCE_ENABLE {
        @Override
        public TriState apply(TriState partAO) {
            return TriState.TRUE;
        }
    };

    /// {@return the AO config adjusted by this default AO}
    public abstract TriState apply(TriState partAO);
}
