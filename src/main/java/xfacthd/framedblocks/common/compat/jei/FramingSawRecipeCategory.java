package xfacthd.framedblocks.common.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.screen.FramingSawScreen;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramingSawBlock;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;

import java.util.List;
import java.util.stream.Stream;

public final class FramingSawRecipeCategory implements IRecipeCategory<FramingSawRecipe>
{
    private static final ResourceLocation BACKGROUND = Utils.rl("textures/gui/framing_saw_jei.png");
    private static final int WIDTH = 82;
    private static final int HEIGHT = 41;
    private static final int WARNING_X = 20;
    private static final int WARNING_Y = 3;
    private static final int WARNING_SIZE = 16;
    private static final float WARNING_SCALE = .75F;
    private static final int WARNING_DRAW_SIZE = (int) (WARNING_SIZE * WARNING_SCALE);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable warning;

    public FramingSawRecipeCategory(IGuiHelper guiHelper)
    {
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(FBContent.blockFramingSaw.get()));
        this.warning = guiHelper.drawableBuilder(FramingSawScreen.WARNING_ICON, 8, 8, WARNING_SIZE, WARNING_SIZE).setTextureSize(32, 32).build();
    }

    @Override
    @SuppressWarnings("removal")
    public ResourceLocation getUid() { return FramedJeiPlugin.FRAMING_SAW_RECIPE_TYPE.getUid(); }

    @Override
    @SuppressWarnings("removal")
    public Class<? extends FramingSawRecipe> getRecipeClass() { return FramingSawRecipe.class; }

    @Override
    public RecipeType<FramingSawRecipe> getRecipeType() { return FramedJeiPlugin.FRAMING_SAW_RECIPE_TYPE; }

    @Override
    public Component getTitle() { return FramingSawBlock.MENU_TITLE; }

    @Override
    public IDrawable getBackground() { return background; }

    @Override
    public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FramingSawRecipe recipe, IFocusGroup focuses)
    {
        FramingSawRecipeCache cache = FramingSawRecipeCache.get(true);
        Ingredient additive = recipe.getAdditive();

        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).setSlotName("input");
        IRecipeSlotBuilder additiveSlot = null;
        if (additive != null)
        {
            additiveSlot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 24);
        }
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 13);

        ItemStack inputStack = focuses.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT)
                .map(focus -> focus.getTypedValue().getIngredient())
                .filter(stack -> cache.getMaterialValue(stack.getItem()) > 0)
                .findFirst()
                .orElse(ItemStack.EMPTY);

        if (focuses.isEmpty())
        {
            for (Item input : cache.getKnownItems())
            {
                setRecipe(input, recipe, inputSlot, additiveSlot, outputSlot);
            }

            if (additive != null)
            {
                builder.createFocusLink(inputSlot, additiveSlot, outputSlot);
            }
            else
            {
                builder.createFocusLink(inputSlot, outputSlot);
            }
        }
        else if (!inputStack.isEmpty())
        {
            setRecipe(inputStack.getItem(), recipe, inputSlot, additiveSlot, outputSlot);
        }
        else
        {
            setRecipe(FBContent.blockFramedCube.get().asItem(), recipe, inputSlot, additiveSlot, outputSlot);
        }
    }

    private static void setRecipe(
            Item input, FramingSawRecipe recipe, IRecipeSlotBuilder inputSlot, IRecipeSlotBuilder additiveSlot, IRecipeSlotBuilder outputSlot
    )
    {
        IntIntPair counts = recipe.getInputOutputCount(input, true);

        ItemStack inputStack = new ItemStack(input, counts.leftInt());
        ItemStack outputStack = recipe.getResultItem().copy();
        outputStack.setCount(counts.rightInt());

        Ingredient additive = recipe.getAdditive();
        if (additive != null)
        {
            int addCount = recipe.getAdditiveCount() * (counts.rightInt() / recipe.getResultItem().getCount());

            Stream.of(additive.getItems())
                    .map(ItemStack::copy)
                    .peek(s -> s.setCount(addCount))
                    .forEach(additiveStack ->
                            setRecipe(inputStack, additiveStack, outputStack, inputSlot, additiveSlot, outputSlot)
                    );
        }
        else
        {
            setRecipe(inputStack, null, outputStack, inputSlot, additiveSlot, outputSlot);
        }
    }

    private static void setRecipe(
            ItemStack input, ItemStack additive, ItemStack output, IRecipeSlotBuilder inputSlot, IRecipeSlotBuilder additiveSlot, IRecipeSlotBuilder outputSlot
    )
    {
        inputSlot.addItemStack(input);
        if (additive != null)
        {
            additiveSlot.addItemStack(additive);
        }
        outputSlot.addItemStack(output);
    }

    @Override
    public void draw(FramingSawRecipe recipe, IRecipeSlotsView slots, PoseStack poseStack, double mouseX, double mouseY)
    {
        ItemStack input = slots.findSlotByName("input")
                .orElseThrow()
                .getDisplayedIngredient(VanillaTypes.ITEM_STACK)
                .orElseThrow();

        if (FramingSawRecipeCache.get(true).containsAdditive(input.getItem()))
        {
            poseStack.pushPose();
            poseStack.scale(WARNING_SCALE, WARNING_SCALE, 1F);
            poseStack.translate(WARNING_X * (1F / WARNING_SCALE), WARNING_Y * (1F / WARNING_SCALE), 0);
            warning.draw(poseStack);
            poseStack.popPose();
        }
    }

    @Override
    public List<Component> getTooltipStrings(FramingSawRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY)
    {
        if (mouseX >= WARNING_X && mouseY >= WARNING_Y && mouseX <= (WARNING_X + WARNING_DRAW_SIZE) && mouseY <= (WARNING_Y + WARNING_DRAW_SIZE))
        {
            ItemStack input = slots.findSlotByName("input")
                    .orElseThrow()
                    .getDisplayedIngredient(VanillaTypes.ITEM_STACK)
                    .orElseThrow();

            if (FramingSawRecipeCache.get(true).containsAdditive(input.getItem()))
            {
                return List.of(FramingSawScreen.TOOLTIP_LOOSE_ADDITIVE);
            }
        }

        return List.of();
    }
}
