package io.github.xfacthd.framedblocks.common.item.applicator;

import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.common.FBContent;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public final class CamoApplicatorUtils {
    public static <T> void updateConfig(ItemStack stack, T updateParam, BiFunction<CamoApplicatorConfig, T, CamoApplicatorConfig> updater) {
        stack.update(FBContent.DC_TYPE_APPLICATOR_CONFIG, CamoApplicatorConfig.DEFAULT, updateParam, updater);
    }

    public static void updateModifierInConfig(ItemStack stack, FrameModifier modifier, boolean apply) {
        updateConfig(stack, apply, switch (modifier) {
            case GLOWING -> CamoApplicatorConfig::withGlowstone;
            case INTANGIBLE -> CamoApplicatorConfig::withPhantomPaste;
            case REINFORCED -> CamoApplicatorConfig::withReinforcement;
            case EMISSIVE -> CamoApplicatorConfig::withGlowPaste;
        });
    }

    public static void updateConfigAfterApplication(ItemStack stack, Player player) {
        updateConfig(stack, player, (config, modPlayer) -> {
            CamoApplicatorConfig.Mode mode = config.mode();
            if (mode == CamoApplicatorConfig.Mode.FIXED) {
                return config;
            }

            CamoApplicatorContent content = CamoApplicatorContent.of(stack);
            int oldSlot = config.selectedSlot();
            int newSlot = switch (mode) {
                case CYCLING -> content.findFirstNonEmptyCamoSlotAfter(oldSlot);
                case AUTO_INCREMENT -> {
                    if (!content.hasCamoStackInSlot(oldSlot)) {
                        yield content.findFirstNonEmptyCamoSlotAfter(oldSlot);
                    }
                    yield oldSlot;
                }
                case RANDOM -> {
                    IntList filledSlots = new IntArrayList(CamoApplicatorContent.CAMO_COUNT);
                    for (int i = 0; i < CamoApplicatorContent.CAMO_COUNT; i++) {
                        if (content.hasCamoStackInSlot(i)) {
                            filledSlots.add(i);
                        }
                    }
                    if (!filledSlots.isEmpty()) {
                        int idx = modPlayer.getRandom().nextInt(filledSlots.size());
                        yield filledSlots.getInt(idx);
                    }
                    yield oldSlot;
                }
                default -> throw new AssertionError();
            };
            return config.withSlot(newSlot);
        });
    }

    public static <T> void updateContent(ItemStack stack, T slotIndex, BiFunction<CamoApplicatorContent, T, CamoApplicatorContent> updater) {
        stack.update(FBContent.DC_TYPE_APPLICATOR_CONTENT, CamoApplicatorContent.EMPTY, slotIndex, updater);
    }

    public static void setCamoInContent(ItemStack stack, int slotIndex, ItemStack newCamoStack) {
        updateContent(stack, slotIndex, (content, slot) -> {
            CamoApplicatorContent.CamoEntry[] newEntries = Arrays.copyOf(content.camoEntries(), CamoApplicatorContent.CAMO_COUNT);
            newEntries[slot] = CamoApplicatorContent.CamoEntry.of(newCamoStack);
            return new CamoApplicatorContent(newEntries, content.modifierStacks());
        });
    }

    public static void updateCamoInContent(ItemStack stack, int slotIndex, UnaryOperator<ItemStack> updater) {
        updateContent(stack, slotIndex, (content, slot) -> {
            CamoApplicatorContent.CamoEntry[] newStacks = Arrays.copyOf(content.camoEntries(), CamoApplicatorContent.CAMO_COUNT);
            newStacks[slot] = CamoApplicatorContent.CamoEntry.of(updater.apply(content.camoEntries()[slot].getStack().copy()));
            return new CamoApplicatorContent(newStacks, content.modifierStacks());
        });
    }

    public static void setModifierInContent(ItemStack stack, FrameModifier modifier, int amount) {
        updateContent(stack, modifier, (content, mod) -> {
            int[] newStacks = Arrays.copyOf(content.modifierStacks(), FrameModifier.COUNT);
            newStacks[mod.ordinal()] = amount;
            return new CamoApplicatorContent(content.camoEntries(), newStacks);
        });
    }

    public static void updateModifierInContent(ItemStack stack, FrameModifier modifier, int diff) {
        updateContent(stack, modifier, (content, mod) -> {
            int[] newStacks = Arrays.copyOf(content.modifierStacks(), FrameModifier.COUNT);
            newStacks[mod.ordinal()] += diff;
            return new CamoApplicatorContent(content.camoEntries(), newStacks);
        });
    }

    private CamoApplicatorUtils() { }
}
