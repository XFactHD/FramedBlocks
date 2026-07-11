package io.github.xfacthd.framedblocks.api.model.template;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Quadrant;

/// Builder for transformations applied to templates.
///
/// The resulting [OctahedralGroup] will apply the rotations and mirrors in the order
/// `rotX`, `rotY`, `rotZ`, `mirrorX`, `mirrorY`, `mirrorZ`.
public final class TemplateTransformBuilder {
    private Quadrant rotX = Quadrant.R0;
    private Quadrant rotY = Quadrant.R0;
    private Quadrant rotZ = Quadrant.R0;
    private boolean mirrorX = false;
    private boolean mirrorY = false;
    private boolean mirrorZ = false;

    public TemplateTransformBuilder() { }

    /// Specify a rotation around the X axis.
    ///
    /// @param rotX The rotation to apply
    public TemplateTransformBuilder rotationX(Quadrant rotX) {
        this.rotX = rotX;
        return this;
    }

    /// Specify a rotation around the Y axis.
    ///
    /// @param rotY The rotation to apply
    public TemplateTransformBuilder rotationY(Quadrant rotY) {
        this.rotY = rotY;
        return this;
    }

    /// Specify a rotation around the Z axis.
    ///
    /// @param rotZ The rotation to apply
    public TemplateTransformBuilder rotationZ(Quadrant rotZ) {
        this.rotZ = rotZ;
        return this;
    }

    /// Specify whether the template should be mirrored along the X axis.
    ///
    /// @param mirrorX Whether to mirror along the X axis
    public TemplateTransformBuilder mirrorX(boolean mirrorX) {
        this.mirrorX = mirrorX;
        return this;
    }

    /// Specify whether the template should be mirrored along the Y axis.
    ///
    /// @param mirrorY Whether to mirror along the X axis
    public TemplateTransformBuilder mirrorY(boolean mirrorY) {
        this.mirrorY = mirrorY;
        return this;
    }

    /// Specify whether the template should be mirrored along the Z axis.
    ///
    /// @param mirrorZ Whether to mirror along the X axis
    public TemplateTransformBuilder mirrorZ(boolean mirrorZ) {
        this.mirrorZ = mirrorZ;
        return this;
    }

    /// {@return the {@link OctahedralGroup} representing the specified rotations and mirrors}
    public OctahedralGroup build() {
        OctahedralGroup xform = Quadrant.fromXYZAngles(rotX, rotY, rotZ);
        if (mirrorX) {
            xform = OctahedralGroup.INVERT_X.compose(xform);
        }
        if (mirrorY) {
            xform = OctahedralGroup.INVERT_Y.compose(xform);
        }
        if (mirrorZ) {
            xform = OctahedralGroup.INVERT_Z.compose(xform);
        }
        return xform;
    }
}
