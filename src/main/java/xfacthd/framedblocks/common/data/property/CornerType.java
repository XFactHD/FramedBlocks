package xfacthd.framedblocks.common.data.property;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

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