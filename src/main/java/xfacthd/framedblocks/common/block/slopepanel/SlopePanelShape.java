package xfacthd.framedblocks.common.block.slopepanel;

import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public enum SlopePanelShape
{
    UP_BACK,
    DOWN_BACK,
    RIGHT_BACK,
    LEFT_BACK,
    UP_FRONT,
    DOWN_FRONT,
    RIGHT_FRONT,
    LEFT_FRONT;

    private static final SlopePanelShape[] VALUES = values();

    public static SlopePanelShape get(HorizontalRotation rot, boolean front)
    {
        return VALUES[(front ? 0b100 : 0b000) | rot.ordinal()];
    }
}
