package io.github.xfacthd.framedblocks.common.data.property;

import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.text.Printable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum SlopeType implements StringRepresentable, Printable {
    BOTTOM(SlopeToggleBlock.SlopeOrientation.VERTICAL),
    HORIZONTAL(SlopeToggleBlock.SlopeOrientation.HORIZONTAL),
    TOP(SlopeToggleBlock.SlopeOrientation.VERTICAL);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Component displayName = Utils.translate("value", "slope_type." + name);
    private final SlopeToggleBlock.SlopeOrientation orientation;

    SlopeType(SlopeToggleBlock.SlopeOrientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public Component print(ChatFormatting defaultColor) {
        return displayName.copy().withStyle(defaultColor);
    }

    public SlopeType getOpposite() {
        return switch (this) {
            case TOP -> BOTTOM;
            case BOTTOM -> TOP;
            default -> throw new IllegalArgumentException("Can't get opposite of '" + getSerializedName() + "'!");
        };
    }

    public SlopeToggleBlock.SlopeOrientation getOrientation() {
        return orientation;
    }
}
