package io.github.xfacthd.framedblocks.client.screen.saw;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.InputConstants;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.compat.ae2.AppliedEnergisticsCompat;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeAdditive;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCalculation;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeMatchResult;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import io.github.xfacthd.framedblocks.common.menu.FramingSawWithEncoderMenu;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundEncodeFramingSawPatternPayload;
import io.github.xfacthd.framedblocks.common.util.ArrayBackedRecipeInput;
import net.minecraft.Optionull;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FramingSawWithEncoderScreen extends FramingSawScreen {
    public static final Component TOOLTIP_TAB_CRAFTING = Utils.translate("tooltip", "framing_saw.mode.crafting");
    public static final Component TOOLTIP_TAB_PATTERN = Utils.translate("tooltip", "framing_saw.mode.pattern_encode");
    private static final Identifier BACKGROUND_ENCODER = Utils.id("textures/gui/framing_saw_encoder.png");
    private static final Identifier TAB_ICON = Utils.id("minecraft", "advancements/tab_left_middle");
    private static final Identifier TAB_SELECTED_ICON = Utils.id("minecraft", "advancements/tab_left_middle_selected");
    private static final WidgetSprites ENCODE_BTN_SPRITES = new WidgetSprites(
            Utils.id("framing_saw/button_encode"),
            Utils.id("framing_saw/button_encode_disabled"),
            Utils.id("framing_saw/button_encode_focused")
    );
    public static final int TAB_WIDTH = 32;
    public static final int TAB_HEIGHT = 28;
    public static final int TAB_X = -28;
    public static final int TAB_TOP_Y = 4;
    private static final int TAB_BOT_Y = TAB_TOP_Y + TAB_HEIGHT;
    private static final int TAB_ICON_X = -18;
    private static final int TAB_ICON_TOP_Y = TAB_TOP_Y + 6;
    private static final int TAB_ICON_BOT_Y = TAB_ICON_TOP_Y + TAB_HEIGHT;
    private static final int ENCODER_RESULT_SLOT_Y = 31;

    private final ItemStack tableStack = new ItemStack(Items.CRAFTING_TABLE);
    private final ItemStack blankPatternStack = AppliedEnergisticsCompat.makeBlankPatternStack();
    private final ItemStack sawPatternStack = AppliedEnergisticsCompat.makeSawPatternStack();
    private final ItemStack[] encodingInputs = new ItemStack[1 + FramingSawRecipe.MAX_ADDITIVE_COUNT];
    private final RecipeInput encodingRecipeInput = new ArrayBackedRecipeInput(encodingInputs);
    @UnknownNullability
    private Button encodeButton = null;
    private boolean encoding = false;
    @Nullable
    private FramingSawRecipeCalculation encoderCalculation = null;
    @Nullable
    private FramingSawRecipeMatchResult encoderMatchResult = null;

    FramingSawWithEncoderScreen(FramingSawMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        Preconditions.checkState(AppliedEnergisticsCompat.isLoaded(), "FramingSawWithEncoderScreen requires AE2, how did we get here???");
        resetEncoderInputs(((FramingSawWithEncoderMenu) menu).isInEncoderMode());
    }

    @Override
    protected void init() {
        super.init();

        encodeButton = addRenderableWidget(new ImageButton(leftPos + 224, topPos + 92, 14, 14, ENCODE_BTN_SPRITES, this::onEncodePressed));
        ((FramingSawWithEncoderMenu) menu).setEncoderModeListener(encoder -> {
            encoding = encoder;
            encodeButton.visible = encoder;
            resetEncoderInputs(encoder);

            if (encoder) {
                updateEncoderCalculation();
            } else {
                encoderCalculation = null;
                encoderMatchResult = null;
            }
        });
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        Identifier rlTop = encoding ? TAB_ICON : TAB_SELECTED_ICON;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, rlTop, leftPos + TAB_X, topPos + TAB_TOP_Y, TAB_WIDTH, TAB_HEIGHT);
        graphics.fakeItem(tableStack, leftPos + TAB_ICON_X, topPos + TAB_ICON_TOP_Y);

        Identifier rlBot = encoding ? TAB_SELECTED_ICON : TAB_ICON;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, rlBot, leftPos + TAB_X, topPos + TAB_BOT_Y, TAB_WIDTH, TAB_HEIGHT);
        graphics.fakeItem(sawPatternStack, leftPos + TAB_ICON_X, topPos + TAB_ICON_BOT_Y);

        if (encoding) {
            FramingSawRecipe recipe = cache.getRecipes().get(menu.getSelectedRecipeIndex()).value();

            ClientUtils.renderTransparentFakeItem(graphics, recipe.getResultStack(), leftPos + 223, topPos + 31);
            int count = Optionull.mapOrDefault(encoderCalculation, FramingSawRecipeCalculation::getOutputCount, 1);
            drawItemCount(graphics, count, leftPos + 223, topPos + 31);

            if (!menu.getSlot(FramingSawWithEncoderMenu.SLOT_PATTERN_INPUT).hasItem()) {
                ClientUtils.renderTransparentFakeItem(graphics, blankPatternStack, leftPos + 223, topPos + 73);
            }
            if (!menu.getSlot(FramingSawWithEncoderMenu.SLOT_PATTERN_OUTPUT).hasItem()) {
                ClientUtils.renderTransparentFakeItem(graphics, sawPatternStack, leftPos + 223, topPos + 109);
            }
        }
    }

    @Override
    protected Identifier getBackground() {
        return encoding ? BACKGROUND_ENCODER : super.getBackground();
    }

    @Override
    public ItemStack getInputStack() {
        return encoding ? encodingInputs[0] : super.getInputStack();
    }

    @Override
    public ItemStack getAdditiveStack(int slot) {
        return encoding ? encodingInputs[slot + 1] : super.getAdditiveStack(slot);
    }

    @Override
    public RecipeInput getRecipeInput() {
        return encoding ? encodingRecipeInput : super.getRecipeInput();
    }

    @Override
    protected void handleRecipeChange() {
        super.handleRecipeChange();
        if (encoding) {
            resetEncoderInputs(true);
            updateEncoderCalculation();
        }
    }

    @Override
    protected boolean drawInputStackHint(GuiGraphicsExtractor graphics, ItemStack input) {
        if (!super.drawInputStackHint(graphics, input) && encoding) {
            graphics.fakeItem(input, leftPos + 20, topPos + 22);
            int count = Optionull.mapOrDefault(encoderCalculation, FramingSawRecipeCalculation::getInputCount, 1);
            drawItemCount(graphics, count, leftPos + 20, topPos + 22);
        }
        return true;
    }

    @Override
    protected boolean drawAdditiveStackHint(GuiGraphicsExtractor graphics, int index, ItemStack additive, List<FramingSawRecipeAdditive> additives, int y) {
        boolean superResult = super.drawAdditiveStackHint(graphics, index, additive, additives, y);
        if (encoding) {
            if (!superResult) {
                graphics.fakeItem(additive, leftPos + 20, y);
            }

            int count = Optionull.mapOrDefault(encoderCalculation, calc -> calc.getAdditiveCount(index), 1);
            drawItemCount(graphics, count, leftPos + 20, y);
        }
        return true;
    }

    @Override
    protected boolean displayRecipeErrors() {
        return !encoding;
    }

    private void drawItemCount(GuiGraphicsExtractor graphics, int count, int x, int y) {
        if (count != 1) {
            String text = String.valueOf(count);
            graphics.text(font, text, x + 19 - 2 - font.width(text), y + 6 + 3, 0xFFFFFFFF, true);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        if (mouseX >= leftPos + TAB_X && mouseX <= leftPos) {
            if (mouseY >= topPos + TAB_TOP_Y && mouseY <= topPos + TAB_BOT_Y) {
                graphics.setTooltipForNextFrame(font, TOOLTIP_TAB_CRAFTING, mouseX, mouseY);
            } else if (mouseY >= topPos + TAB_BOT_Y && mouseY <= topPos + (TAB_BOT_Y + TAB_HEIGHT)) {
                graphics.setTooltipForNextFrame(font, TOOLTIP_TAB_PATTERN, mouseX, mouseY);
            }
        } else if (encodeButton.isMouseOver(mouseX, mouseY) && encoderMatchResult != null && !encoderMatchResult.success()) {
            List<Component> lines = new ArrayList<>();
            FramingSawMenu.FramedRecipeHolder recipe = menu.getRecipes().get(menu.getSelectedRecipeIndex());
            SawRecipeFailurePrinter.appendRecipeFailure(lines, cache, additiveResolver, recipe.getRecipe(), encoderMatchResult, this);
            graphics.setTooltipForNextFrame(font, lines, Optional.empty(), mouseX, mouseY);
        } else if (encoding) {
            for (int i = 0; i <= FramingSawMenu.SLOT_RESULT; i++) {
                Slot slot = menu.getSlot(i);
                int sy = i == FramingSawMenu.SLOT_RESULT ? ENCODER_RESULT_SLOT_Y : slot.y;
                if (isHovering(slot.x, sy, 16, 16, mouseX, mouseY)) {
                    ItemStack stack = switch (i) {
                        case FramingSawMenu.SLOT_INPUT -> getInputStack();
                        case FramingSawMenu.SLOT_RESULT -> menu.getRecipes()
                                .get(menu.getSelectedRecipeIndex())
                                .getRecipe()
                                .getResultStack();
                        default -> getAdditiveStack(i - 1);
                    };
                    if (!stack.isEmpty()) {
                        renderItemTooltip(graphics, mouseX, mouseY, stack, null);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            if (event.x() >= leftPos + TAB_X && event.x() <= leftPos) {
                int value = 0;
                boolean hit = false;
                if (event.y() >= topPos + TAB_TOP_Y && event.y() <= topPos + TAB_BOT_Y) {
                    value = FramingSawWithEncoderMenu.MENU_BUTTON_MODE_CRAFTING;
                    hit = true;
                } else if (event.y() >= topPos + TAB_BOT_Y && event.y() <= topPos + (TAB_BOT_Y + TAB_HEIGHT)) {
                    value = FramingSawWithEncoderMenu.MENU_BUTTON_MODE_ENCODING;
                    hit = true;
                }
                if (hit) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
                    //noinspection ConstantConditions
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, value);
                    return true;
                }
            } else if (encoding) {
                ItemStack carried = menu.getCarried();
                FramingSawRecipe recipe = cache.getRecipes().get(menu.getSelectedRecipeIndex()).value();
                for (int i = 0; i < 1 + recipe.getAdditives().size(); i++) {
                    Slot slot = menu.getSlot(i);
                    if (isHovering(slot.x, slot.y, 16, 16, event.x(), event.y())) {
                        if (isValidEncodingInput(recipe, i, carried)) {
                            acceptEncodingInput(i, carried.copyWithCount(1));
                        }
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    private boolean isValidEncodingInput(FramingSawRecipe recipe, int slot, ItemStack stack) {
        if (slot == 0) {
            return cache.getMaterialValue(stack.getItem()) > 0;
        }
        return recipe.getAdditives().get(slot - 1).ingredient().test(stack);
    }

    public void acceptEncodingInput(int slot, ItemStack stack) {
        encodingInputs[slot] = stack;
        if (slot == 0) {
            if (stack.isEmpty()) {
                encodingInputs[0] = cubeStack.copyWithCount(1);
            }
            updateEncoderCalculation();
        }
    }

    private void resetEncoderInputs(boolean encoding) {
        Arrays.fill(encodingInputs, ItemStack.EMPTY);
        if (encoding) {
            encodingInputs[0] = cubeStack.copyWithCount(1);
            List<FramingSawRecipeAdditive> additives = cache.getRecipes().get(menu.getSelectedRecipeIndex()).value().getAdditives();
            for (int i = 0; i < additives.size(); i++) {
                encodingInputs[i + 1] = additiveResolver.getFirstStack(i, additives.get(i).ingredient()).copy();
            }
        }
    }

    private void updateEncoderCalculation() {
        FramingSawRecipe recipe = cache.getRecipes().get(menu.getSelectedRecipeIndex()).value();
        encoderCalculation = recipe.makeCraftingCalculation(encodingRecipeInput, true);
        encodingInputs[0].setCount(encoderCalculation.getInputCount());
        for (int i = 0; i < recipe.getAdditives().size(); i++) {
            encodingInputs[i + 1].setCount(encoderCalculation.getAdditiveCount(i));
        }
        //noinspection ConstantConditions
        encoderMatchResult = recipe.matchWithResult(encodingRecipeInput, minecraft.level);
    }

    private void onEncodePressed(Button btn) {
        if (encoderMatchResult == null || !encoderMatchResult.success()) {
            return;
        }

        ClientPacketDistributor.sendToServer(new ServerboundEncodeFramingSawPatternPayload(
                menu.containerId,
                cache.getRecipes().get(menu.getSelectedRecipeIndex()).id(),
                Arrays.stream(encodingInputs).filter(stack -> !stack.isEmpty()).toArray(ItemStack[]::new)
        ));
    }

    public int getInputSlotX() {
        return leftPos + menu.getSlot(FramingSawMenu.SLOT_INPUT).x;
    }

    public int getInputSlotY(int slot) {
        return topPos + menu.getSlot(slot).y;
    }

    @Override
    public FramingSawWithEncoderMenu getMenu() {
        return (FramingSawWithEncoderMenu) super.getMenu();
    }
}
