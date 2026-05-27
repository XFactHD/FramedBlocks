package io.github.xfacthd.framedblocks.client.screen.saw;

import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.block.SimpleBlockCamoContainer;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.screen.widget.BlockPreviewTooltipComponent;
import io.github.xfacthd.framedblocks.client.screen.widget.SawCamoModeButton;
import io.github.xfacthd.framedblocks.client.screen.widget.SearchEditBox;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.compat.ae2.AppliedEnergisticsCompat;
import io.github.xfacthd.framedblocks.common.compat.searchables.SearchablesCompat;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeAdditive;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundSelectFramingSawRecipePayload;
import io.github.xfacthd.framedblocks.common.util.CachingIngredientResolver;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
    public static final Component MSG_HINT_SEARCH = Utils.translate("msg", "framing_saw.search")
            .withStyle(style -> style.withShadowColor(ARGB.scaleRGB(0xFFFFFFFF, .25F)));
    private static final Identifier BACKGROUND = Utils.id("textures/gui/framing_saw.png");
    public static final Identifier WARNING_ICON = Utils.id("neoforge", "textures/gui/experimental_warning.png");
    private static final Identifier SPRITE_RECIPE = Utils.id("framing_saw/recipe");
    private static final Identifier SPRITE_RECIPE_HOVERED = Utils.id("framing_saw/recipe_highlighted");
    private static final Identifier SPRITE_RECIPE_SELECTED = Utils.id("framing_saw/recipe_selected");
    private static final Identifier SPRITE_SCROLLER = Utils.id("framing_saw/scroller");
    private static final Identifier SPRITE_SCROLLER_DISABLED = Utils.id("framing_saw/scroller_disabled");
    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 240;
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
    private static final int SCROLL_BAR_HEIGHT = 108;
    private static final int WARNING_X = 20;
    private static final int WARNING_Y = 41;
    private static final int CAMO_MODE_X = 19;
    private static final int CAMO_MODE_Y = 113;
    private static final int SEARCH_WIDTH = 120;
    private static final int SEARCH_HEIGHT = 14;
    private static final int SEARCH_X = IMAGE_WIDTH - SEARCH_WIDTH - 6;
    private static final int SEARCH_Y = 5;

    protected final FramingSawRecipeCache cache = FramingSawRecipeCache.get(true);
    protected final ItemStack cubeStack = new ItemStack(FBContent.BLOCK_FRAMED_CUBE.value());
    private final List<FramingSawMenu.FramedRecipeHolder> filteredRecipes = new ArrayList<>();
    protected final CachingIngredientResolver.Multi additiveResolver;
    private final CamoList dummyCamos;
    @UnknownNullability
    private SearchEditBox searchBox = null;
    private int firstIndex = 0;
    private boolean scrolling = false;
    private float scrollOffset = 0F;
    private boolean hasEffectiveSearchQuery = false;
    private boolean showResultsWithCamo = false;

    protected FramingSawScreen(FramingSawMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, IMAGE_WIDTH, IMAGE_HEIGHT);
        this.titleLabelY -= 1;
        this.inventoryLabelX = 47;
        this.inventoryLabelY = 147;
        this.filteredRecipes.addAll(menu.getRecipes());
        Level level = Objects.requireNonNull(minecraft.level);
        this.additiveResolver = new CachingIngredientResolver.Multi(level, FramingSawRecipe.MAX_ADDITIVE_COUNT);
        this.dummyCamos = CamoList.of(
                new SimpleBlockCamoContainer(Blocks.POLISHED_GRANITE.defaultBlockState(), FBContent.FACTORY_BLOCK.get()),
                new SimpleBlockCamoContainer(Blocks.POLISHED_DIORITE.defaultBlockState(), FBContent.FACTORY_BLOCK.get())
        );
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

        addRenderableWidget(new SawCamoModeButton(
                minecraft,
                leftPos + CAMO_MODE_X,
                topPos + CAMO_MODE_Y,
                this::onToggleCamoMode,
                () -> showResultsWithCamo
        ));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(RenderPipelines.GUI_TEXTURED, getBackground(), leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        int offset = (int) ((SCROLL_BAR_HEIGHT - SCROLL_BTN_HEIGHT) * scrollOffset);
        Identifier scroller = isScrollBarActive() ? SPRITE_SCROLLER : SPRITE_SCROLLER_DISABLED;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, scroller, leftPos + SCROLL_BAR_X, topPos + SCROLL_BAR_Y + offset, SCROLL_BTN_WIDTH, SCROLL_BTN_HEIGHT);

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
                int y = topPos + 58 + (18 * i);
                drawAdditiveStackHint(graphics, i, additive, additives, y);
            }
        }

        int toggleX = leftPos + CAMO_MODE_X;
        int toggleY = topPos + CAMO_MODE_Y;
        Identifier camoToggleSprite = showResultsWithCamo ? Utils.id("minecraft", "widget/cross_button") : Identifier.withDefaultNamespace("widget/button");
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, camoToggleSprite, toggleX, toggleY, 18, 18);
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
            ClientUtils.renderTransparentFakeItem(graphics, cubeStack, leftPos + 20, topPos + 22);
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
            scrollOffset = 0;
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

        int minX = leftPos + RECIPES_X;
        int maxX = minX + (RECIPE_COLS * RECIPE_WIDTH);
        int minY = topPos + RECIPES_Y;
        int maxY = minY + (RECIPE_ROWS * RECIPE_HEIGHT);

        if (mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY) {
            int col = (mouseX - minX) / RECIPE_WIDTH;
            int row = (mouseY - minY) / RECIPE_HEIGHT;
            int idx = firstIndex + row * RECIPE_COLS + col;

            if (idx >= 0 && idx < filteredRecipes.size()) {
                FramingSawMenu.FramedRecipeHolder recipe = filteredRecipes.get(idx);
                ItemStack result = recipe.getRecipe().getResultStack();
                renderItemTooltip(graphics, mouseX, mouseY, result, recipe);
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
            minecraft.getItemModelResolver().updateForTopItem(renderState, getResultRenderStack(stack), ItemDisplayContext.FIXED, null, null, 0);
            if (renderState.usesBlockLight()) {
                tooltip = Optional.of(new BlockPreviewTooltipComponent(renderState));
            }
        }

        graphics.setTooltipForNextFrame(font, components, tooltip, stack, mouseX, mouseY);
    }

    private void appendRecipeFailure(List<Component> components, FramingSawMenu.FramedRecipeHolder recipeHolder) {
        SawRecipeFailurePrinter.appendRecipeFailure(components, cache, additiveResolver, recipeHolder.getRecipe(), recipeHolder.getMatchResult(), this);
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

            Identifier sprite = SPRITE_RECIPE;
            boolean hovered = false;
            if (idx == selIdx) {
                sprite = SPRITE_RECIPE_SELECTED;
            } else if (mouseX >= recX && mouseY >= recY && mouseX < recX + RECIPE_WIDTH && mouseY < recY + RECIPE_HEIGHT) {
                sprite = SPRITE_RECIPE_HOVERED;
                hovered = true;
            }

            int color = 0xFFFFFFFF;
            if (!hovered && displayRecipeErrors() && !filteredRecipes.get(idx).getMatchResult().success()) {
                color = 0xFFE54C4C;
            }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, recX, recY, RECIPE_WIDTH, RECIPE_HEIGHT, color);
        }
    }

    private void renderRecipes(GuiGraphicsExtractor graphics, int pLeft, int pTop, int lastIndex) {
        for (int idx = firstIndex; idx < lastIndex && idx < filteredRecipes.size(); idx++) {
            int relIdx = idx - firstIndex;
            int x = pLeft + relIdx % RECIPE_COLS * RECIPE_WIDTH + 1;
            int y = pTop + relIdx / RECIPE_COLS * RECIPE_HEIGHT + 1;

            ItemStack stack = getResultRenderStack(filteredRecipes.get(idx).getRecipe().getResultStack());
            graphics.item(stack, x, y, x * y * imageWidth);
            graphics.itemDecorations(font, stack, x, y);
        }
    }

    private ItemStack getResultRenderStack(ItemStack stack) {
        if (showResultsWithCamo) {
            stack = stack.copy();
            stack.set(FramedConstants.Objects.DC_TYPE_CAMO_LIST, dummyCamos);
        }
        return stack;
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

        int minX = leftPos + RECIPES_X;
        int maxX = minX + (RECIPE_COLS * RECIPE_WIDTH);
        int minY = topPos + RECIPES_Y;
        int maxY = minY + (RECIPE_ROWS * RECIPE_HEIGHT);

        if (event.x() >= minX && event.x() < maxX && event.y() >= minY && event.y() < maxY) {
            int col = (int) ((event.x() - minX) / RECIPE_WIDTH);
            int row = (int) ((event.y() - minY) / RECIPE_HEIGHT);
            int idx = firstIndex + row * RECIPE_COLS + col;
            if (idx >= 0 && idx < filteredRecipes.size()) {
                if (hasEffectiveSearchQuery) {
                    RecipeHolder<FramingSawRecipe> recipe = filteredRecipes.get(idx).toVanilla();
                    idx = cache.getRecipes().indexOf(recipe);
                    if (idx == -1) {
                        return false;
                    }
                }
                //noinspection ConstantConditions
                if (menu.clickMenuButton(minecraft.player, idx)) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    ClientPacketDistributor.sendToServer(new ServerboundSelectFramingSawRecipePayload(menu.containerId, idx));
                    return true;
                }
            }
            return false;
        }

        if (isScrollBarActive()) {
            minX = leftPos + SCROLL_BAR_X;
            minY = topPos + SCROLL_BAR_Y;
            if (event.x() >= (double) minX && event.x() < (double) (minX + SCROLL_BTN_WIDTH) && event.y() >= (double) minY && event.y() < (double) (minY + SCROLL_BAR_HEIGHT)) {
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
        } else {
            tryScrollToRecipe(-1);
        }
    }

    private void onToggleCamoMode(Button btn) {
        showResultsWithCamo = !showResultsWithCamo;
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
