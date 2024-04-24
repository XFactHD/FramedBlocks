package xfacthd.framedblocks.client.screen;

import com.google.common.base.Preconditions;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.compat.ae2.AppliedEnergisticsCompat;
import xfacthd.framedblocks.common.crafting.*;
import xfacthd.framedblocks.common.menu.FramingSawMenu;
import xfacthd.framedblocks.common.menu.FramingSawWithEncoderMenu;
import xfacthd.framedblocks.common.net.payload.EncodeFramingSawPatternPayload;

import java.util.*;

public class FramingSawWithEncoderScreen extends FramingSawScreen
{
    public static final Component TOOLTIP_TAB_CRAFTING = Utils.translate("tooltip", "framing_saw.mode.crafting");
    public static final Component TOOLTIP_TAB_PATTERN = Utils.translate("tooltip", "framing_saw.mode.pattern_encode");
    private static final ResourceLocation BACKGROUND_ENCODER = Utils.rl("textures/gui/framing_saw_encoder.png");
    private static final ResourceLocation TAB_ICON = new ResourceLocation("advancements/tab_left_middle");
    private static final ResourceLocation TAB_SELECTED_ICON = new ResourceLocation("advancements/tab_left_middle_selected");
    private static final WidgetSprites ENCODE_BTN_SPRITES = new WidgetSprites(
            Utils.rl("button_encode"),
            Utils.rl("button_encode_disabled"),
            Utils.rl("button_encode_focused")
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
    private final Container encodingInputContainer = new SimpleContainer(encodingInputs);
    private Button encodeButton = null;
    private boolean encoding = false;
    private FramingSawRecipeCalculation encoderCalculation = null;
    private FramingSawRecipeMatchResult encoderMatchResult = null;

    FramingSawWithEncoderScreen(FramingSawMenu menu, Inventory inv, Component title)
    {
        super(menu, inv, title);
        Preconditions.checkState(AppliedEnergisticsCompat.isLoaded(), "FramingSawWithEncoderScreen requires AE2, how did we get here???");
        resetEncoderInputs(((FramingSawWithEncoderMenu) menu).isInEncoderMode());
    }

    @Override
    protected void init()
    {
        super.init();

        encodeButton = addRenderableWidget(new ImageButton(leftPos + 224, topPos + 92, 14, 14, ENCODE_BTN_SPRITES, this::onEncodePressed));
        ((FramingSawWithEncoderMenu) menu).setEncoderModeListener(encoder ->
        {
            encoding = encoder;
            encodeButton.visible = encoder;
            resetEncoderInputs(encoder);

            if (encoder)
            {
                updateEncoderCalculation();
            }
            else
            {
                encoderCalculation = null;
                encoderMatchResult = null;
            }
        });
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTick, mouseX, mouseY);

        ResourceLocation rlTop = encoding ? TAB_ICON : TAB_SELECTED_ICON;
        graphics.blitSprite(rlTop, leftPos + TAB_X, topPos + TAB_TOP_Y, TAB_WIDTH, TAB_HEIGHT);
        graphics.renderFakeItem(tableStack, leftPos + TAB_ICON_X, topPos + TAB_ICON_TOP_Y);

        ResourceLocation rlBot = encoding ? TAB_SELECTED_ICON : TAB_ICON;
        graphics.blitSprite(rlBot, leftPos + TAB_X, topPos + TAB_BOT_Y, TAB_WIDTH, TAB_HEIGHT);
        graphics.renderFakeItem(sawPatternStack, leftPos + TAB_ICON_X, topPos + TAB_ICON_BOT_Y);

        if (encoding)
        {
            FramingSawRecipe recipe = cache.getRecipes().get(menu.getSelectedRecipeIndex()).value();

            ClientUtils.renderTransparentFakeItem(graphics, recipe.getResult(), leftPos + 223, topPos + 31);
            int count = Optionull.mapOrDefault(encoderCalculation, FramingSawRecipeCalculation::getOutputCount, 1);
            drawItemCount(graphics, count, leftPos + 223, topPos + 31);

            if (!menu.getSlot(FramingSawWithEncoderMenu.SLOT_PATTERN_INPUT).hasItem())
            {
                ClientUtils.renderTransparentFakeItem(graphics, blankPatternStack, leftPos + 223, topPos + 73);
            }
            if (!menu.getSlot(FramingSawWithEncoderMenu.SLOT_PATTERN_OUTPUT).hasItem())
            {
                ClientUtils.renderTransparentFakeItem(graphics, sawPatternStack, leftPos + 223, topPos + 109);
            }
        }
    }

