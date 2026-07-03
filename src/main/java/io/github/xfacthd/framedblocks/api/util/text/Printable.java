package io.github.xfacthd.framedblocks.api.util.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/// Represents an object which can be displayed to the user as text.
@FunctionalInterface
public interface Printable {
    /// {@return the value in a user-displayable representation}
    ///
    /// Unless the value has specific formatting (i.e. red/green for false/true respectively),
    /// the given color should be applied to the resulting text component.
    ///
    /// @param defaultColor The default color to apply to the text
    Component print(ChatFormatting defaultColor);
}
