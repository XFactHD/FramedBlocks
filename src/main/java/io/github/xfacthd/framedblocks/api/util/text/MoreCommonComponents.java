package io.github.xfacthd.framedblocks.api.util.text;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;

/// Holds various commonly used text components.
///
/// @see CommonComponents
public final class MoreCommonComponents {
    /// 'False' tinted in red.
    public static final Component FALSE = Utils.translate("value", "bool.false").withStyle(ChatFormatting.RED);
    /// 'True' tinted in green.
    public static final Component TRUE = Utils.translate("value", "bool.true").withStyle(ChatFormatting.GREEN);

    /// {@return the user-displayable representation of the given direction}
    ///
    /// @param dir The direction to display
    public static Component direction(Direction dir) {
        return Internal.DIRECTIONS[dir.ordinal()];
    }

    /// {@return the user-displayable representation of the given axis}
    ///
    /// @param axis The axis to display
    public static Component axis(Direction.Axis axis) {
        return Internal.AXES[axis.ordinal()];
    }

    /// {@return the given text indented by two spaces}
    ///
    /// @param text The text to indent
    public static Component indent(Component text) {
        return Component.translatable(Internal.INDENT_KEY, text);
    }

    /// {@return the given text prepended by a dash and a space}
    ///
    /// @param text The text to prepend
    public static Component bullet(Component text) {
        return Component.translatable(Internal.BULLET_KEY, text);
    }

    @ApiStatus.Internal
    public static final class Internal {
        public static final String INDENT_KEY = Utils.translationKey("desc", "generic.indent");
        public static final String BULLET_KEY = Utils.translationKey("desc", "generic.bullet");
        private static final Component[] DIRECTIONS = Util.make(() -> {
            Component[] arr = new Component[6];
            for (Direction dir : Direction.values()) {
                arr[dir.ordinal()] = Utils.translate("value", "dir." + dir.getSerializedName());
            }
            return arr;
        });
        private static final Component[] AXES = Util.make(() -> {
            Component[] arr = new Component[3];
            for (Direction.Axis axis : Direction.Axis.values()) {
                arr[axis.ordinal()] = Utils.translate("value", "axis." + axis.getSerializedName());
            }
            return arr;
        });

        private Internal() { }
    }

    private MoreCommonComponents() { }
}
