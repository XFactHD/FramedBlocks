package io.github.xfacthd.framedblocks.api.util;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public final class ValueMerger<T> {
    private final Predicate<@Nullable T> defaultCheck;
    private final BinaryOperator<@Nullable T> merger;

    public ValueMerger(BinaryOperator<@Nullable T> merger) {
        this(Objects::isNull, merger);
    }

    public ValueMerger(Predicate<@Nullable T> defaultCheck, BinaryOperator<@Nullable T> merger) {
        this.defaultCheck = defaultCheck;
        this.merger = merger;
    }

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
