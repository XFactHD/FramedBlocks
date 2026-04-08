package io.github.xfacthd.framedblocks.api.camo;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public final class CamoPrinter {
    public static final MutableComponent BLOCK_NONE = Utils.translate("desc", "camo_tooltip.block.none").withStyle(ChatFormatting.RED);
    public static final String DOUBLE_CAMO_SEPARATOR_KEY = Utils.translationKey("desc", "camo_tooltip.double_camo_separator");
    public static final String MULTI_CAMO_ENTRY_PREFIX_KEY = Utils.translationKey("desc", "camo_tooltip.multi_camo_prefix");
    private static final Prefixer DEFAULT_CAMO_PREFIXER = (text, multiple) -> {
        String key = multiple ? IFramedBlock.CAMO_LABEL_MULTI : IFramedBlock.CAMO_LABEL;
        return Component.translatable(key, text).withStyle(ChatFormatting.GOLD);
    };

    public static void printCamoList(Consumer<Component> appender, @Nullable CamoList camos, boolean blueprint) {
        printCamoList(appender, camos, DEFAULT_CAMO_PREFIXER, blueprint);
    }

    public static void printCamoList(Consumer<Component> appender, @Nullable CamoList camos, Prefixer prefixer, boolean blueprint) {
        camos = Objects.requireNonNullElse(camos, CamoList.EMPTY);
        if (!blueprint && camos.isEmptyOrContentsEmpty()) return;

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
                    appender.accept(Component.translatable(MULTI_CAMO_ENTRY_PREFIX_KEY, printed));
                }
            }
        }
    }

    public static MutableComponent printCamo(CamoContainer<?, ?> camoContainer) {
        if (!camoContainer.isEmpty()) {
            return camoContainer.getContent().getCamoName().withStyle(ChatFormatting.WHITE);
        }
        return BLOCK_NONE.copy();
    }

    public static MutableComponent combine(MutableComponent compOne, MutableComponent compTwo) {
        return Component.translatable(DOUBLE_CAMO_SEPARATOR_KEY, compOne, compTwo).withStyle(ChatFormatting.GOLD);
    }

    public interface Prefixer {
        MutableComponent apply(Object text, boolean multiple);
    }

    private CamoPrinter() { }
}
