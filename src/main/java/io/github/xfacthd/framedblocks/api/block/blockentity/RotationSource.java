package io.github.xfacthd.framedblocks.api.block.blockentity;

/// Indicates what triggered the rotation of a framed block.
public enum RotationSource {
    /// Block was rotated by a structure being placed.
    STRUCTURE,
    /// Block was rotated by a wrench.
    WRENCH,
}
