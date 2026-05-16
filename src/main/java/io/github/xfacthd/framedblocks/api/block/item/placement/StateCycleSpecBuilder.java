package io.github.xfacthd.framedblocks.api.block.item.placement;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.internal.StateCycleSpecAssembler;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinter;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.SequencedMap;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class StateCycleSpecBuilder {
    private final Block block;
    private final SequencedMap<Property<?>, PropertySpec<?>> properties = new LinkedHashMap<>();
    private final SequencedMap<Property<?>, PropertyPrinter<?>> propertyPrinters = new Reference2ObjectLinkedOpenHashMap<>();
    @Nullable
    private PlacementStatePostProcessor postProcessor = null;
    private boolean mayLockState = true;
    private boolean reversed = false;

    StateCycleSpecBuilder(Block block) {
        this.block = block;
    }

    /// Add the provided property to the set of properties to cycle.
    /// A given property is only cycled once all subsequent properties have cycled back to their respective original value.
    ///
    /// @param property The property to cycle
    public <T extends Comparable<T>> StateCycleSpecBuilder property(Property<T> property) {
        return property(property, UnaryOperator.identity());
    }

    /// Add the provided property to the set of properties to cycle and specify how the provided property is displayed in the tooltip.
    /// A given property is only cycled once all subsequent properties have cycled back to their respective original value.
    ///
    /// The provided label string must be a translation key and is expected to translate to
    /// a string in the format of `LabelText: %s`.
    ///
    /// @param property The property to cycle
    /// @param label    The label to prefix the property value with
    /// @see PropertyLabels
    public <T extends Comparable<T>> StateCycleSpecBuilder property(Property<T> property, String label) {
        return property(property).propertyPrinter(property, label);
    }

    /// Add the provided property to the set of properties to cycle.
    /// A given property is only cycled once all subsequent properties have cycled back to their respective original value.
    ///
    /// @param property The property to cycle
    /// @param builder  The builder for configuring details of the provided property
    public <T extends Comparable<T>> StateCycleSpecBuilder property(Property<T> property, UnaryOperator<PropertyBuilder<T>> builder) {
        if (property == FramedProperties.STATE_LOCKED) {
            throw new IllegalArgumentException("FramedProperties.STATE_LOCKED cannot be cycled");
        }
        if (property.getPossibleValues().size() < 2) {
            throw new IllegalArgumentException("Cannot cycle property with less than 2 values");
        }
        if (!block.defaultBlockState().hasProperty(property)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Property %s is not valid for %s", property, block));
        }
        if (properties.containsKey(property)) {
            throw new IllegalStateException("Duplicate declaration of property " + property);
        }
        builder.apply(new PropertyBuilder<>(this, property)).build();
        return this;
    }

    /// Specify how the provided property is displayed in the tooltip.
    ///
    /// The provided label string must be a translation key and is expected to translate to
    /// a string in the format of `LabelText: %s`.
    ///
    /// @param property The property to display
    /// @param label    The label to prefix the property value with
    /// @see PropertyLabels
    public <T extends Comparable<T>> StateCycleSpecBuilder propertyPrinter(Property<T> property, String label) {
        return propertyPrinter(property, PropertyPrinter.of(label, property));
    }

    /// Specify how the provided property is displayed in the tooltip.
    ///
    /// The provided label string must be a translation key and is expected to translate to
    /// a string in the format of `LabelText: %s`.
    ///
    /// @param property     The property to display
    /// @param label        The label to prefix the property value with
    /// @param valuePrinter The value printer to use for displaying the property's values
    /// @see PropertyLabels
    public <T extends Comparable<T>> StateCycleSpecBuilder propertyPrinter(Property<T> property, String label, ValuePrinter<T> valuePrinter) {
        return propertyPrinter(property, PropertyPrinter.of(label, valuePrinter));
    }

    /// Specify how the provided property is displayed in the tooltip.
    ///
    /// @param property The property to display
    /// @param printer  The printer to use for displaying the property's values
    public <T extends Comparable<T>> StateCycleSpecBuilder propertyPrinter(Property<T> property, PropertyPrinter<T> printer) {
        if (property == FramedProperties.STATE_LOCKED) {
            throw new IllegalArgumentException("FramedProperties.STATE_LOCKED cannot be printed");
        }
        if (!block.defaultBlockState().hasProperty(property)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Property %s is not valid for %s", property, block));
        }
        if (propertyPrinters.putIfAbsent(property, printer) != null) {
            throw new IllegalStateException("Duplicate printer declaration for property " + property);
        }
        return this;
    }

    /// Add a [PlacementStatePostProcessor] to adjust the state about to be placed without affecting the stored cycled state,
    /// i.e. to adjust the state based on adjacent blocks.
    public StateCycleSpecBuilder postProcessor(PlacementStatePostProcessor postProcessor) {
        this.postProcessor = postProcessor;
        return this;
    }

    /// Disable automatic application of [FramedProperties#STATE_LOCKED] to blocks with state lock support.
    public StateCycleSpecBuilder disableStateLock() {
        if (!block.defaultBlockState().hasProperty(FramedProperties.STATE_LOCKED)) {
            throw new UnsupportedOperationException(String.format(Locale.ROOT, "%s does not support state locking", block));
        }
        this.mayLockState = false;
        return this;
    }

    /// Reverse the order in which the specified properties are cycled, such that earlier properties are cycled faster than
    /// later properties. This does not affect the display order of the properties.
    public StateCycleSpecBuilder reverseCycleOrder() {
        reversed = true;
        return this;
    }

    <T extends StateCycleSpec> T assemble(StateCycleSpecAssembler.EntryAssembler<T> assembler) {
        if (properties.size() != propertyPrinters.size()) {
            throw new IllegalStateException("Property count does not match property printer count");
        }
        for (Property<?> property : properties.keySet()) {
            if (!propertyPrinters.containsKey(property)) {
                throw new IllegalStateException("Properties to cycle do not match printed properties");
            }
        }
        boolean lockState = mayLockState && block.defaultBlockState().hasProperty(FramedProperties.STATE_LOCKED);
        // Keeping the properties in the order of slowest cycled to fastest cycled ensures they are cycled in the correct order
        List<PropertySpec<?>> propertiesList;
        if (reversed) {
            // Keep in order of declaration to cycle earlier properties faster than later properties
            propertiesList = new ArrayList<>(properties.sequencedValues());
        } else {
            // Reverse order to cycle earlier properties slower than later properties
            propertiesList = new ArrayList<>(properties.sequencedValues().reversed());
        }
        return assembler.assemble(block, propertiesList, propertyPrinters, postProcessor, lockState);
    }

    public StateCycleSpec build() {
        return InternalAPI.INSTANCE.buildSingleBlockStateCycleSpec(this, StateCycleSpecBuilder::assemble);
    }

    public static final class PropertyBuilder<T extends Comparable<T>> {
        private static final Comparator<Direction> DIRECTION_COMPARATOR = Comparator.comparingInt(ValueOrders.FACING::indexOf);

        private final StateCycleSpecBuilder owner;
        private final Property<T> property;
        @Nullable
        private final List<T> defaultValues;
        @Nullable
        private List<T> values;
        @Nullable
        private Predicate<T> filter;
        @Nullable
        private PropertyPrinter<T> printer;

        @SuppressWarnings("unchecked")
        PropertyBuilder(StateCycleSpecBuilder owner, Property<T> property) {
            this.owner = owner;
            this.property = property;
            if (property instanceof EnumProperty<?> prop && prop.getValueClass() == Direction.class) {
                List<Direction> values = (List<Direction>) new ArrayList<>(prop.getPossibleValues());
                values.sort(DIRECTION_COMPARATOR);
                this.defaultValues = (List<T>) values;
            } else if (property instanceof BooleanProperty) {
                this.defaultValues = List.copyOf(property.getPossibleValues().reversed());
            } else {
                this.defaultValues = null;
            }
        }

        /// Specify the exact list of values to cycle through and their order.
        /// Cannot be combined with a filter.
        ///
        /// @param values The property values to cycle through
        public PropertyBuilder<T> values(List<T> values) {
            Preconditions.checkState(filter == null, "Cannot combine explicit values with filter");
            if (values.size() < 2) {
                throw new IllegalArgumentException("Property must have at least 2 values");
            }
            HashSet<T> allValues = new HashSet<>(property.getPossibleValues());
            if (!allValues.containsAll(values)) {
                throw new IllegalArgumentException(String.format(
                        Locale.ROOT,
                        "Value list %s is not compatible with property %s",
                        values,
                        property
                ));
            }
            this.values = values;
            return this;
        }

        /// Specify a predicate to use for filtering the values to cycle through.
        /// Cannot be combined with explicit declaration of values.
        ///
        /// @param filter The predicate to filter with
        public PropertyBuilder<T> filter(Predicate<T> filter) {
            Preconditions.checkState(values == null, "Cannot combine filter with explicit values");
            this.filter = filter;
            return this;
        }

        /// Specify how the property is displayed in the tooltip.
        ///
        /// The provided label string must be a translation key and is expected to translate to
        /// a string in the format of `LabelText: %s`.
        ///
        /// Use [StateCycleSpecBuilder#propertyPrinter(Property, String)] or [StateCycleSpecBuilder#propertyPrinter(Property, PropertyPrinter)]
        /// instead if the display order does not match the cycle order.
        ///
        /// @param label The label to prefix the property value with
        /// @see PropertyLabels
        public PropertyBuilder<T> printer(String label) {
            return printer(PropertyPrinter.of(label, property));
        }

        /// Specify how the property is displayed in the tooltip.
        ///
        /// The provided label string must be a translation key and is expected to translate to
        /// a string in the format of `LabelText: %s`.
        ///
        /// Use [StateCycleSpecBuilder#propertyPrinter(Property, String)] or [StateCycleSpecBuilder#propertyPrinter(Property, PropertyPrinter)]
        /// instead if the display order does not match the cycle order.
        ///
        /// @param label        The label to prefix the property value with
        /// @param valuePrinter The value printer to use for displaying the property's values
        /// @see PropertyLabels
        public PropertyBuilder<T> printer(String label, ValuePrinter<T> valuePrinter) {
            return printer(PropertyPrinter.of(label, valuePrinter));
        }

        /// Specify how the provided property is displayed in the tooltip.
        ///
        /// Use [StateCycleSpecBuilder#propertyPrinter(Property, String)] or [StateCycleSpecBuilder#propertyPrinter(Property, PropertyPrinter)]
        /// instead if the display order does not match the cycle order.
        ///
        /// @param printer The printer to use for displaying the property's values
        public PropertyBuilder<T> printer(PropertyPrinter<T> printer) {
            this.printer = printer;
            return this;
        }

        void build() {
            List<T> values;
            if (this.values != null) {
                values = this.values;
            } else if (filter != null || defaultValues == null) {
                values = new ArrayList<>(property.getPossibleValues());
                if (filter != null) {
                    values.removeIf(filter.negate());
                    if (values.size() < 2) {
                        throw new IllegalArgumentException("Property must have at least 2 values");
                    }
                }
            } else {
                values = defaultValues;
            }
            values = List.copyOf(values);
            owner.properties.put(property, new PropertySpec<>(property, values));
            if (printer != null) {
                owner.propertyPrinters.put(property, printer);
            }
        }
    }
}
