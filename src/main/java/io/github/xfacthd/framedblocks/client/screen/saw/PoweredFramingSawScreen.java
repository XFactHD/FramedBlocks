package io.github.xfacthd.framedblocks.client.screen.saw;

import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeAdditive;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeMatchResult;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import io.github.xfacthd.framedblocks.common.menu.PoweredFramingSawMenu;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundSelectFramingSawRecipePayload;
import io.github.xfacthd.framedblocks.common.util.CachingIngredientResolver;
import net.minecraft.Optionull;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PoweredFramingSawScreen extends AbstractContainerScreen<PoweredFramingSawMenu> implements IFramingSawScreen {
    private static final Identifier BACKGROUND = Utils.id("textures/gui/powered_framing_saw.png");
    public static final Component TITLE_TARGETBLOCK = Utils.translate("title", "powered_saw.target_block");
    public static final MutableComponent MSG_STATUS = Utils.translate("msg", "powered_saw.status");
    public static final Component MSG_STATUS_NO_RECIPE = Utils.translate("msg", "powered_saw.status.no_recipe")
            .withStyle(Style.EMPTY.withColor(0xDD7700));
    public static final Component MSG_STATUS_NO_MATCH = Utils.translate("msg", "powered_saw.status.no_match")
            .withStyle(Style.EMPTY.withColor(0xDD0000));
    public static final Component MSG_STATUS_READY = Utils.translate("msg", "powered_saw.status.ready")
            .withStyle(Style.EMPTY.withColor(0x00DD00));
    public static final Component TOOLTIP_STATUS_NO_RECIPE = Utils.translate("tooltip", "powered_saw.status.no_recipe");
    public static final String TOOLTIP_ENERGY = Utils.translationKey("tooltip", "powered_saw.energy");
    private static final int TITLE_TARGETBLOCK_X = 88;
    private static final int TITLE_TARGETBLOCK_Y = 24;
    private static final int TARGET_STACK_X = 92;
    private static final int TARGET_STACK_Y = 20;
    private static final int STATUS_X = 8;
    private static final int STATUS_Y = 62;
    private static final int ENERGY_X = 8;
    private static final int ENERGY_Y = 18;
    private static final int ENERGY_WIDTH = 14;
    private static final int ENERGY_HEIGHT = 48;
    private static final int ENERGY_U = 176;
    private static final int ENERGY_V = 16;
    public static final int PROGRESS_X = 115;
    public static final int PROGRESS_Y = 46;
    public static final int PROGRESS_WIDTH = 22;
    public static final int PROGRESS_HEIGHT = 16;
    private static final int PROGRESS_U = 176;
    private static final int PROGRESS_V = 0;
    private static final int CROSS_SIZE = 16;
    private static final int CROSS_U = 176;
    private static final int CROSS_V = 64;
    private static final Rect2i EMPTY = new Rect2i(0, 0, 0, 0);

    private final FramingSawRecipeCache cache = FramingSawRecipeCache.get(true);
    private final ItemStack cubeStack = new ItemStack(FBContent.BLOCK_FRAMED_CUBE.value());
    private final CachingIngredientResolver.Multi additiveResolver;
    private int targetStackX;
    private int targetStackY;
    private Rect2i statusTooltipArea = EMPTY;
    private List<Component> statusTooltip = List.of();

    public PoweredFramingSawScreen(PoweredFramingSawMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, DEFAULT_IMAGE_WIDTH, 182);
        this.inventoryLabelY = imageHeight - 94;
        Level level = Objects.requireNonNull(minecraft.level);
        this.additiveResolver = new CachingIngredientResolver.Multi(level, FramingSawRecipe.MAX_ADDITIVE_COUNT);
    }

    @Override
    protected void init() {
        super.init();

        targetStackX = leftPos + TARGET_STACK_X;
        targetStackY = topPos + TARGET_STACK_Y;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        int tx = leftPos + TITLE_TARGETBLOCK_X - font.width(TITLE_TARGETBLOCK);
        int ty = topPos + TITLE_TARGETBLOCK_Y;
        graphics.text(font, TITLE_TARGETBLOCK, tx, ty, 0xFF404040, false);

        FramingSawRecipe recipe = Optionull.map(menu.getSelectedRecipe(), RecipeHolder::value);
        FramingSawRecipeMatchResult match = recipe != null ? menu.getMatchResult() : null;
        drawRecipeInfo(graphics, recipe, match);
        drawStatus(graphics, recipe, match);

        drawEnergyBar(graphics, mouseX, mouseY);
    }

    private void drawRecipeInfo(GuiGraphicsExtractor graphics, @Nullable FramingSawRecipe recipe, @Nullable FramingSawRecipeMatchResult match) {
        Slot inputSlot = menu.getSlot(FramingSawMenu.SLOT_INPUT);
        if (!inputSlot.hasItem()) {
            int ix = leftPos + inputSlot.x;
            int iy = topPos + inputSlot.y;
            ClientUtils.renderTransparentFakeItem(graphics, cubeStack, ix, iy);
        }

        if (recipe != null) {
            ItemStack result = recipe.getResultStack();
            ClientUtils.renderTransparentFakeItem(graphics, result, targetStackX, targetStackY);

            List<FramingSawRecipeAdditive> additives = recipe.getAdditives();
            for (int i = 0; i < FramingSawRecipe.MAX_ADDITIVE_COUNT; i++) {
                Slot additiveSlot = menu.getSlot(FramingSawMenu.SLOT_ADDITIVE_FIRST + i);
                if (i >= additives.size()) {
                    int ax = leftPos + additiveSlot.x;
                    int ay = topPos + additiveSlot.y;
                    graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, ax, ay, CROSS_U, CROSS_V, CROSS_SIZE, CROSS_SIZE, 256, 256);
                } else if (!additiveSlot.hasItem()) {
                    List<ItemStack> items = additiveResolver.getStacks(i, additives.get(i).ingredient());
                    int t = (int) (System.currentTimeMillis() / 1700) % items.size();
                    int ax = leftPos + additiveSlot.x;
                    int ay = topPos + additiveSlot.y;
                    ClientUtils.renderTransparentFakeItem(graphics, items.get(t), ax, ay);
                }
            }

            if (match != null && match.success()) {
                float progress = (float) menu.getProgress() / (float) menu.getCraftingDuration();
                if (progress > 0F) {
                    int width = Math.round(PROGRESS_WIDTH * progress);
                    graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + PROGRESS_X, topPos + PROGRESS_Y, PROGRESS_U, PROGRESS_V, width, PROGRESS_HEIGHT, 256, 256);
                }
            }
        }
    }

    private void drawStatus(GuiGraphicsExtractor graphics, @Nullable FramingSawRecipe recipe, @Nullable FramingSawRecipeMatchResult match) {
        MutableComponent status = MSG_STATUS.copy();
        int width = -1;
        if (recipe == null) {
            status.append(MSG_STATUS_NO_RECIPE);
            statusTooltip = List.of(TOOLTIP_STATUS_NO_RECIPE);
            width = font.width(MSG_STATUS_NO_RECIPE);
        } else if (match != null && !match.success()) {
            status.append(MSG_STATUS_NO_MATCH);
            statusTooltip = SawRecipeFailurePrinter.appendRecipeFailure(new ArrayList<>(), cache, additiveResolver, recipe, match, this);
            width = font.width(MSG_STATUS_NO_MATCH);
        } else {
            status.append(MSG_STATUS_READY);
        }
        int sx = leftPos + STATUS_X;
        int sy = topPos + STATUS_Y + font.lineHeight;
        graphics.text(font, status, sx, sy, 0xFF404040, false);
        statusTooltipArea = width == -1 ? EMPTY : new Rect2i(sx + font.width(MSG_STATUS), sy, width, font.lineHeight);
    }

    private void drawEnergyBar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        float energy = (float) menu.getEnergy() / (float) menu.getEnergyCapacity();
        int height = (int) (energy * ENERGY_HEIGHT);
        int y = topPos + ENERGY_Y + (ENERGY_HEIGHT - height);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + ENERGY_X, y, ENERGY_U, ENERGY_V + (ENERGY_HEIGHT - height), ENERGY_WIDTH, height, 256, 256);

        int minX = leftPos + ENERGY_X;
        int minY = topPos + ENERGY_Y;
        if (mouseX >= minX && mouseX < minX + ENERGY_WIDTH && mouseY >= minY && mouseY < minY + ENERGY_HEIGHT) {
            graphics.setTooltipForNextFrame(Component.translatable(
                    TOOLTIP_ENERGY, menu.getEnergy(), menu.getEnergyCapacity()
            ), mouseX, mouseY);
        }
    }

    @Override
    public ItemStack getInputStack() {
        return menu.getInputStack();
    }

    @Override
    public ItemStack getAdditiveStack(int slot) {
        return menu.getAdditiveStack(slot);
    }

    @Override
    public RecipeInput getRecipeInput() {
        return menu.getRecipeInput();
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.hasItem()) {
            renderHoveredItemTooltip(graphics, mouseX, mouseY, hoveredSlot.getItem());
            return;
        }

        if (statusTooltipArea.contains(mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(font, statusTooltip, Optional.empty(), ItemStack.EMPTY, mouseX, mouseY);
            statusTooltipArea = EMPTY;
            statusTooltip = List.of();
        }
    }

    private void renderHoveredItemTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, ItemStack stack) {
        List<Component> components = new ArrayList<>(getTooltipFromItem(minecraft, stack));
        Optional<TooltipComponent> tooltip = stack.getTooltipImage();

        int material = cache.getMaterialValue(stack.getItem());
        if (material > 0) {
            components.add(Component.translatable(FramingSawScreen.TOOLTIP_MATERIAL, material));
        }

        graphics.setTooltipForNextFrame(font, components, tooltip, stack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.x() >= targetStackX && event.x() < targetStackX + 18 && event.y() >= targetStackY && event.y() < targetStackY + 18) {
            selectRecipe(menu.getCarried());
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    public boolean isMouseOverRecipeSlot(double mouseX, double mouseY) {
        return mouseX >= targetStackX && mouseX < targetStackX + 16 && mouseY >= targetStackY && mouseY < targetStackY + 16;
    }

    public void selectRecipe(ItemStack cursorStack) {
        if (cursorStack.isEmpty() || cache.getMaterialValue(cursorStack.getItem()) != -1) {
            RecipeHolder<FramingSawRecipe> recipe = cache.findRecipeFor(cursorStack);
            if (Objects.equals(recipe, menu.getSelectedRecipe())) {
                return;
            }

            int id = recipe == null ? -1 : cache.getRecipes().indexOf(recipe);
            //noinspection ConstantConditions
            if (menu.clickMenuButton(minecraft.player, id)) {
                ClientPacketDistributor.sendToServer(new ServerboundSelectFramingSawRecipePayload(menu.containerId, id));
            }
        }
    }

    public int getTargetStackX() {
        return targetStackX;
    }

    public int getTargetStackY() {
        return targetStackY;
    }

    public Rect2i getTargetStackArea() {
        return new Rect2i(targetStackX, targetStackY, targetStackX + 16, targetStackY + 16);
    }
}
