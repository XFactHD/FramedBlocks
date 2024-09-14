package xfacthd.framedblocks.common.data.property;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum StairsType implements StringRepresentable
{
    VERTICAL,
    TOP_FWD,
    TOP_CCW,
    TOP_BOTH,
    BOTTOM_FWD,
    BOTTOM_CCW,
    BOTTOM_BOTH;

    private final String name = toString().toLowerCase(Locale.ENGLISH);

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public boolean isTop()
    {
        return this == TOP_FWD || this == TOP_CCW || this == TOP_BOTH;
    }

    public boolean isBottom()
    {
        return this == BOTTOM_FWD || this == BOTTOM_CCW || this == BOTTOM_BOTH;
    }

    public boolean isForward()
    {
        return this == TOP_FWD || this == BOTTOM_FWD || this == TOP_BOTH || this == BOTTOM_BOTH;
    }

    public boolean isCounterClockwise()
    {
        return this == TOP_CCW || this == BOTTOM_CCW || this == TOP_BOTH || this == BOTTOM_BOTH;
    }



    public static StairsType get(boolean top, boolean fwd, boolean ccw)
    {
        if (fwd && ccw)
        {
            return top ? TOP_BOTH : BOTTOM_BOTH;
        }
        if (fwd)
        {
            return top ? TOP_FWD : BOTTOM_FWD;
        }
        if (ccw)
        {
            return top ? TOP_CCW : BOTTOM_CCW;
        }
        return VERTICAL;
    }
}
