package io.github.xfacthd.framedblocks.api.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class FramedProperties
{
    public static final EnumProperty<Direction> FACING_HOR = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Direction> FACING_NE = EnumProperty.create("facing", Direction.class, dir -> dir == Direction.NORTH || dir == Direction.EAST);

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty OFFSET = BooleanProperty.create("offset");
    public static final BooleanProperty X_AXIS = BooleanProperty.create("x_axis");
    public static final BooleanProperty Y_AXIS = BooleanProperty.create("y_asix");
    public static final BooleanProperty Z_AXIS = BooleanProperty.create("z_axis");
    public static final BooleanProperty SOLID = BooleanProperty.create("solid");
    public static final BooleanProperty PROPAGATES_SKYLIGHT = BooleanProperty.create("propagates_skylight");
    public static final BooleanProperty GLOWING = BooleanProperty.create("glowing");
    public static final BooleanProperty STATE_LOCKED = BooleanProperty.create("locked");
    public static final BooleanProperty ALT_SLOPE = BooleanProperty.create("alt_slope");
    public static final BooleanProperty COPYCAT_STYLE = BooleanProperty.create("copycat_style");



    private FramedProperties() { }
}