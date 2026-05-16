package io.github.xfacthd.framedblocks.api.util.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

@FunctionalInterface
public interface ValuePrinter<T> {
    default Component print(T value) {
        return printStyled(value, ChatFormatting.RESET);
    }

    Component printStyled(T value, ChatFormatting defaultColor);

    static <T> ValuePrinter<T> of(Function<T, Component> plainPrinter) {
        return (value, defaultColor) -> plainPrinter.apply(value).copy().withStyle(defaultColor);
    }
}
