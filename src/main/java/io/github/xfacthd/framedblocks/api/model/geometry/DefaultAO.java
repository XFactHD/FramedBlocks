package io.github.xfacthd.framedblocks.api.model.geometry;

import net.minecraft.util.TriState;

public enum DefaultAO {
    FORCE_DISABLE {
        @Override
        public TriState apply(TriState partAO) {
            return TriState.FALSE;
        }
    },
    DISABLE {
        @Override
        public TriState apply(TriState partAO) {
            return partAO != TriState.DEFAULT ? partAO : TriState.FALSE;
        }
    },
    DEFAULT {
        @Override
        public TriState apply(TriState partAO) {
            return partAO;
        }
    },
    ENABLE {
        @Override
        public TriState apply(TriState partAO) {
            return partAO != TriState.DEFAULT ? partAO : TriState.TRUE;
        }
    },
    FORCE_ENABLE {
        @Override
        public TriState apply(TriState partAO) {
            return TriState.TRUE;
        }
    };

    public abstract TriState apply(TriState partAO);
}
