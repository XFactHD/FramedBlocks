package io.github.xfacthd.framedblocks.common.data.skippreds;

public enum TubeOpening {
    NONE,
    THIN,
    THICK;

    public boolean isEqualTo(TubeOpening other) {
        return this != NONE && this == other;
    }
}
