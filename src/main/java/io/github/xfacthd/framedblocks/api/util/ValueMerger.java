package io.github.xfacthd.framedblocks.api.util;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

/// Merges two given values, falling back to the one if the other is considered "default".
public final class ValueMerger<T> {
    private final Predicate<@Nullable T> defaultCheck;
    private final BinaryOperator<@Nullable T> merger;

    /// Construct a value merger with the given merging function and `value==null`
    /// as the default predicate.
    ///
    /// @param merger The operator to use for merging the values
    public ValueMerger(BinaryOperator<@Nullable T> merger) {
        this(Objects::isNull, merger);
    }

    /// Construct a value merger with the given default predicate and merging function.
    ///
    /// @param defaultCheck The predicate to use for determining whether one of the values is "default"
    /// @param merger       The operator to use for merging the values
    public ValueMerger(Predicate<@Nullable T> defaultCheck, BinaryOperator<@Nullable T> merger) {
        this.defaultCheck = defaultCheck;
        this.merger = merger;
    }

    /// Merge the given values through this value merger.
    ///
    /// @param valOne The first input value
    /// @param valTwo The second input value
    /// @return the merged value
    @Contract("!null, _ -> !null; _, !null -> !null")
    public @Nullable T apply(@Nullable T valOne, @Nullable T valTwo) {
        if (defaultCheck.test(valOne)) {
            return valTwo;
        }
        if (defaultCheck.test(valTwo)) {
            return valOne;
        }
        return merger.apply(valOne, valTwo);
    }
}
