package xfacthd.framedblocks.common.data.property;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;

import java.util.Locale;

public enum CornerType implements StringRepresentable
{
    BOTTOM,
    TOP,
    HORIZONTAL_BOTTOM_LEFT,
    HORIZONTAL_BOTTOM_RIGHT,
    HORIZONTAL_TOP_LEFT,
    HORIZONTAL_TOP_RIGHT;

    private final String name = toString().toLowerCase(Locale.ENGLISH);

    @Override
    public String getSerializedName() { return name; }

    public boolean isHorizontal() { return this != BOTTOM && this != TOP; }

    public boolean isTop() { return this == TOP || this == HORIZONTAL_TOP_LEFT || this == HORIZONTAL_TOP_RIGHT; }

    public boolean isRight() { return this == HORIZONTAL_BOTTOM_RIGHT || this == HORIZONTAL_TOP_RIGHT; }

    public CornerType verticalOpposite()
    {
        return switch (this)
        {
            case TOP -> BOTTOM;
            case BOTTOM -> TOP;
            case HORIZONTAL_BOTTOM_RIGHT -> HORIZONTAL_TOP_RIGHT;
            case HORIZONTAL_BOTTOM_LEFT -> HORIZONTAL_TOP_LEFT;
            case HORIZONTAL_TOP_RIGHT -> HORIZONTAL_BOTTOM_RIGHT;
            case HORIZONTAL_TOP_LEFT -> HORIZONTAL_BOTTOM_LEFT;
        };
    }

    public CornerType horizontalOpposite()
    {
        return switch (this)
        {
            case TOP, BOTTOM -> this;
            case HORIZONTAL_BOTTOM_RIGHT -> HORIZONTAL_BOTTOM_LEFT;
            case HORIZONTAL_BOTTOM_LEFT -> HORIZONTAL_BOTTOM_RIGHT;
            case HORIZONTAL_TOP_RIGHT -> HORIZONTAL_TOP_LEFT;
            case HORIZONTAL_TOP_LEFT -> HORIZONTAL_TOP_RIGHT;
        };
    }

    public CornerType rotate(Rotation rot)
    {
        return switch (this)
        {
            case HORIZONTAL_TOP_LEFT -> switch (rot)
            {
                case NONE -> this;
                case CLOCKWISE_90 -> HORIZONTAL_TOP_RIGHT;
                case CLOCKWISE_180 -> HORIZONTAL_BOTTOM_RIGHT;
                case COUNTERCLOCKWISE_90 -> HORIZONTAL_BOTTOM_LEFT;
            };
            case HORIZONTAL_TOP_RIGHT -> switch (rot)
            {
                case NONE -> this;
                case CLOCKWISE_90 -> HORIZONTAL_BOTTOM_RIGHT;
                case CLOCKWISE_180 -> HORIZONTAL_BOTTOM_LEFT;
                case COUNTERCLOCKWISE_90 -> HORIZONTAL_TOP_LEFT;
            };
            case HORIZONTAL_BOTTOM_LEFT -> switch (rot)
            {
                case NONE -> this;
                case CLOCKWISE_90 -> HORIZONTAL_TOP_LEFT;
                case CLOCKWISE_180 -> HORIZONTAL_TOP_RIGHT;
                case COUNTERCLOCKWISE_90 -> HORIZONTAL_BOTTOM_RIGHT;
            };
            case HORIZONTAL_BOTTOM_RIGHT -> switch (rot)
            {
                case NONE -> this;
                case CLOCKWISE_90 -> HORIZONTAL_BOTTOM_LEFT;
                case CLOCKWISE_180 -> HORIZONTAL_TOP_LEFT;
                case COUNTERCLOCKWISE_90 -> HORIZONTAL_TOP_RIGHT;
            };
            case TOP, BOTTOM -> throw new IllegalStateException("Non-horizontal CornerTypes cannot be rotated");
        };
    }

    public boolean isHorizontalAdjacent(Direction dir, Direction side, CornerType adjType)
    {
        if (!isHorizontal() || !adjType.isHorizontal()) { return false; }

        switch (this)
        {
            case HORIZONTAL_TOP_LEFT ->
            {
                if (side == Direction.UP) { return adjType == HORIZONTAL_BOTTOM_LEFT; }
                if (side == dir.getCounterClockWise()) { return adjType == HORIZONTAL_TOP_RIGHT; }
                return false;
            }
            case HORIZONTAL_TOP_RIGHT ->
            {
                if (side == Direction.UP) { return adjType == HORIZONTAL_BOTTOM_RIGHT; }
                if (side == dir.getClockWise()) { return adjType == HORIZONTAL_TOP_LEFT; }
                return false;
            }
            case HORIZONTAL_BOTTOM_LEFT ->
            {
                if (side == Direction.DOWN) { return adjType == HORIZONTAL_TOP_LEFT; }
                if (side == dir.getCounterClockWise()) { return adjType == HORIZONTAL_BOTTOM_RIGHT; }
                return false;
            }
            case HORIZONTAL_BOTTOM_RIGHT ->
            {
                if (side == Direction.DOWN) { return adjType == HORIZONTAL_TOP_RIGHT; }
                if (side == dir.getClockWise()) { return adjType == HORIZONTAL_BOTTOM_LEFT; }
                return false;
            }
        }
        return false;
    }

    public boolean isHorizontalAdjacentInner(Direction dir, Direction side, CornerType adjType)
    {
        if (!isHorizontal() || !adjType.isHorizontal()) { return false; }

        switch (this)
        {
            case HORIZONTAL_BOTTOM_RIGHT ->
            {
                if (side == Direction.UP) { return adjType == HORIZONTAL_TOP_RIGHT; }
                if (side == dir.getCounterClockWise()) { return adjType == HORIZONTAL_BOTTOM_LEFT; }
                return false;
            }
            case HORIZONTAL_BOTTOM_LEFT ->
            {
                if (side == Direction.UP) { return adjType == HORIZONTAL_TOP_LEFT; }
                if (side == dir.getClockWise()) { return adjType == HORIZONTAL_BOTTOM_RIGHT; }
                return false;
            }
            case HORIZONTAL_TOP_RIGHT ->
            {
                if (side == Direction.DOWN) { return adjType == HORIZONTAL_BOTTOM_RIGHT; }
                if (side == dir.getCounterClockWise()) { return adjType == HORIZONTAL_TOP_LEFT; }
                return false;
            }
            case HORIZONTAL_TOP_LEFT ->
            {
                if (side == Direction.DOWN) { return adjType == HORIZONTAL_BOTTOM_LEFT; }
                if (side == dir.getClockWise()) { return adjType == HORIZONTAL_TOP_RIGHT; }
                return false;
            }
        }
        return false;
    }
}