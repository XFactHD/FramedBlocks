package io.github.xfacthd.framedblocks.client.screen.saw;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeAdditive;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCalculation;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeMatchResult;
import io.github.xfacthd.framedblocks.common.util.CachingIngredientResolver;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class SawRecipeFailurePrinter {
    public static final String TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM = Utils.translationKey("tooltip", "framing_saw.have_x_but_need_y_item");
    public static final String TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM_MULTI = Utils.translationKey("tooltip", "framing_saw.have_x_but_need_y_item_multi");
    public static final String TOOLTIP_HAVE_X_BUT_NEED_Y_TAG = Utils.translationKey("tooltip", "framing_saw.have_x_but_need_y_tag");
    public static final String TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM_COUNT = Utils.translationKey("tooltip", "framing_saw.have_x_but_need_y_item_count");
    public static final String TOOLTIP_HAVE_X_BUT_NEED_Y_MATERIAL_COUNT = Utils.translationKey("tooltip", "framing_saw.have_x_but_need_y_material_count");
    public static final String TOOLTIP_OUTPUT_COUNT = Utils.translationKey("tooltip", "framing_saw.output_count");
    public static final Component TOOLTIP_HAVE_ITEM_NONE = Utils.translate("tooltip", "framing_saw.have_item_none").withStyle(ChatFormatting.GOLD);
    public static final String TOOLTIP_PRESS_TO_SHOW = Utils.translationKey("tooltip", "framing_saw.press_to_show");
    public static final String TOOLTIP_USE_INTERMEDIATE = Utils.translationKey("tooltip", "framing_saw.use_intermediate");

    static List<Component> appendRecipeFailure(
            List<Component> components,
            FramingSawRecipeCache cache,
            CachingIngredientResolver.Multi additiveResolver,
            FramingSawRecipe recipe,
            FramingSawRecipeMatchResult matchResult,
            IFramingSawScreen screen
    ) {
        if (matchResult.success()) {
            return components;
        }

        components.add(matchResult.translation());

        ItemStack input = screen.getInputStack();
        int listAdditives = -1;
        List<MutableComponent> detail = switch (matchResult) {
            case CAMO_PRESENT -> List.of();
            case MATERIAL_VALUE -> {
                int matIn = input.isEmpty() ? 0 : cache.getMaterialValue(input.getItem()) * input.getCount();
                int matReq = recipe.getMaterialAmount();
                yield List.of(Component.translatable(
                        TOOLTIP_HAVE_X_BUT_NEED_Y_MATERIAL_COUNT,
                        Component.literal(Integer.toString(matIn)).withStyle(ChatFormatting.GOLD),
                        Component.literal(Integer.toString(matReq)).withStyle(ChatFormatting.GOLD)
                ));
            }
            case MATERIAL_LCM -> {
                if (input.isEmpty()) {
                    yield List.of();
                }

                FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(
                        screen.getRecipeInput(), true
                );
                yield List.of(Component.translatable(
                        TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM_COUNT,
                        Component.literal(Integer.toString(input.getCount())).withStyle(ChatFormatting.GOLD),
                        Component.literal(Integer.toString(calc.getInputCount())).withStyle(ChatFormatting.GOLD)
                ));
            }
            case OUTPUT_SIZE -> {
                if (input.isEmpty()) {
                    yield List.of();
                }

                FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(
                        screen.getRecipeInput(), true
                );
                int maxSize = recipe.getResult().getMaxStackSize();
                yield List.of(
                        Component.translatable(TOOLTIP_OUTPUT_COUNT, calc.getOutputCount(), maxSize),
                        Component.translatable(TOOLTIP_USE_INTERMEDIATE)
                );
            }
            case MISSING_ADDITIVE_0, MISSING_ADDITIVE_1, MISSING_ADDITIVE_2 -> {
                listAdditives = matchResult.additiveSlot();
                FramingSawRecipeAdditive additive = recipe.getAdditives().get(matchResult.additiveSlot());
                yield List.of(makeHaveButNeedTooltip(TOOLTIP_HAVE_ITEM_NONE, additive, listAdditives, additiveResolver));
            }
            case UNEXPECTED_ADDITIVE_0, UNEXPECTED_ADDITIVE_1, UNEXPECTED_ADDITIVE_2 -> {
                Item itemIn = screen.getAdditiveStack(matchResult.additiveSlot()).getItem();
                yield List.of(Component.translatable(
                        TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM,
                        Component.translatable(itemIn.getDescriptionId()).withStyle(ChatFormatting.GOLD),
                        TOOLTIP_HAVE_ITEM_NONE
                ));
            }
            case INCORRECT_ADDITIVE_0, INCORRECT_ADDITIVE_1, INCORRECT_ADDITIVE_2 -> {
                listAdditives = matchResult.additiveSlot();
                Item itemIn = screen.getAdditiveStack(matchResult.additiveSlot()).getItem();
                yield List.of(makeHaveButNeedTooltip(
                        Component.translatable(itemIn.getDescriptionId()).withStyle(ChatFormatting.GOLD),
                        recipe.getAdditives().get(matchResult.additiveSlot()),
                        listAdditives,
                        additiveResolver
                ));
            }
            case INSUFFICIENT_ADDITIVE_0, INSUFFICIENT_ADDITIVE_1, INSUFFICIENT_ADDITIVE_2 -> {
                if (input.isEmpty()) {
                    yield List.of();
                }

                FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(
                        screen.getRecipeInput(), true
                );
                int cntIn = screen.getAdditiveStack(matchResult.additiveSlot()).getCount();
                int cntReq = calc.getAdditiveCount(matchResult.additiveSlot());
                yield List.of(Component.translatable(
                        TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM_COUNT,
                        Component.literal(Integer.toString(cntIn)).withStyle(ChatFormatting.GOLD),
                        Component.literal(Integer.toString(cntReq)).withStyle(ChatFormatting.GOLD)
                ));
            }
            case SUCCESS -> throw new IllegalStateException("Unreachable");
        };
        for (MutableComponent component : detail) {
            components.add(component.withStyle(ChatFormatting.RED));
        }

        if (listAdditives > -1) {
            appendAdditiveItemOptions(components, recipe, listAdditives, additiveResolver);
        }
        return components;
    }

    private static void appendAdditiveItemOptions(List<Component> components, FramingSawRecipe recipe, int additiveSlot, CachingIngredientResolver.Multi additiveResolver) {
        FramingSawRecipeAdditive additive = recipe.getAdditives().get(additiveSlot);
        List<ItemStack> items = additiveResolver.getStacks(additiveSlot, additive.ingredient());
        if (!additive.isTagBased() && items.size() <= 1) {
            return;
        }

        if (Minecraft.getInstance().hasShiftDown()) {
            for (ItemStack option : items) {
                Component name = option.getItemName();
                components.add(Component.literal("- ").append(name).withStyle(ChatFormatting.GOLD));
            }
        } else {
            Component keyName = InputConstants.getKey(new KeyEvent(InputConstants.KEY_LSHIFT, -1, 0)).getDisplayName();
            components.add(Component.translatable(
                    TOOLTIP_PRESS_TO_SHOW,
                    Component.literal("").append(keyName).withStyle(ChatFormatting.GOLD)
            ).withStyle(ChatFormatting.RED));
        }
    }

    private static MutableComponent makeHaveButNeedTooltip(
            Component present,
            FramingSawRecipeAdditive additive,
            int index,
            CachingIngredientResolver.Multi additiveResolver
    ) {
        if (additive.isTagBased()) {
            return Component.translatable(
                    TOOLTIP_HAVE_X_BUT_NEED_Y_TAG,
                    present,
                    Utils.translateTag(additive.srcTag().orElseThrow()).withStyle(ChatFormatting.GOLD)
            );
        }

        List<ItemStack> options = additiveResolver.getStacks(index, additive.ingredient());
        return Component.translatable(
                options.size() > 1 ? TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM_MULTI : TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM,
                present,
                options.getFirst().getItemName().copy().withStyle(ChatFormatting.GOLD)
        );
    }

    private SawRecipeFailurePrinter() { }
}
