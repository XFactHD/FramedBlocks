package xfacthd.framedblocks.common.data;

import net.minecraft.state.*;
import net.minecraft.util.Direction;

public class PropertyHolder
{
    public static final DirectionProperty FACING_HOR = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public static final DirectionProperty FACING_NE = DirectionProperty.create("facing", dir -> dir == Direction.NORTH || dir == Direction.EAST);
    public static final EnumProperty<SlopeType> SLOPE_TYPE = EnumProperty.create("type", SlopeType.class);
    public static final EnumProperty<CornerType> CORNER_TYPE = EnumProperty.create("type", CornerType.class);

    public static final BooleanProperty TOP = BooleanProperty.create("top");
}