package xfacthd.framedblocks.common.block.slopeslab;

public enum SlopeSlabShape
{
    BOTTOM_BOTTOM_HALF,
    BOTTOM_TOP_HALF,
    TOP_BOTTOM_HALF,
    TOP_TOP_HALF;

    private static final SlopeSlabShape[] VALUES = values();

    public static SlopeSlabShape get(boolean top, boolean topHalf)
    {
        return VALUES[(top ? 0b10 : 0b00) | (topHalf ? 0b01 : 0b00)];
    }
}
