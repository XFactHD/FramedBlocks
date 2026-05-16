package io.github.xfacthd.framedblocks.api.util.text;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface ValuePrinter<T> {
    Component print(T value);
}
