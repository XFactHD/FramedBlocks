package io.github.xfacthd.framedblocks.common.item.applicator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public record CamoApplicatorConfig(
        Mode mode,
        int selectedSlot,
        boolean applyGlowstone,
        boolean applyPhantomPaste,
        boolean applyReinforcement,
        boolean applyGlowPaste
) implements TooltipProvider {
    public static final Codec<CamoApplicatorConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Mode.CODEC.fieldOf("mode").forGetter(CamoApplicatorConfig::mode),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("selected_slot").forGetter(CamoApplicatorConfig::selectedSlot),
            Codec.BOOL.fieldOf("apply_glowstone").forGetter(CamoApplicatorConfig::applyGlowstone),
            Codec.BOOL.fieldOf("apply_phantom_paste").forGetter(CamoApplicatorConfig::applyPhantomPaste),
            Codec.BOOL.fieldOf("apply_reinforcement").forGetter(CamoApplicatorConfig::applyReinforcement),
            Codec.BOOL.fieldOf("apply_glowPaste").forGetter(CamoApplicatorConfig::applyGlowPaste)
    ).apply(inst, CamoApplicatorConfig::new));
    public static final StreamCodec<ByteBuf, CamoApplicatorConfig> STREAM_CODEC = StreamCodec.composite(
            Mode.STREAM_CODEC,
            CamoApplicatorConfig::mode,
            ByteBufCodecs.VAR_INT,
            CamoApplicatorConfig::selectedSlot,
            ByteBufCodecs.BOOL,
            CamoApplicatorConfig::applyGlowstone,
            ByteBufCodecs.BOOL,
            CamoApplicatorConfig::applyPhantomPaste,
            ByteBufCodecs.BOOL,
            CamoApplicatorConfig::applyReinforcement,
            ByteBufCodecs.BOOL,
            CamoApplicatorConfig::applyGlowPaste,
            CamoApplicatorConfig::new
    );
    public static final CamoApplicatorConfig DEFAULT = new CamoApplicatorConfig(Mode.AUTO_INCREMENT, 0, false, false, false, false);
    public static final String APPLICATOR_MODE = Utils.translationKey("desc", "camo_applicator.mode");
    public static final String SELECTED_ITEM = Utils.translationKey("desc", "camo_applicator.selected");
    public static final String SELECTED_ITEM_VALUE = Utils.translationKey("desc", "camo_applicator.selected.value");
    public static final Component SELECTED_ITEM_EMPTY = Utils.translate("desc", "camo_applicator.selected.empty")
            .withStyle(ChatFormatting.RED, ChatFormatting.ITALIC);
    public static final String APPLY_GLOWSTONE = Utils.translationKey("desc", "camo_applicator.modifier.glowstone");
    public static final String APPLY_PHANTOM_PASTE = Utils.translationKey("desc", "camo_applicator.modifier.phantom_paste");
    public static final String APPLY_REINFORCEMENT = Utils.translationKey("desc", "camo_applicator.modifier.reinforcement");
    public static final String APPLY_GLOW_PASTE = Utils.translationKey("desc", "camo_applicator.modifier.glow_paste");
    public static final Component FALSE = Utils.translate("value", "camo_applicator.false").withStyle(ChatFormatting.RED);
    public static final Component TRUE = Utils.translate("value", "camo_applicator.true").withStyle(ChatFormatting.GREEN);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> appender, TooltipFlag flag, DataComponentGetter componentGetter) {
        appender.accept(Component.translatable(APPLICATOR_MODE, mode.translation).withStyle(ChatFormatting.GOLD));

        ItemStack stack = CamoApplicatorContent.of(componentGetter).getCamoStack(selectedSlot);
        Component itemName = SELECTED_ITEM_EMPTY;
        if (!stack.isEmpty()) {
            itemName = Component.translatable(
                    SELECTED_ITEM_VALUE,
                    stack.getCount(),
                    stack.getItemName()
            ).withStyle(ChatFormatting.WHITE);
        }
        appender.accept(Component.translatable(SELECTED_ITEM, itemName).withStyle(ChatFormatting.GOLD));

        appender.accept(Component.translatable(APPLY_GLOWSTONE, applyGlowstone ? TRUE : FALSE).withStyle(ChatFormatting.GOLD));
        appender.accept(Component.translatable(APPLY_PHANTOM_PASTE, applyPhantomPaste ? TRUE : FALSE).withStyle(ChatFormatting.GOLD));
        appender.accept(Component.translatable(APPLY_REINFORCEMENT, applyReinforcement ? TRUE : FALSE).withStyle(ChatFormatting.GOLD));
        appender.accept(Component.translatable(APPLY_GLOW_PASTE, applyGlowPaste ? TRUE : FALSE).withStyle(ChatFormatting.GOLD));
    }

    public boolean applyModifier(FrameModifier modifier) {
        return switch (modifier) {
            case GLOWING -> applyGlowstone;
            case INTANGIBLE -> applyPhantomPaste;
            case REINFORCED -> applyReinforcement;
            case EMISSIVE -> applyGlowPaste;
        };
    }

    public CamoApplicatorConfig withMode(Mode mode) {
        return new CamoApplicatorConfig(mode, selectedSlot, applyGlowstone, applyPhantomPaste, applyReinforcement, applyGlowPaste);
    }

    public CamoApplicatorConfig withSlot(int slot) {
        return new CamoApplicatorConfig(mode, slot, applyGlowstone, applyPhantomPaste, applyReinforcement, applyGlowPaste);
    }

    public CamoApplicatorConfig withGlowstone(boolean applyGlowstone) {
        return new CamoApplicatorConfig(mode, selectedSlot, applyGlowstone, applyPhantomPaste, applyReinforcement, applyGlowPaste);
    }

    public CamoApplicatorConfig withPhantomPaste(boolean applyPhantomPaste) {
        return new CamoApplicatorConfig(mode, selectedSlot, applyGlowstone, applyPhantomPaste, applyReinforcement, applyGlowPaste);
    }

    public CamoApplicatorConfig withReinforcement(boolean applyReinforcement) {
        return new CamoApplicatorConfig(mode, selectedSlot, applyGlowstone, applyPhantomPaste, applyReinforcement, applyGlowPaste);
    }

    public CamoApplicatorConfig withGlowPaste(boolean applyGlowPaste) {
        return new CamoApplicatorConfig(mode, selectedSlot, applyGlowstone, applyPhantomPaste, applyReinforcement, applyGlowPaste);
    }

    public static CamoApplicatorConfig of(ItemStack stack) {
        return stack.getOrDefault(FBContent.DC_TYPE_APPLICATOR_CONFIG, DEFAULT);
    }

    public enum Mode implements StringRepresentable {
        FIXED,
        AUTO_INCREMENT,
        CYCLING,
        RANDOM,
        ;

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
        private static final IntFunction<Mode> BY_ID = ByIdMap.continuous(Mode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, Mode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Mode::ordinal);

        private final String name = toString().toLowerCase(Locale.ROOT);
        private final Component translation = Utils.translate("desc", "camo_applicator.mode." + name).withStyle(ChatFormatting.WHITE);
        private final Component tooltip = Utils.translate("desc", "camo_applicator.mode." + name + ".tooltip");

        public Component getTranslation() {
            return translation;
        }

        public Component getTooltip() {
            return tooltip;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public static Mode byId(int id) {
            return BY_ID.apply(id);
        }
    }
}
