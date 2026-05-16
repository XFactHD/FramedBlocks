package io.github.xfacthd.framedblocks.api.block.item.placement;

import io.github.xfacthd.framedblocks.api.util.text.ValuePrinter;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinters;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.function.BiConsumer;

public interface PropertyPrinter<T extends Comparable<T>> {
    /// Print the provided value to the provided output
    ///
    /// @param value  The value to be printed
    /// @param output The output to pass printed lines to
    void print(T value, LineOutput output);

    /// Create a [PropertyPrinter] for the provided property with the provided label.
    ///
    /// The provided label string must be a translation key and is expected to translate to
    /// a string in the format of `LabelText: %s`.
    ///
    /// @param label    The label to prefix the property value with
    /// @param property The property to display
    static <T extends Comparable<T>> PropertyPrinter<T> of(String label, Property<T> property) {
        ValuePrinter<T> valuePrinter = ValuePrinters.find(property.getValueClass());
        if (valuePrinter == null) {
            throw new IllegalArgumentException("Cannot find suitable ValuePrinter for value class of " + property);
        }
        return of(label, valuePrinter);
    }

    /// Create a [PropertyPrinter] with the provided label and value printer.
    ///
    /// The provided label string must be a translation key and is expected to translate to
    /// a string in the format of `LabelText: %s`.
    ///
    /// @param label        The label to prefix the property value with
    /// @param valuePrinter The value printer to use for displaying the property's values
    static <T extends Comparable<T>> PropertyPrinter<T> of(String label, ValuePrinter<T> valuePrinter) {
        return (value, output) -> output.accept(label, valuePrinter.print(value));
    }

    interface LineOutput extends BiConsumer<String, Component> {
        /// Output the provided label-value pair.
        ///
        /// The provided label string must be a translation key and is expected to translate to
        /// a string in the format of `LabelText: %s`.
        ///
        /// @param label The label to prefix the property value with
        /// @param value The stringified value
        @Override
        void accept(String label, Component value);
    }
}
