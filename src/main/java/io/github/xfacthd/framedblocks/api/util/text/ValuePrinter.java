package io.github.xfacthd.framedblocks.api.util.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

/// Represents a function that converts a given value to user-displayable text.
@FunctionalInterface
public interface ValuePrinter<T> {
    /// {@return the user-displayable representation of the given value without a color override}
    ///
    /// @param value The value to print
    default Component print(T value) {
        return printStyled(value, ChatFormatting.RESET);
    }

    /// {@return the user-displayable representation of the given value with the given color}
    ///
    /// @param value        The value to print
    /// @param defaultColor The color to apply to the text
    Component printStyled(T value, ChatFormatting defaultColor);

    /// {@return a value printer wrapping the given plain printing function}
    ///
    /// @param plainPrinter The function to wrap
    static <T> ValuePrinter<T> of(Function<T, Component> plainPrinter) {
        return (value, defaultColor) -> plainPrinter.apply(value).copy().withStyle(defaultColor);
    }
}
