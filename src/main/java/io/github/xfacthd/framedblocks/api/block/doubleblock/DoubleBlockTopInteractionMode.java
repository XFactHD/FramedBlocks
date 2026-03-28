package io.github.xfacthd.framedblocks.api.block.doubleblock;

/**
 * Declares which camo or camos an interaction with the top face of a double block should take into account.
 * Used for sounds and friction
 */
public enum DoubleBlockTopInteractionMode {
    FIRST(true, false),
    SECOND(false, true),
    EITHER(true, true);

    private final boolean applyFirst;
    private final boolean applySecond;

    DoubleBlockTopInteractionMode(boolean applyFirst, boolean applySecond) {
        this.applyFirst = applyFirst;
        this.applySecond = applySecond;
    }

    public boolean applyFirst() {
        return applyFirst;
    }

    public boolean applySecond() {
        return applySecond;
    }
}
