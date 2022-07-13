package xfacthd.framedblocks.common.data;

import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.common.data.property.*;

public final class PropertyHolder
{
    public static final DirectionProperty ORIENTATION = DirectionProperty.create("orientation");
    public static final EnumProperty<SlopeType> SLOPE_TYPE = EnumProperty.create("type", SlopeType.class);
    public static final EnumProperty<CornerType> CORNER_TYPE = EnumProperty.create("type", CornerType.class);
    public static final EnumProperty<StairsType> STAIRS_TYPE = EnumProperty.create("type", StairsType.class);
    public static final EnumProperty<ChestState> CHEST_STATE = EnumProperty.create("state", ChestState.class);
    public static final EnumProperty<RailShape> ASCENDING_RAIL_SHAPE = EnumProperty.create("shape", RailShape.class, RailShape::isAscending);
    public static final EnumProperty<CollapseFace> COLLAPSED_FACE = EnumProperty.create("face", CollapseFace.class);
    public static final EnumProperty<LatchType> LATCH_TYPE = EnumProperty.create("latch", LatchType.class);
    public static final EnumProperty<HorizontalRotation> ROTATION = EnumProperty.create("rotation", HorizontalRotation.class);

    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final BooleanProperty TOP_HALF = BooleanProperty.create("top_half");
    public static final BooleanProperty FRONT = BooleanProperty.create("front");



    private PropertyHolder() { }
}