package io.github.xfacthd.framedblocks.api.util.text;

import com.google.common.base.CaseFormat;
import io.github.xfacthd.framedblocks.api.util.Utils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/// Holds value printers for various common types.
public final class ValuePrinters {
    private static final Map<Class<?>, ValuePrinter<?>> PRINTERS = new Reference2ObjectOpenHashMap<>();

    public static final ValuePrinter<Boolean> BOOLEAN = register(Boolean.class, ValuePrinters::printBoolean);
    public static final ValuePrinter<Integer> INTEGER = register(Integer.class, ValuePrinters::printInteger);
    public static final ValuePrinter<Direction> DIRECTION = register(Direction.class, ValuePrinter.of(MoreCommonComponents::direction));
    public static final ValuePrinter<Direction.Axis> AXIS = register(Direction.Axis.class, ValuePrinter.of(MoreCommonComponents::axis));
    public static final ValuePrinter<Half> HALF = registerEnum(Half.class);
    public static final ValuePrinter<Boolean> HALF_BOOL = (top, defaultColor) -> HALF.printStyled(top ? Half.TOP : Half.BOTTOM, defaultColor);
    public static final ValuePrinter<StairsShape> STAIRS_SHAPE = registerEnum(StairsShape.class);
    public static final ValuePrinter<RailShape> RAIL_SHAPE = registerEnum(RailShape.class);
    public static final ValuePrinter<DoorHingeSide> HINGE_SIDE = registerEnum(DoorHingeSide.class);

    /// {@return the built-in value printer for the given type or null if none is registered}
    ///
    /// @param valueClass The type to get the printer for
    @SuppressWarnings("unchecked")
    public static <T> @Nullable ValuePrinter<T> find(Class<T> valueClass) {
        ValuePrinter<?> printer = PRINTERS.get(valueClass);
        if (printer != null) {
            return (ValuePrinter<T>) printer;
        }
        if (Printable.class.isAssignableFrom(valueClass)) {
            return (ValuePrinter<T>) PrintableValuePrinter.INSTANCE;
        }
        return null;
    }

    /// {@return a value printer for the given enum type, creating one if no built-in one is applicable}
    /// The returned printer, if newly created, uses translation keys in the format `value.framedblocks.snake_case_class_name.value_serialized_name`.
    ///
    /// @param valueClass The type to create the printer for
    public static <T extends Enum<T> & StringRepresentable> ValuePrinter<T> createForEnum(Class<T> valueClass) {
        ValuePrinter<T> printer = find(valueClass);
        return printer != null ? printer : createForEnum(valueClass, getDefaultEnumPrefix(valueClass));
    }

    /// {@return a value printer for the given enum type, creating one if no built-in one is applicable}
    /// The returned printer uses translation keys in the format `value.framedblocks.prefix.value_serialized_name`.
    ///
    /// @param valueClass The type to create the printer for
    public static <T extends Enum<T> & StringRepresentable> ValuePrinter<T> createForEnum(Class<T> valueClass, String prefix) {
        return createForEnumRaw(valueClass, prefix);
    }

    private static Component printBoolean(Boolean value, ChatFormatting defaultColor) {
        return value ? MoreCommonComponents.TRUE : MoreCommonComponents.FALSE;
    }

    private static Component printInteger(Integer value, ChatFormatting defaultColor) {
        return Component.literal(value.toString()).withStyle(defaultColor);
    }

    private static <T extends Enum<T> & StringRepresentable> ValuePrinter<T> registerEnum(Class<T> valueClass) {
        return register(valueClass, createForEnumRaw(valueClass, getDefaultEnumPrefix(valueClass)));
    }

    private static <T extends Enum<T> & StringRepresentable> ValuePrinter<T> createForEnumRaw(Class<T> valueClass, String prefix) {
        T[] constants = valueClass.getEnumConstants();
        Component[] components = new Component[constants.length];
        prefix += ".";
        for (T constant : constants) {
            components[constant.ordinal()] = Utils.translate("value", prefix + constant.getSerializedName());
        }
        return ValuePrinter.of(value -> components[value.ordinal()]);
    }

    private static <T extends Enum<T>> String getDefaultEnumPrefix(Class<T> valueClass) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, valueClass.getSimpleName());
    }

    private static <T> ValuePrinter<T> register(Class<T> valueClass, ValuePrinter<T> printer) {
        PRINTERS.put(valueClass, printer);
        return printer;
    }

    private ValuePrinters() { }
}
