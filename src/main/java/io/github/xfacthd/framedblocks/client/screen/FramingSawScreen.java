package io.github.xfacthd.framedblocks.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.screen.widget.BlockPreviewTooltipComponent;
import io.github.xfacthd.framedblocks.client.screen.widget.SearchEditBox;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.compat.ae2.AppliedEnergisticsCompat;
import io.github.xfacthd.framedblocks.common.compat.searchables.SearchablesCompat;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeAdditive;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCalculation;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeMatchResult;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundSelectFramingSawRecipePayload;
import io.github.xfacthd.framedblocks.common.util.CachingIngredientResolver;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class FramingSawScreen extends AbstractContainerScreen<FramingSawMenu> implements IFramingSawScreen {
    public static final String TOOLTIP_MATERIAL = Utils.translationKey("tooltip", "framing_saw.material");
    public static final Component TOOLTIP_LOOSE_ADDITIVE = Utils.translate("tooltip", "framing_saw.loose_additive");
    public static final String TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM = Utils.translationKey("tooltip", "framing_saw.have_x_but_need_y_item");
    public static final String TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM_MULTI = Utils.translationKey("tooltip", "framing_saw.have_x_but_need_y_item_multi");
    public static final String TOOLTIP_HAVE_X_BUT_NEED_Y_TAG = Utils.translationKey("tooltip", "framing_saw.have_x_but_need_y_tag");
    public static final String TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM_COUNT = Utils.translationKey("tooltip", "framing_saw.have_x_but_need_y_item_count");
    public static final String TOOLTIP_HAVE_X_BUT_NEED_Y_MATERIAL_COUNT = Utils.translationKey("tooltip", "framing_saw.have_x_but_need_y_material_count");
    public static final String TOOLTIP_OUTPUT_COUNT = Utils.translationKey("tooltip", "framing_saw.output_count");
    public static final Component TOOLTIP_HAVE_ITEM_NONE = Utils.translate("tooltip", "framing_saw.have_item_none").withStyle(ChatFormatting.GOLD);
    public static final String TOOLTIP_PRESS_TO_SHOW = Utils.translationKey("tooltip", "framing_saw.press_to_show");
    public static final String TOOLTIP_USE_INTERMEDIATE = Utils.translationKey("tooltip", "framing_saw.use_intermediate");
    public static final Component MSG_HINT_SEARCH = Utils.translate("msg", "framing_saw.search")
            .withStyle(style -> style.withShadowColor(ARGB.scaleRGB(0xFFFFFFFF, .25F)));
    private static final Identifier BACKGROUND = Utils.id("textures/gui/framing_saw.png");
    public static final Identifier WARNING_ICON = Utils.id("neoforge", "textures/gui/experimental_warning.png");
    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 233;
    private static final int RECIPES_X = 48;
    private static final int RECIPES_Y = 22;
    private static final int RECIPE_ROWS = 6;
    private static final int RECIPE_COLS = 8;
    private static final int RECIPE_COUNT = RECIPE_ROWS * RECIPE_COLS;
    private static final int RECIPE_WIDTH = 18;
    private static final int RECIPE_HEIGHT = 18;
    private static final int SCROLL_BAR_X = 195;
    private static final int SCROLL_BAR_Y = 22;
    private static final int SCROLL_BTN_WIDTH = 12;
    private static final int SCROLL_BTN_HEIGHT = 15;
    private static final int SCROLL_BTN_TEX_X = RECIPE_WIDTH * 3;
    private static final int SCROLL_BAR_HEIGHT = 108;
    private static final int WARNING_X = 20;
    private static final int WARNING_Y = 46;
    private static final int SEARCH_WIDTH = 120;
    private static final int SEARCH_HEIGHT = 14;
    private static final int SEARCH_X = IMAGE_WIDTH - SEARCH_WIDTH - 6;
    private static final int SEARCH_Y = 5;

    protected final FramingSawRecipeCache cache = FramingSawRecipeCache.get(true);
    protected final ItemStack cubeStack = new ItemStack(FBContent.BLOCK_FRAMED_CUBE.value());
    private final List<FramingSawMenu.FramedRecipeHolder> filteredRecipes = new ArrayList<>();
    protected final CachingIngredientResolver.Multi additiveResolver;
    @UnknownNullability
    private SearchEditBox searchBox = null;
    private int firstIndex = 0;
    private boolean scrolling = false;
    private float scrollOffset = 0F;
    private boolean hasEffectiveSearchQuery = false;

    protected FramingSawScreen(FramingSawMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, IMAGE_WIDTH, IMAGE_HEIGHT);
        this.titleLabelY -= 1;
        this.inventoryLabelX = 47;
        this.inventoryLabelY = 139;
        this.filteredRecipes.addAll(menu.getRecipes());
        Level level = Objects.requireNonNull(minecraft.level);
        this.additiveResolver = new CachingIngredientResolver.Multi(level, FramingSawRecipe.MAX_ADDITIVE_COUNT);
    }

    @Override
    protected void init() {
        super.init();

        int searchX = leftPos + SEARCH_X;
        int searchY = topPos + SEARCH_Y;
        Consumer<String> searchHandler = SearchablesCompat.createSearchHandler(menu::getRecipes, this::acceptSearchResult, this::onSearchChanged);
        searchBox = addRenderableWidget(new SearchEditBox(
                font, searchX, searchY, SEARCH_WIDTH, SEARCH_HEIGHT, MSG_HINT_SEARCH, searchHandler, searchBox
        ));
        searchBox.setMaxLength(50);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(RenderPipelines.GUI_TEXTURED, getBackground(), leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        int offset = (int) ((SCROLL_BAR_HEIGHT - SCROLL_BTN_HEIGHT) * scrollOffset);
        int scrollU = SCROLL_BTN_TEX_X + (isScrollBarActive() ? 0 : SCROLL_BTN_WIDTH);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + SCROLL_BAR_X, topPos + SCROLL_BAR_Y + offset, scrollU, imageHeight, SCROLL_BTN_WIDTH, SCROLL_BTN_HEIGHT, 256, 256);

        ItemStack input = getInputStack();
        if (!input.isEmpty() && cache.containsAdditive(input.getItem())) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, WARNING_ICON, leftPos + WARNING_X, topPos + WARNING_Y, 8, 8, 24, 24, 32, 32);
        }

        int idx = menu.getSelectedRecipeIndex();
        if (menu.hasRecipeChanged()) {
            handleRecipeChange();
        }

        int recX = leftPos + RECIPES_X;
        int recY = topPos + RECIPES_Y;
        int lastIndex = firstIndex + RECIPE_COUNT;
        renderButtons(graphics, mouseX, mouseY, recX, recY, lastIndex);
        renderRecipes(graphics, recX, recY, lastIndex);

        List<RecipeHolder<FramingSawRecipe>> recipes = cache.getRecipes();
        if (idx >= 0 && idx < recipes.size()) {
            FramingSawRecipe recipe = recipes.get(idx).value();
            drawInputStackHint(graphics, input);

            List<FramingSawRecipeAdditive> additives = recipe.getAdditives();
            for (int i = 0; i < additives.size(); i++) {
                ItemStack additive = getAdditiveStack(i);
                int y = topPos + 64 + (18 * i);
                drawAdditiveStackHint(graphics, i, additive, additives, y);
            }
        }
    }

    protected Identifier getBackground() {
        return BACKGROUND;
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

    protected void handleRecipeChange() {
        tryScrollToRecipe(menu.getSelectedRecipeIndex());
    }

    protected boolean drawInputStackHint(GuiGraphicsExtractor graphics, ItemStack input) {
        if (input.isEmpty()) {
            ClientUtils.renderTransparentFakeItem(graphics, cubeStack, leftPos + 20, topPos + 28);
            return true;
        }
        return false;
    }

    protected boolean drawAdditiveStackHint(GuiGraphicsExtractor graphics, int index, ItemStack additive, List<FramingSawRecipeAdditive> additives, int y) {
        if (additive.isEmpty()) {
            List<ItemStack> items = additiveResolver.getStacks(index, additives.get(index).ingredient());
            int t = (int) (System.currentTimeMillis() / 1700) % items.size();
            ClientUtils.renderTransparentFakeItem(graphics, items.get(t), leftPos + 20, y);
            return true;
        }
        return false;
    }

    protected boolean displayRecipeErrors() {
        return true;
    }

    private void tryScrollToRecipe(int idx) {
        if (idx != -1 && hasEffectiveSearchQuery) {
            FramingSawMenu.FramedRecipeHolder recipe = menu.getRecipes().get(idx);
            idx = filteredRecipes.indexOf(recipe);
        }
        if (idx != -1 && (idx < firstIndex || idx >= firstIndex + RECIPE_COUNT)) {
            int row = (idx / RECIPE_COLS) - 2; // Center the selected recipe if possible
            int hidden = getHiddenRows();
            scrollOffset = (float) row / (float) hidden;
            scrollOffset = Mth.clamp(scrollOffset, 0, 1);
            firstIndex = calculateFirstIndex(hidden);
        } else if (idx == -1) {
            firstIndex = 0;
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.hasItem()) {
            renderItemTooltip(graphics, mouseX, mouseY, hoveredSlot.getItem(), null);
            return;
        }

        ItemStack input = getInputStack();
        if (!input.isEmpty() && isHovering(WARNING_X, WARNING_Y, 16, 16, mouseX, mouseY) && cache.containsAdditive(input.getItem())) {
            graphics.setTooltipForNextFrame(font, TOOLTIP_LOOSE_ADDITIVE, mouseX, mouseY);
            return;
        }

        int x = leftPos + RECIPES_X;
        int y = topPos + RECIPES_Y;
        int last = firstIndex + RECIPE_COUNT;

        for (int idx = firstIndex; idx < last && idx < filteredRecipes.size(); idx++) {
            int relIdx = idx - firstIndex;
            int recX = x + relIdx % RECIPE_COLS * RECIPE_WIDTH;
            int recY = y + relIdx / RECIPE_COLS * RECIPE_HEIGHT;
            if (mouseX >= recX && mouseX < recX + RECIPE_WIDTH && mouseY >= recY && mouseY < recY + RECIPE_HEIGHT) {
                FramingSawMenu.FramedRecipeHolder recipe = filteredRecipes.get(idx);
                ItemStack result = recipe.getRecipe().getResultStack();
                renderItemTooltip(graphics, mouseX, mouseY, result, recipe);
                break;
            }
        }
    }

    protected void renderItemTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, ItemStack stack, FramingSawMenu.@Nullable FramedRecipeHolder recipeHolder) {
        List<Component> components = new ArrayList<>(getTooltipFromItem(minecraft, stack));
        Optional<TooltipComponent> tooltip = stack.getTooltipImage();

        int material = cache.getMaterialValue(stack.getItem());
        if (material > 0) {
            components.add(Component.translatable(TOOLTIP_MATERIAL, material));
        }

        if (recipeHolder != null && displayRecipeErrors()) {
            appendRecipeFailure(components, recipeHolder);
        }
        if (recipeHolder != null) {
            TrackingItemStackRenderState renderState = new TrackingItemStackRenderState();
            minecraft.getItemModelResolver().updateForTopItem(renderState, stack, ItemDisplayContext.FIXED, null, null, 0);
            if (renderState.usesBlockLight()) {
                tooltip = Optional.of(new BlockPreviewTooltipComponent(renderState));
            }
        }

        graphics.setTooltipForNextFrame(font, components, tooltip, stack, mouseX, mouseY);
    }

    private void appendRecipeFailure(List<Component> components, FramingSawMenu.FramedRecipeHolder recipeHolder) {
        appendRecipeFailure(components, cache, additiveResolver, recipeHolder.getRecipe(), recipeHolder.getMatchResult(), this);
    }

    public static List<Component> appendRecipeFailure(
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
        List<MutableComponent> detail = switch (matchResult)
        {
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

    private void renderButtons(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, int lastIdx) {
        int selIdx = menu.getSelectedRecipeIndex();
        // Only need to convert the index into filtered space when a query is present which shrinks the displayed count
        if (selIdx != -1 && hasEffectiveSearchQuery) {
            FramingSawMenu.FramedRecipeHolder recipe = menu.getRecipes().get(selIdx);
            selIdx = filteredRecipes.indexOf(recipe);
        }

        for (int idx = firstIndex; idx < lastIdx && idx < filteredRecipes.size(); ++idx) {
            int relIdx = idx - firstIndex;
            int recX = x + relIdx % RECIPE_COLS * RECIPE_WIDTH;
            int recY = y + relIdx / RECIPE_COLS * RECIPE_HEIGHT;

            int u = 0;
            boolean hovered = false;
            if (idx == selIdx) {
                u += RECIPE_WIDTH;
            } else if (mouseX >= recX && mouseY >= recY && mouseX < recX + RECIPE_WIDTH && mouseY < recY + RECIPE_HEIGHT) {
                u += (RECIPE_WIDTH * 2);
                hovered = true;
            }

            int color = 0xFFFFFFFF;
            if (!hovered && displayRecipeErrors() && !filteredRecipes.get(idx).getMatchResult().success()) {
                color = 0xFFE54C4C;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, recX, recY, u, imageHeight, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256, color);
        }
    }

    private void renderRecipes(GuiGraphicsExtractor graphics, int pLeft, int pTop, int lastIndex) {
        for (int idx = firstIndex; idx < lastIndex && idx < filteredRecipes.size(); idx++) {
            int relIdx = idx - firstIndex;
            int x = pLeft + relIdx % RECIPE_COLS * RECIPE_WIDTH + 1;
            int y = pTop + relIdx / RECIPE_COLS * RECIPE_HEIGHT + 1;

            ItemStack stack = filteredRecipes.get(idx).getRecipe().getResultStack();
            graphics.item(stack, x, y, x * y * imageWidth);
            graphics.itemDecorations(font, stack, x, y);
        }
    }

    @Override
    protected void containerTick() {
        searchBox.tick();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        scrolling = false;

        GuiEventListener focused = getFocused();
        if (focused != null && !focused.isMouseOver(event.x(), event.y())) {
            setFocused(null);
        }

        int x = leftPos + RECIPES_X;
        int y = topPos + RECIPES_Y;
        int lastIdx = firstIndex + RECIPE_COUNT;

        for (int idx = firstIndex; idx < lastIdx; ++idx) {
            int relIdx = idx - firstIndex;
            double recRelX = event.x() - (double)(x + relIdx % RECIPE_COLS * RECIPE_WIDTH);
            double recRelY = event.y() - (double)(y + relIdx / RECIPE_COLS * RECIPE_HEIGHT);
            if (recRelX < 0 || recRelY < 0 || recRelX > RECIPE_WIDTH || recRelY > RECIPE_HEIGHT) {
                continue;
            }

            if (hasEffectiveSearchQuery) {
                if (idx < 0 || idx >= filteredRecipes.size()) {
                    break;
                }

                RecipeHolder<FramingSawRecipe> recipe = filteredRecipes.get(idx).toVanilla();
                idx = cache.getRecipes().indexOf(recipe);
                if (idx == -1) {
                    break;
                }
            }

            //noinspection ConstantConditions
            if (menu.clickMenuButton(minecraft.player, idx)) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                ClientPacketDistributor.sendToServer(new ServerboundSelectFramingSawRecipePayload(menu.containerId, idx));
                return true;
            }
        }

        if (isScrollBarActive()) {
            x = leftPos + SCROLL_BAR_X;
            y = topPos + SCROLL_BAR_Y;
            if (event.x() >= (double) x && event.x() < (double) (x + SCROLL_BTN_WIDTH) && event.y() >= (double) y && event.y() < (double) (y + SCROLL_BAR_HEIGHT)) {
                scrolling = true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (scrolling && isScrollBarActive()) {
            float topY = topPos + RECIPES_Y;
            float botY = topY + SCROLL_BAR_HEIGHT;
            float freeScrollHeight = botY - topY - SCROLL_BTN_HEIGHT;

            scrollOffset = ((float) event.y() - topY - (SCROLL_BTN_HEIGHT / 2F)) / freeScrollHeight;
            scrollOffset = Mth.clamp(scrollOffset, 0F, 1F);
            firstIndex = calculateFirstIndex(getHiddenRows());

            return true;
        }

        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (isScrollBarActive()) {
            int hiddenRows = getHiddenRows();
            float offset = (float) deltaY / (float) hiddenRows;
            scrollOffset = Mth.clamp(scrollOffset - offset, 0F, 1F);
            firstIndex = calculateFirstIndex(hiddenRows);
        }

        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        // Prevent typing E in the search box from closing the screen
        if (searchBox.isFocused() && Objects.requireNonNull(minecraft).options.keyInventory.matches(event)) {
            return true;
        }
        return super.keyPressed(event);
    }

    public @Nullable PointedRecipe getRecipeAt(double mouseX, double mouseY) {
        double x = leftPos + RECIPES_X;
        double y = topPos + RECIPES_Y;

        if (mouseX >= x && mouseX <= x + (RECIPE_WIDTH * RECIPE_COLS) && mouseY >= y && mouseY <= y + (RECIPE_HEIGHT * RECIPE_ROWS)) {
            int col = (int) ((mouseX - x) / RECIPE_WIDTH);
            int row = (int) ((mouseY - y) / RECIPE_HEIGHT);
            int idx = (row * RECIPE_COLS) + col + firstIndex;

            if (idx > 0 && idx < filteredRecipes.size()) {
                int rx = (int) x + col * RECIPE_WIDTH;
                int ry = (int) y + row * RECIPE_HEIGHT;
                return new PointedRecipe(filteredRecipes.get(idx).toVanilla(), rx, ry);
            }
        }
        return null;
    }

    private void onSearchChanged(String query) {
        if (query.isBlank()) {
            acceptSearchResult(menu.getRecipes());
            return;
        }

        List<FramingSawMenu.FramedRecipeHolder> recipes = new ArrayList<>(menu.getRecipes().size());
        query = query.toLowerCase(Locale.ROOT);
        for (FramingSawMenu.FramedRecipeHolder recipe : menu.getRecipes()) {
            Component name = recipe.getRecipe().getResultStack().getItemName();
            if (name.getString().toLowerCase(Locale.ROOT).contains(query)) {
                recipes.add(recipe);
            }
        }
        acceptSearchResult(recipes);
    }

    private void acceptSearchResult(List<FramingSawMenu.FramedRecipeHolder> recipes) {
        filteredRecipes.clear();
        filteredRecipes.addAll(recipes);
        hasEffectiveSearchQuery = filteredRecipes.size() != menu.getRecipes().size();
        // If the query is not "effective" then the selected recipe must be in the list
        if (!hasEffectiveSearchQuery || filteredRecipes.contains(menu.getRecipes().get(menu.getSelectedRecipeIndex()))) {
            tryScrollToRecipe(menu.getSelectedRecipeIndex());
        }
    }

    private boolean isScrollBarActive() {
        return filteredRecipes.size() > RECIPE_COUNT;
    }

    private int getHiddenRows() {
        return (filteredRecipes.size() + RECIPE_COLS - 1) / RECIPE_COLS - RECIPE_ROWS;
    }

    private int calculateFirstIndex(int hiddenRows) {
        int idx = (int) ((double) (scrollOffset * (float) hiddenRows) + .5D) * RECIPE_COLS;
        return Mth.clamp(idx, 0, filteredRecipes.size() - 1);
    }

    public static FramingSawScreen create(FramingSawMenu menu, Inventory inv, Component title) {
        if (AppliedEnergisticsCompat.isLoaded()) {
            return new FramingSawWithEncoderScreen(menu, inv, title);
        }
        return new FramingSawScreen(menu, inv, title);
    }

    public record PointedRecipe(ResourceKey<Recipe<?>> id, FramingSawRecipe recipe, Rect2i area) {
        private PointedRecipe(RecipeHolder<FramingSawRecipe> recipe, int x, int y) {
            this(recipe.id(), recipe.value(), new Rect2i(x, y, RECIPE_WIDTH, RECIPE_HEIGHT));
        }
    }
}
