package xfacthd.framedblocks.api.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public final class FramedProperties
{
    public static final DirectionProperty FACING_HOR = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public static final DirectionProperty FACING_NE = DirectionProperty.create("facing", dir -> dir == Direction.NORTH || dir == Direction.EAST);

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty OFFSET = BooleanProperty.create("offset");
    public static final BooleanProperty X_AXIS = BooleanProperty.create("x_axis");
    public static final BooleanProperty Y_AXIS = BooleanProperty.create("y_asix");
    public static final BooleanProperty Z_AXIS = BooleanProperty.create("z_axis");
    public static final BooleanProperty SOLID = BooleanProperty.create("solid");
    public static final BooleanProperty GLOWING = BooleanProperty.create("glowing");
    public static final BooleanProperty STATE_LOCKED = BooleanProperty.create("locked");



    private FramedProperties() { }
}