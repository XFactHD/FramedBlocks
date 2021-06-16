package xfacthd.framedblocks.common.data;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum CornerType implements IStringSerializable
{
    BOTTOM,
    TOP,
    HORIZONTAL_BOTTOM_LEFT,
    HORIZONTAL_BOTTOM_RIGHT,
    HORIZONTAL_TOP_LEFT,
    HORIZONTAL_TOP_RIGHT;

    private final String name = toString().toLowerCase(Locale.ENGLISH);

    @Override
    public String getString() { return name; }

    public boolean isHorizontal() { return this != BOTTOM && this != TOP; }

    public boolean isTop() { return this == TOP || this == HORIZONTAL_TOP_LEFT || this == HORIZONTAL_TOP_RIGHT; }

    public boolean isRight() { return this == HORIZONTAL_BOTTOM_RIGHT || this == HORIZONTAL_TOP_RIGHT; }

    @SuppressWarnings("DuplicatedCode") //Switch over enum cannot be deduplicated
    public boolean isHorizontalAdjacent(Direction dir, Direction side, CornerType adjType)
    {
        if (!isHorizontal() || !adjType.isHorizontal()) { return false; }

        switch (this)
        {
            case HORIZONTAL_TOP_LEFT:
            {
                if (side == Direction.UP) { return adjType == HORIZONTAL_BOTTOM_LEFT; }
                if (side == dir.rotateYCCW()) { return adjType == HORIZONTAL_TOP_RIGHT; }
                return false;
            }
            case HORIZONTAL_TOP_RIGHT:
            {
                if (side == Direction.UP) { return adjType == HORIZONTAL_BOTTOM_RIGHT; }
                if (side == dir.rotateY()) { return adjType == HORIZONTAL_TOP_LEFT; }
                return false;
            }
            case HORIZONTAL_BOTTOM_LEFT:
            {
                if (side == Direction.DOWN) { return adjType == HORIZONTAL_TOP_LEFT; }
                if (side == dir.rotateYCCW()) { return adjType == HORIZONTAL_BOTTOM_RIGHT; }
                return false;
            }
            case HORIZONTAL_BOTTOM_RIGHT:
            {
                if (side == Direction.DOWN) { return adjType == HORIZONTAL_TOP_RIGHT; }
                if (side == dir.rotateY()) { return adjType == HORIZONTAL_BOTTOM_LEFT; }
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings("DuplicatedCode")
    public boolean isHorizontalAdjacentInner(Direction dir, Direction side, CornerType adjType)
    {
        if (!isHorizontal() || !adjType.isHorizontal()) { return false; }

        switch (this)
        {
            case HORIZONTAL_BOTTOM_RIGHT:
            {
                if (side == Direction.UP) { return adjType == HORIZONTAL_TOP_RIGHT; }
                if (side == dir.rotateYCCW()) { return adjType == HORIZONTAL_BOTTOM_LEFT; }
                return false;
            }
            case HORIZONTAL_BOTTOM_LEFT:
            {
                if (side == Direction.UP) { return adjType == HORIZONTAL_TOP_LEFT; }
                if (side == dir.rotateY()) { return adjType == HORIZONTAL_BOTTOM_RIGHT; }
                return false;
            }
            case HORIZONTAL_TOP_RIGHT:
            {
                if (side == Direction.DOWN) { return adjType == HORIZONTAL_BOTTOM_RIGHT; }
                if (side == dir.rotateYCCW()) { return adjType == HORIZONTAL_TOP_LEFT; }
                return false;
            }
            case HORIZONTAL_TOP_LEFT:
            {
                if (side == Direction.DOWN) { return adjType == HORIZONTAL_BOTTOM_LEFT; }
                if (side == dir.rotateY()) { return adjType == HORIZONTAL_TOP_RIGHT; }
                return false;
            }
        }
        return false;
    }
}