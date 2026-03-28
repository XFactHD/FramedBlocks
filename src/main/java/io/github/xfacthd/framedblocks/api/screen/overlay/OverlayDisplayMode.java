package io.github.xfacthd.framedblocks.api.screen.overlay;

public enum OverlayDisplayMode {
    HIDDEN,
    ICON,
    DETAILED_TOGGLE,
    DETAILED_ALWAYS;

    public OverlayDisplayMode constrain(OverlayDisplayMode other) {
        return ordinal() < other.ordinal() ? this : other;
    }
}
