package xfacthd.framedblocks.common.data;

import net.minecraft.state.*;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;

public class PropertyHolder
{
    public static final DirectionProperty FACING_HOR = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public static final DirectionProperty FACING_NE = DirectionProperty.create("facing", dir -> dir == Direction.NORTH || dir == Direction.EAST);
    public static final DirectionProperty ORIENTATION = DirectionProperty.create("orientation", Direction.values());
    public static final EnumProperty<SlopeType> SLOPE_TYPE = EnumProperty.create("type", SlopeType.class);
    public static final EnumProperty<CornerType> CORNER_TYPE = EnumProperty.create("type", CornerType.class);
    public static final EnumProperty<StairsType> STAIRS_TYPE = EnumProperty.create("type", StairsType.class);
    public static final EnumProperty<ChestState> CHEST_STATE = EnumProperty.create("state", ChestState.class);
    public static final EnumProperty<RailShape> ASCENDING_RAIL_SHAPE = EnumProperty.create("shape", RailShape.class, RailShape::isAscending);
    public static final EnumProperty<CollapseFace> COLLAPSED_FACE = EnumProperty.create("face", CollapseFace.class);
    public static final EnumProperty<LatchType> LATCH_TYPE = EnumProperty.create("latch", LatchType.class);

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty OFFSET = BooleanProperty.create("offset");
    public static final BooleanProperty X_AXIS = BooleanProperty.create("x_axis");
    public static final BooleanProperty Y_AXIS = BooleanProperty.create("y_asix");
    public static final BooleanProperty Z_AXIS = BooleanProperty.create("z_axis");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final BooleanProperty SOLID = BooleanProperty.create("solid");
    public static final BooleanProperty TOP_HALF = BooleanProperty.create("top_half");
    public static final BooleanProperty GLOWING = BooleanProperty.create("glowing");
}