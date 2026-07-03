package io.github.xfacthd.framedblocks.api.camo;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.text.MoreCommonComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

/// Helpers for displaying camos in item tooltips.
public final class CamoPrinter {
    public static final MutableComponent BLOCK_NONE = Utils.translate("desc", "camo_tooltip.block.none").withStyle(ChatFormatting.RED);
    public static final String CAMO_LABEL = Utils.translationKey("desc", "block.stored_camo");
    public static final String CAMO_LABEL_MULTI = Utils.translationKey("desc", "block.stored_camo_multi");
    public static final String DOUBLE_CAMO_SEPARATOR_KEY = Utils.translationKey("desc", "camo_tooltip.double_camo_separator");
    private static final Prefixer DEFAULT_CAMO_PREFIXER = (text, multiple) -> {
        String key = multiple ? CAMO_LABEL_MULTI : CAMO_LABEL;
        return Component.translatable(key, text).withStyle(ChatFormatting.GOLD);
    };

    /// Add the given camos to the given appender.
    ///
    /// @param appender   The appender to add the resulting tooltip lines to
    /// @param camos      The camos to print
    /// @param forcePrint If `true` an empty list or list of only empty camos still appends one tooltip line
    public static void printCamoList(Consumer<Component> appender, @Nullable CamoList camos, boolean forcePrint) {
        printCamoList(appender, camos, DEFAULT_CAMO_PREFIXER, forcePrint);
    }

    /// Add the given camos to the given appender.
    ///
    /// @param appender   The appender to add the resulting tooltip lines to
    /// @param camos      The camos to print
    /// @param prefixer   The prefixer to use for creating the label
    /// @param forcePrint If `true` an empty list or list of only empty camos still appends one tooltip line
    public static void printCamoList(Consumer<Component> appender, @Nullable CamoList camos, Prefixer prefixer, boolean forcePrint) {
        camos = Objects.requireNonNullElse(camos, CamoList.EMPTY);
        if (!forcePrint && camos.isEmptyOrContentsEmpty()) return;

        switch (camos.size()) {
            case 0 -> appender.accept(prefixer.apply(BLOCK_NONE, false));
            case 1 -> {
                MutableComponent camoOne = printCamo(camos.getCamo(0));
                appender.accept(prefixer.apply(camoOne, false));
            }
            case 2 -> {
                MutableComponent camoOne = printCamo(camos.getCamo(0));
                MutableComponent camoTwo = printCamo(camos.getCamo(1));
                MutableComponent combined = combine(camoOne, camoTwo);
                appender.accept(prefixer.apply(combined, true));
            }
            default -> {
                appender.accept(prefixer.apply("", true));
                for (CamoContainer<?, ?> camo : camos) {
                    MutableComponent printed = printCamo(camo);
                    appender.accept(MoreCommonComponents.bullet(printed));
                }
            }
        }
    }

    /// {@return the name of the given camo for display in a tooltip}
    ///
    /// @param camoContainer The camo being displayed
    public static MutableComponent printCamo(CamoContainer<?, ?> camoContainer) {
        if (!camoContainer.isEmpty()) {
            return camoContainer.getContent().getCamoName().withStyle(ChatFormatting.WHITE);
        }
        return BLOCK_NONE.copy();
    }

    /// Combine two printed camos into one line, separated by a pipe character.
    ///
    /// @param compOne The first camo
    /// @param compTwo The second camo
    /// @return The combined line
    public static MutableComponent combine(MutableComponent compOne, MutableComponent compTwo) {
        return Component.translatable(DOUBLE_CAMO_SEPARATOR_KEY, compOne, compTwo).withStyle(ChatFormatting.GOLD);
    }

    /// Prefixes a printed camo with a suitable label.
    @FunctionalInterface
    public interface Prefixer {
        /// {@return the given text prefixed with a label}
        ///
        /// @param text     The text to prefix
        /// @param multiple Whether the text contains one or multiple camos
        MutableComponent apply(Object text, boolean multiple);
    }

    private CamoPrinter() { }
}
