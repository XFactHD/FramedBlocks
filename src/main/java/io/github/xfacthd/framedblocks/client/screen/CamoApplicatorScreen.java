package io.github.xfacthd.framedblocks.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.screen.widget.IndicatorButton;
import io.github.xfacthd.framedblocks.common.item.applicator.CamoApplicatorConfig;
import io.github.xfacthd.framedblocks.common.item.applicator.CamoApplicatorContent;
import io.github.xfacthd.framedblocks.common.menu.CamoApplicatorMenu;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundCamoApplicatorConfigureModifierPayload;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundCamoApplicatorSetModePayload;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundCamoApplicatorSetSlotPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.UnknownNullability;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CamoApplicatorScreen extends AbstractContainerScreen<CamoApplicatorMenu> {
    private static final Identifier BACKGROUND = Utils.id("textures/gui/camo_applicator.png");
    private static final Identifier SLOT_SELECTION = Utils.id("camo_applicator/selection");
    private static final Identifier CONFIG_BUTTON = Utils.id("camo_applicator/config_button");
    private static final Identifier CONFIG_TAB = Utils.id("camo_applicator/config_tab");
    private static final FrameModifier[] MODIFIERS = FrameModifier.values();
    private static final int WIDTH = 176;
    private static final int HEIGHT = 195;
    public static final int CFG_TAB_X = WIDTH - 4;
    public static final int CFG_TAB_Y = 0;
    public static final int CFG_TAB_WIDTH_CLOSED = 28;
    public static final int CFG_TAB_HEIGHT_CLOSED = 28;
    public static final int CFG_TAB_WIDTH_OPEN = 105;
    public static final int CFG_TAB_HEIGHT_OPEN = 145;
    private static final int CFG_BTN_X = WIDTH - 2;
    private static final int CFG_BTN_Y = 2;
    private static final int CFG_BTN_SIZE = 24;
    private static final int CFG_LABEL_X = CFG_BTN_X + CFG_BTN_SIZE + 2;
    private static final int CFG_LABEL_Y = CFG_BTN_Y + (CFG_BTN_SIZE / 2) - 4;
    private static final int CFG_CONTENT_X = WIDTH + 2;
    private static final int MODE_LABEL_Y = 32;
    private static final int MOD_LABEL_Y = 68;
    private static final int MODE_BTN_Y = 41;
    private static final int MODE_BTN_WIDTH = 90;
    private static final int MOD_BTN_TOP_Y = 77;
    private static final int MOD_BTN_DIST = 16;
    private static final int MOD_BAR_WIDTH = 6;
    private static final int MOD_BAR_HEIGHT = 64;
    private static final int MOD_BAR_DIST = 13;
    private static final int MOD_BAR_LAST_X = 139;
    private static final int MOD_BAR_X = MOD_BAR_LAST_X - (MODIFIERS.length - 1) * MOD_BAR_DIST;
    private static final int MOD_BAR_Y = 26;
    public static final Component MODE_BTN_TITLE = Utils.translate("btn", "camo_applicator.mode");
    public static final String TOOLTIP_SELECT_SLOT = Utils.translationKey("tooltip", "camo_applicator.select");
    public static final String TOOLTIP_MODIFIER_AMOUNT = Utils.translationKey("tooltip", "camo_applicator.modifier.amount");
    public static final String TOOLTIP_MODIFIER_ACTIVE = Utils.translationKey("tooltip", "camo_applicator.modifier.active");
    public static final Component LABEL_CFG_HEADER = Utils.translate("label", "camo_applicator.config");
    public static final Component LABEL_CFG_MODE = Utils.translate("label", "camo_applicator.config.mode");
    public static final Component LABEL_CFG_MODIFIERS = Utils.translate("label", "camo_applicator.config.modifiers");
    public static final ModifierSpec[] MODIFIER_SPECS = Util.make(() -> {
        ModifierSpec[] specs = new ModifierSpec[MODIFIERS.length];
        for (FrameModifier modifier : MODIFIERS) {
            specs[modifier.ordinal()] = ModifierSpec.of(modifier);
        }
        return specs;
    });
    private static final InputConstants.Key KEY_SELECT_SLOT = InputConstants.getKey(new KeyEvent(GLFW.GLFW_KEY_S, -1, 0));

    @UnknownNullability
    private CycleButton<CamoApplicatorConfig.Mode> modeCycleButton;
    private final IndicatorButton[] modCheckboxes = new IndicatorButton[MODIFIERS.length];
    private boolean configOpen = false;

    public CamoApplicatorScreen(CamoApplicatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, WIDTH, HEIGHT);
        this.inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        modeCycleButton = addRenderableWidget(
                new CycleButton.Builder<>(CamoApplicatorConfig.Mode::getTranslation, menu::getMode)
                        .withValues(CamoApplicatorConfig.Mode.values())
                        .withTooltip(CamoApplicatorScreen::getModeTooltip)
                        .displayOnlyValue()
                        .create(leftPos + CFG_CONTENT_X, topPos + MODE_BTN_Y, MODE_BTN_WIDTH, Button.DEFAULT_HEIGHT, MODE_BTN_TITLE, this::setMode)
        );
        modeCycleButton.visible = configOpen;
        for (FrameModifier modifier : MODIFIERS) {
            int i = modifier.ordinal();
            modCheckboxes[i] = addRenderableWidget(
                    new IndicatorButton(
                            leftPos + CFG_CONTENT_X,
                            topPos + MOD_BTN_TOP_Y + MOD_BTN_DIST * i,
                            MODIFIER_SPECS[i].tooltip.copy().setStyle(Style.EMPTY.withShadowColor(0)).withColor(0xFF404040),
                            menu.isModifierActive(modifier),
                            state -> configureModifier(modifier, state)
                    )
            );
            modCheckboxes[i].visible = configOpen;
        }
    }

    @Override
    protected void repositionElements() {
        modeCycleButton.setPosition(leftPos + CFG_CONTENT_X, topPos + MODE_BTN_Y);
        for (int i = 0; i < modCheckboxes.length; i++) {
            modCheckboxes[i].setPosition(
                    leftPos + CFG_CONTENT_X,
                    topPos + MOD_BTN_TOP_Y + MOD_BTN_DIST * i
            );
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        for (FrameModifier modifier : MODIFIERS) {
            float value = menu.getModifierStack(modifier);
            int height = Mth.ceil(value / CamoApplicatorContent.MODIFIER_MAX_STACK_SIZE * MOD_BAR_HEIGHT);
            int x = leftPos + MOD_BAR_X + MOD_BAR_DIST * modifier.ordinal();
            int botY = topPos + MOD_BAR_Y + MOD_BAR_HEIGHT;
            ModifierSpec spec = MODIFIER_SPECS[modifier.ordinal()];

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, spec.barTexture, MOD_BAR_WIDTH, MOD_BAR_HEIGHT, 0, MOD_BAR_HEIGHT - height, x, botY - height, MOD_BAR_WIDTH, height);

            graphics.pose().pushMatrix();
            graphics.pose().translate(x - 1, botY + 1);
            graphics.pose().scale(.5F);
            graphics.fakeItem(spec.dummyStack(), 0, 0);
            graphics.pose().popMatrix();
        }

        int configWidth = configOpen ? CFG_TAB_WIDTH_OPEN : CFG_TAB_WIDTH_CLOSED;
        int configHeight = configOpen ? CFG_TAB_HEIGHT_OPEN : CFG_TAB_HEIGHT_CLOSED;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CONFIG_TAB, leftPos + CFG_TAB_X, topPos + CFG_TAB_Y, configWidth, configHeight);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CONFIG_BUTTON, leftPos + CFG_BTN_X, topPos + CFG_BTN_Y, CFG_BTN_SIZE, CFG_BTN_SIZE);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        int slot = menu.getSelectedSlot();
        int x = CamoApplicatorMenu.CAMO_INV_X + (slot % CamoApplicatorMenu.CAMO_INV_ROWS_COLS) * AbstractContainerMenu.SLOT_SIZE;
        int y = CamoApplicatorMenu.CAMO_INV_Y + (slot / CamoApplicatorMenu.CAMO_INV_ROWS_COLS) * AbstractContainerMenu.SLOT_SIZE;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SELECTION, x - 2, y - 2, 20, 20, 0xFFFF6666);

        if (configOpen) {
            graphics.text(font, LABEL_CFG_HEADER, CFG_LABEL_X, CFG_LABEL_Y, 0xFF404040, false);
            graphics.text(font, LABEL_CFG_MODE, CFG_CONTENT_X, MODE_LABEL_Y, 0xFF404040, false);
            graphics.text(font, LABEL_CFG_MODIFIERS, CFG_CONTENT_X, MOD_LABEL_Y, 0xFF404040, false);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int barTop = topPos + MOD_BAR_Y;
        if (mouseY >= barTop && mouseY < barTop + MOD_BAR_HEIGHT) {
            int relX = mouseX - (leftPos + MOD_BAR_X);
            int barIdx = relX / MOD_BAR_DIST;
            if (relX >= 0 && barIdx >= 0 && barIdx < MODIFIERS.length && relX % MOD_BAR_DIST < MOD_BAR_WIDTH) {
                FrameModifier modifier = MODIFIERS[barIdx];
                int amount = menu.getModifierStack(modifier);
                boolean active = menu.isModifierActive(modifier);

                List<Component> lines = List.of(
                        MODIFIER_SPECS[barIdx].tooltip,
                        Component.translatable(TOOLTIP_MODIFIER_AMOUNT, amount, CamoApplicatorContent.MODIFIER_MAX_STACK_SIZE),
                        Component.translatable(TOOLTIP_MODIFIER_ACTIVE, active ? CamoApplicatorConfig.TRUE : CamoApplicatorConfig.FALSE)
                );
                graphics.setTooltipForNextFrame(font, lines, Optional.empty(), mouseX, mouseY);
            }
        }

        if (hoveredSlot == null) {
            return;
        }

        List<Component> tooltip = List.of();
        Optional<TooltipComponent> tooltipImage = Optional.empty();
        ItemStack tooltipStack = ItemStack.EMPTY;
        Identifier tooltipStyle = null;
        if (hoveredSlot.hasItem()) {
            ItemStack stack = hoveredSlot.getItem();
            if (!menu.getCarried().isEmpty() && !showTooltipWithItemInHand(stack)) {
                return;
            }

            tooltip = getTooltipFromContainerItem(stack);
            tooltipImage = stack.getTooltipImage();
            tooltipStack = stack;
            tooltipStyle = stack.get(DataComponents.TOOLTIP_STYLE);
        } else if (!menu.getCarried().isEmpty()) {
            return;
        }

        if (hoveredSlot.index < CamoApplicatorContent.CAMO_COUNT && hoveredSlot.index != menu.getSelectedSlot()) {
            tooltip = new ArrayList<>(tooltip);
            if (!tooltip.isEmpty()) {
                tooltip.add(Component.empty());
            }

            Component keyName = KEY_SELECT_SLOT.getDisplayName();
            tooltip.add(Component.translatable(
                    TOOLTIP_SELECT_SLOT,
                    Component.literal("").append(keyName).withStyle(ChatFormatting.GOLD)
            ).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }

        if (!tooltip.isEmpty()) {
            graphics.setTooltipForNextFrame(font, tooltip, tooltipImage, tooltipStack, mouseX, mouseY, tooltipStyle);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        int minX = leftPos + CFG_BTN_X;
        int minY = topPos + CFG_BTN_Y;
        if (event.x() >= minX && event.x() < minX + CFG_BTN_SIZE && event.y() >= minY && event.y() < minY + CFG_BTN_SIZE) {
            toggleConfigTab();
            Button.playButtonClickSound(Objects.requireNonNull(minecraft).getSoundManager());
            return true;
        }
        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (hoveredSlot != null && hoveredSlot.index < CamoApplicatorContent.CAMO_COUNT && event.key() == GLFW.GLFW_KEY_S) {
            setSelectedSlot(hoveredSlot.index);
            return true;
        }
        return super.keyPressed(event);
    }

    public boolean isConfigOpen() {
        return configOpen;
    }

    private static Tooltip getModeTooltip(CamoApplicatorConfig.Mode mode) {
        return Tooltip.create(mode.getTooltip());
    }

    private void toggleConfigTab() {
        configOpen = !configOpen;
        modeCycleButton.visible = configOpen;
        for (int i = 0; i < MODIFIERS.length; i++) {
            modCheckboxes[i].visible = configOpen;
        }
    }

    private void setMode(CycleButton<CamoApplicatorConfig.Mode> button, CamoApplicatorConfig.Mode mode) {
        ClientPacketDistributor.sendToServer(new ServerboundCamoApplicatorSetModePayload(menu.containerId, mode));
    }

    private void configureModifier(FrameModifier modifier, boolean active) {
        ClientPacketDistributor.sendToServer(new ServerboundCamoApplicatorConfigureModifierPayload(menu.containerId, modifier, active));
    }

    private void setSelectedSlot(int slot) {
        ClientPacketDistributor.sendToServer(new ServerboundCamoApplicatorSetSlotPayload(menu.containerId, slot));
    }

    public record ModifierSpec(Identifier barTexture, Component tooltip, Lazy<ItemStack> lazyDummyStack) {
        static ModifierSpec of(FrameModifier modifier) {
            Identifier barTexture = Utils.id("camo_applicator/modifier_" + modifier.getSerializedName());
            Component tooltip = Utils.translate("tooltip", "camo_applicator.modifier.type." + modifier.getSerializedName());
            Lazy<ItemStack> lazyDummyStack = Lazy.of(modifier::getDefaultStack);
            return new ModifierSpec(barTexture, tooltip, lazyDummyStack);
        }

        public ItemStack dummyStack() {
            return lazyDummyStack.get();
        }
    }
}
