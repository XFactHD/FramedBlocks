package io.github.xfacthd.framedblocks.api.block.item.placement;

import io.github.xfacthd.framedblocks.api.util.Utils;

/// Holds labels for various common blockstate properties
public final class PropertyLabels {
    public static final String FACING = Utils.translationKey("label", "state_cycling.property.facing");
    public static final String ORIENTATION = Utils.translationKey("label", "state_cycling.property.orientation");
    public static final String AXIS = Utils.translationKey("label", "state_cycling.property.axis");
    public static final String ROTATION = Utils.translationKey("label", "state_cycling.property.rotation");
    public static final String HALF = Utils.translationKey("label", "state_cycling.property.half");
    public static final String SHAPE = Utils.translationKey("label", "state_cycling.property.shape");
    public static final String TYPE = Utils.translationKey("label", "state_cycling.property.type");
    public static final String HINGE_SIDE = Utils.translationKey("label", "state_cycling.property.hinge_side");

    private PropertyLabels() { }
}
