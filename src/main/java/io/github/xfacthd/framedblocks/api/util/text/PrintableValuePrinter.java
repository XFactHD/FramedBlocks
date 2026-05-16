package io.github.xfacthd.framedblocks.api.util.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

final class PrintableValuePrinter<T extends Printable> implements ValuePrinter<T> {
    public static final PrintableValuePrinter<?> INSTANCE = new PrintableValuePrinter<>();

    private PrintableValuePrinter() { }

    @Override
    public Component printStyled(T value, ChatFormatting defaultColor) {
        return value.print(defaultColor);
    }
}
