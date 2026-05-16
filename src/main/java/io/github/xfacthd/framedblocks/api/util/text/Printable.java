package io.github.xfacthd.framedblocks.api.util.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/// Represents an object which can be displayed to the user as text
@FunctionalInterface
public interface Printable {
    Component print(ChatFormatting defaultColor);
}
