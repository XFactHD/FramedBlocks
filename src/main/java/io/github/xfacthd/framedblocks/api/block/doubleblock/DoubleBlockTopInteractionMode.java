package io.github.xfacthd.framedblocks.api.block.doubleblock;

/// Declares which camo or camos an interaction with the top face of a double block should take into account.
/// Used primarily for sounds, particles and friction.
public enum DoubleBlockTopInteractionMode {
    /// Indicates that only the first camo is relevant for top-face interactions.
    FIRST(true, false),
    /// Indicates that only the second camo is relevant for top-face interactions.
    SECOND(false, true),
    /// Indicates that both camos are relevant for top-face interactions.
    BOTH(true, true);

    private final boolean applyFirst;
    private final boolean applySecond;

    DoubleBlockTopInteractionMode(boolean applyFirst, boolean applySecond) {
        this.applyFirst = applyFirst;
        this.applySecond = applySecond;
    }

    /// {@return whether interactions with the block's top should take the first camo into account}
    public boolean applyFirst() {
        return applyFirst;
    }

    /// {@return whether interactions with the block's top should take the second camo into account}
    public boolean applySecond() {
        return applySecond;
    }
}