    @Override
    protected ResourceLocation getBackground()
    {
        return encoding ? BACKGROUND_ENCODER : super.getBackground();
    }

    @Override
    public ItemStack getInputStack()
    {
        return encoding ? encodingInputs[0] : super.getInputStack();
    }

    @Override
    public ItemStack getAdditiveStack(int slot)
    {
        return encoding ? encodingInputs[slot + 1] : super.getAdditiveStack(slot);
    }

    @Override
    public Container getInputContainer()
    {
        return encoding ? encodingInputContainer : super.getInputContainer();
    }

    @Override
    protected void handleRecipeChange()
    {
        super.handleRecipeChange();
        if (encoding)
        {
            resetEncoderInputs(true);
            updateEncoderCalculation();
        }
    }

    @Override
    protected boolean drawInputStackHint(GuiGraphics graphics, ItemStack input)
    {
        if (!super.drawInputStackHint(graphics, input) && encoding)
        {
            graphics.renderFakeItem(input, leftPos + 20, topPos + 28);
            int count = Optionull.mapOrDefault(encoderCalculation, FramingSawRecipeCalculation::getInputCount, 1);
            drawItemCount(graphics, count, leftPos + 20, topPos + 28);
        }
        return true;
    }

    @Override
    protected boolean drawAdditiveStackHint(GuiGraphics graphics, int index, ItemStack additive, List<FramingSawRecipeAdditive> additives, int y)
    {
        boolean superResult = super.drawAdditiveStackHint(graphics, index, additive, additives, y);
        if (encoding)
        {
            if (!superResult)
            {
                graphics.renderFakeItem(additive, leftPos + 20, y);
            }

            int count = Optionull.mapOrDefault(encoderCalculation, calc -> calc.getAdditiveCount(index), 1);
            drawItemCount(graphics, count, leftPos + 20, y);
        }
        return true;
    }

    @Override
    protected boolean displayRecipeErrors()
    {
        return !encoding;
    }

