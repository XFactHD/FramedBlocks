package io.github.xfacthd.framedblocks.api.util;

/// Indicates the verbosity of camo application failure messages.
public enum CamoMessageVerbosity {
    /// Camo application failure messages are never displayed.
    NONE,
    /// Only major camo application failure messages are displayed (blocked camo, block with BE).
    DEFAULT,
    /// All camo application failure messages are displayed.
    DETAILED;

    /// {@return whether the given verbosity is higher or equal to this verbosity}
    ///
    /// @param verbosity The verbosity to compare against
    public boolean isAtLeast(CamoMessageVerbosity verbosity) {
        return ordinal() >= verbosity.ordinal();
    }
}
