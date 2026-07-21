package io.github.xfacthd.framedblocks.api.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/// Holds various standard blockstate properties.
public final class FramedProperties {
    /// Blocks with this property can be oriented in all four horizontal directions.
    public static final EnumProperty<Direction> FACING_HOR = BlockStateProperties.HORIZONTAL_FACING;

    /// Indicates whether the block occupies the top or bottom half of the block space (currently misused to mean upside-down orientation on some blocks).
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    /// Indicates whether a part of the block oriented along the X axis is present.
    public static final BooleanProperty X_AXIS = BooleanProperty.create("x_axis");
    /// Indicates whether a part of the block oriented along the Y axis is present.
    public static final BooleanProperty Y_AXIS = BooleanProperty.create("y_asix");
    /// Indicates whether a part of the block oriented along the Z axis is present.
    public static final BooleanProperty Z_AXIS = BooleanProperty.create("z_axis");
    /// Indicates whether the block is made fully opaque by the camo(s) applied to it.
    public static final BooleanProperty SOLID = BooleanProperty.create("solid");
    /// Indicates whether the block is made to propagate skylight by the camo(s) applied to it.
    public static final BooleanProperty PROPAGATES_SKYLIGHT = BooleanProperty.create("propagates_skylight");
    /// Indicates whether the block has any kind of light emission, either from a camo or from the glowing modifier.
    public static final BooleanProperty GLOWING = BooleanProperty.create("glowing");
    /// Indicates whether the block has its state locked and ignores neighbor changes.
    /// @see ShapeLockableBlock
    public static final BooleanProperty STATE_LOCKED = BooleanProperty.create("locked");
    /// Indicates whether the block has an alternative slope orientation.
    /// @see SlopeToggleBlock
    public static final BooleanProperty ALT_SLOPE = BooleanProperty.create("alt_slope");
    /// Indicates whether the block's model is uses copycat-style quad cutting (texture edges moved inwards)
    /// or standard quad cutting (texture edges cut off).
    public static final BooleanProperty COPYCAT_STYLE = BooleanProperty.create("copycat_style");

    private FramedProperties() { }
}