    private void drawItemCount(GuiGraphics graphics, int count, int x, int y)
    {
        if (count != 1)
        {
            graphics.pose().pushPose();
            graphics.pose().translate(0F, 0F, 200F);

            String text = String.valueOf(count);
            graphics.drawString(font, text, x + 19 - 2 - font.width(text), y + 6 + 3, 16777215, true);

            graphics.pose().popPose();
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);

        if (mouseX >= leftPos + TAB_X && mouseX <= leftPos)
        {
            if (mouseY >= topPos + TAB_TOP_Y && mouseY <= topPos + TAB_BOT_Y)
            {
                graphics.renderTooltip(font, TOOLTIP_TAB_CRAFTING, mouseX, mouseY);
            }
            else if (mouseY >= topPos + TAB_BOT_Y && mouseY <= topPos + (TAB_BOT_Y + TAB_HEIGHT))
            {
                graphics.renderTooltip(font, TOOLTIP_TAB_PATTERN, mouseX, mouseY);
            }
        }
        else if (encodeButton.isMouseOver(mouseX, mouseY) && encoderMatchResult != null && !encoderMatchResult.success())
        {
            List<Component> lines = new ArrayList<>();
            FramingSawMenu.FramedRecipeHolder recipe = menu.getRecipes().get(menu.getSelectedRecipeIndex());
            appendRecipeFailure(lines, cache, recipe.getRecipe(), encoderMatchResult, this);
            graphics.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
        }
        else if (encoding)
        {
            for (int i = 0; i <= FramingSawMenu.SLOT_RESULT; i++)
            {
                Slot slot = menu.getSlot(i);
                int sy = i == FramingSawMenu.SLOT_RESULT ? ENCODER_RESULT_SLOT_Y : slot.y;
                if (isHovering(slot.x, sy, 16, 16, mouseX, mouseY))
                {
                    ItemStack stack = switch (i)
                    {
                        case FramingSawMenu.SLOT_INPUT -> getInputStack();
                        case FramingSawMenu.SLOT_RESULT -> menu.getRecipes()
                                .get(menu.getSelectedRecipeIndex())
                                .getRecipe()
                                .getResult();
                        default -> getAdditiveStack(i - 1);
                    };
                    if (!stack.isEmpty())
                    {
                        renderItemTooltip(graphics, mouseX, mouseY, stack, null);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1)
        {
            if (mouseX >= leftPos + TAB_X && mouseX <= leftPos)
            {
                int value = 0;
                boolean hit = false;
                if (mouseY >= topPos + TAB_TOP_Y && mouseY <= topPos + TAB_BOT_Y)
                {
                    value = FramingSawWithEncoderMenu.MENU_BUTTON_MODE_CRAFTING;
                    hit = true;
                }
                else if (mouseY >= topPos + TAB_BOT_Y && mouseY <= topPos + (TAB_BOT_Y + TAB_HEIGHT))
                {
                    value = FramingSawWithEncoderMenu.MENU_BUTTON_MODE_ENCODING;
                    hit = true;
                }
                if (hit)
                {
                    Minecraft.getInstance().getSoundManager()
                            .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
                    //noinspection ConstantConditions
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, value);
                    return true;
                }
            }
            else if (encoding)
            {
                ItemStack carried = menu.getCarried();
                FramingSawRecipe recipe = cache.getRecipes().get(menu.getSelectedRecipeIndex()).value();
                for (int i = 0; i < 1 + recipe.getAdditives().size(); i++)
                {
                    Slot slot = menu.getSlot(i);
                    if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY))
                    {
                        if (i == 0 || recipe.getAdditives().get(i - 1).ingredient().test(carried))
                        {
                            acceptEncodingInput(i, carried.copyWithCount(1));
                        }
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void acceptEncodingInput(int slot, ItemStack stack)
    {
        encodingInputs[slot] = stack;
        if (slot == 0)
        {
            if (stack.isEmpty())
            {
                encodingInputs[0] = cubeStack.copyWithCount(1);
            }
            updateEncoderCalculation();
        }
    }

    private void resetEncoderInputs(boolean encoding)
    {
        Arrays.fill(encodingInputs, ItemStack.EMPTY);
        if (encoding)
        {
            encodingInputs[0] = cubeStack.copyWithCount(1);
            List<FramingSawRecipeAdditive> additives = cache.getRecipes().get(menu.getSelectedRecipeIndex()).value().getAdditives();
            for (int i = 0; i < additives.size(); i++)
            {
                encodingInputs[i + 1] = additives.get(i).ingredient().getItems()[0].copyWithCount(1);
            }
        }
    }

    private void updateEncoderCalculation()
    {
        FramingSawRecipe recipe = cache.getRecipes().get(menu.getSelectedRecipeIndex()).value();
        encoderCalculation = recipe.makeCraftingCalculation(encodingInputContainer, true);
        encodingInputs[0].setCount(encoderCalculation.getInputCount());
        for (int i = 0; i < recipe.getAdditives().size(); i++)
        {
            encodingInputs[i + 1].setCount(encoderCalculation.getAdditiveCount(i));
        }
        //noinspection ConstantConditions
        encoderMatchResult = recipe.matchWithResult(encodingInputContainer, minecraft.level);
    }

    private void onEncodePressed(Button btn)
    {
        if (encoderMatchResult == null || !encoderMatchResult.success()) return;

        PacketDistributor.sendToServer(new EncodeFramingSawPatternPayload(
                menu.containerId,
                cache.getRecipes().get(menu.getSelectedRecipeIndex()).id(),
                encodingInputs
        ));
    }

    public int getInputSlotX()
    {
        return leftPos + menu.getSlot(FramingSawMenu.SLOT_INPUT).x;
    }

    public int getInputSlotY(int slot)
    {
        return topPos + menu.getSlot(slot).y;
    }

    @Override
    public FramingSawWithEncoderMenu getMenu()
    {
        return (FramingSawWithEncoderMenu) super.getMenu();
    }
}
