package xfacthd.framedblocks.common.data;

import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.common.data.property.*;

public final class PropertyHolder
{
    public static final EnumProperty<SlopeType> SLOPE_TYPE = EnumProperty.create("type", SlopeType.class);
    public static final EnumProperty<CornerType> CORNER_TYPE = EnumProperty.create("type", CornerType.class);
    public static final EnumProperty<StairsType> STAIRS_TYPE = EnumProperty.create("type", StairsType.class);
    public static final EnumProperty<ChestState> CHEST_STATE = EnumProperty.create("state", ChestState.class);
    public static final EnumProperty<RailShape> ASCENDING_RAIL_SHAPE = EnumProperty.create("shape", RailShape.class, RailShape::isAscending);
    public static final EnumProperty<NullableDirection> NULLABLE_FACE = EnumProperty.create("face", NullableDirection.class);
    public static final EnumProperty<LatchType> LATCH_TYPE = EnumProperty.create("latch", LatchType.class);
    public static final EnumProperty<HorizontalRotation> ROTATION = EnumProperty.create("rotation", HorizontalRotation.class);
    public static final EnumProperty<DirectionAxis> FACING_AXIS = EnumProperty.create("facing_axis", DirectionAxis.class);
    public static final EnumProperty<CompoundDirection> FACING_DIR = EnumProperty.create("facing_dir", CompoundDirection.class);

    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final BooleanProperty TOP_HALF = BooleanProperty.create("top_half");
    public static final BooleanProperty FRONT = BooleanProperty.create("front");
    public static final BooleanProperty HANGING = BooleanProperty.create("hanging");
    public static final BooleanProperty LEATHER = BooleanProperty.create("leather");
    public static final BooleanProperty MAP_FRAME = BooleanProperty.create("map_frame");
    public static final BooleanProperty ROTATE_SPLIT_LINE = BooleanProperty.create("rot_split_line");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty SECOND = BooleanProperty.create("second");
    public static final BooleanProperty ALT = BooleanProperty.create("alt");
    public static final BooleanProperty REINFORCED = BooleanProperty.create("reinforced");
    public static final BooleanProperty SOLID_BG = BooleanProperty.create("solid_bg");
    public static final BooleanProperty ALT_TYPE = BooleanProperty.create("alt_type");
    public static final BooleanProperty SAW_ENCODER = BooleanProperty.create("saw_encoder");

    public static final IntegerProperty SOLID_FACES = IntegerProperty.create("solid_faces", 0b00000000, 0b00111111);



    private PropertyHolder() { }
}