package io.github.xfacthd.framedblocks.common.data.property;

import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum SlopeType implements StringRepresentable
{
    BOTTOM(SlopeToggleBlock.SlopeOrientation.VERTICAL),
    HORIZONTAL(SlopeToggleBlock.SlopeOrientation.HORIZONTAL),
    TOP(SlopeToggleBlock.SlopeOrientation.VERTICAL);

    private final String name = toString().toLowerCase(Locale.ENGLISH);
    private final SlopeToggleBlock.SlopeOrientation orientation;

    SlopeType(SlopeToggleBlock.SlopeOrientation orientation)
    {
        this.orientation = orientation;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public SlopeType getOpposite()
    {
        return switch (this)
        {
            case TOP -> BOTTOM;
            case BOTTOM -> TOP;
            default -> throw new IllegalArgumentException("Can't get opposite of '" + getSerializedName() + "'!");
        };
    }

    public SlopeToggleBlock.SlopeOrientation getOrientation()
    {
        return orientation;
    }
}
