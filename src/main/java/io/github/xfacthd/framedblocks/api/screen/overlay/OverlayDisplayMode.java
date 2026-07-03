package io.github.xfacthd.framedblocks.api.screen.overlay;

/// Indicates how verbose a given block interact overlay should be displayed when its preconditions succeed.
public enum OverlayDisplayMode {
    /// The overlay is never shown.
    HIDDEN,
    /// Only the icon next to the crosshair is shown.
    ICON,
    /// The icon next to the crosshair is always shown, the detailed description
    /// is only shown when the player is crouching.
    DETAILED_TOGGLE,
    /// The icon next to the crosshair and the detailed description are always shown.
    DETAILED_ALWAYS;

    /// {@return the given mode or this, whichever is lower}
    ///
    /// @param other The mode to constrain
    public OverlayDisplayMode constrain(OverlayDisplayMode other) {
        return ordinal() < other.ordinal() ? this : other;
    }
}
