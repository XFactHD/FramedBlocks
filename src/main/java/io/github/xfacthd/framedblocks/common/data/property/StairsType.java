package io.github.xfacthd.framedblocks.common.data.property;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.text.Printable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum StairsType implements StringRepresentable, Printable {
    VERTICAL,
    TOP_FWD,
    TOP_CCW,
    TOP_BOTH,
    BOTTOM_FWD,
    BOTTOM_CCW,
    BOTTOM_BOTH;

    private final String name = toString().toLowerCase(Locale.ENGLISH);
    private final Component displayName = Utils.translate("value", "stairs_type." + name);

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public Component print(ChatFormatting defaultColor) {
        return displayName.copy().withStyle(defaultColor);
    }

    public boolean isTop() {
        return this == TOP_FWD || this == TOP_CCW || this == TOP_BOTH;
    }

    public boolean isBottom() {
        return this == BOTTOM_FWD || this == BOTTOM_CCW || this == BOTTOM_BOTH;
    }

    public boolean isForward() {
        return this == TOP_FWD || this == BOTTOM_FWD || this == TOP_BOTH || this == BOTTOM_BOTH;
    }

    public boolean isCounterClockwise() {
        return this == TOP_CCW || this == BOTTOM_CCW || this == TOP_BOTH || this == BOTTOM_BOTH;
    }

    public static StairsType get(boolean top, boolean fwd, boolean ccw) {
        if (fwd && ccw) {
            return top ? TOP_BOTH : BOTTOM_BOTH;
        }
        if (fwd) {
            return top ? TOP_FWD : BOTTOM_FWD;
        }
        if (ccw) {
            return top ? TOP_CCW : BOTTOM_CCW;
        }
        return VERTICAL;
    }
}
