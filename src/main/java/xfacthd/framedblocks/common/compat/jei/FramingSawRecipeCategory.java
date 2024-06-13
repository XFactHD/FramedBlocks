package xfacthd.framedblocks.common.compat.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.screen.FramingSawScreen;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.special.FramingSawBlock;
import xfacthd.framedblocks.common.crafting.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FramingSawRecipeCategory implements IRecipeCategory<FramingSawRecipe>
{
    private static final ResourceLocation BACKGROUND = Utils.rl("textures/gui/framing_saw_jei.png");
    private static final int WIDTH = 118;
    private static final int HEIGHT = 41;
    private static final int WARNING_X = 38;
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
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(FBContent.BLOCK_FRAMING_SAW.value()));
        this.warning = guiHelper.drawableBuilder(FramingSawScreen.WARNING_ICON, 8, 8, WARNING_SIZE, WARNING_SIZE).setTextureSize(32, 32).build();
    }

    @Override
    public RecipeType<FramingSawRecipe> getRecipeType()
    {
        return FramedJeiPlugin.FRAMING_SAW_RECIPE_TYPE;
    }

    @Override
    public Component getTitle()
    {
        return FramingSawBlock.SAW_MENU_TITLE;
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FramingSawRecipe recipe, IFocusGroup focuses)
    {
        FramingSawRecipeCache cache = FramingSawRecipeCache.get(true);
        List<FramingSawRecipeAdditive> additives = recipe.getAdditives();

        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).setSlotName("input");
        IRecipeSlotBuilder[] additiveSlots = null;
        if (!additives.isEmpty())
        {
            additiveSlots = new IRecipeSlotBuilder[additives.size()];
            for (int i = 0; i < additives.size(); i++)
            {
                int x = 1 + (i * 18);
                additiveSlots[i] = builder.addSlot(RecipeIngredientRole.INPUT, x, 24);
            }
        }
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 97, 13);

        if (focuses.isEmpty())
        {
            for (Item input : cache.getKnownItems())
            {
                setRecipe(input, recipe, inputSlot, additiveSlots, outputSlot);
            }

            if (additiveSlots != null)
            {
                IRecipeSlotBuilder[] slots = new IRecipeSlotBuilder[additives.size() + 2];
                slots[0] = inputSlot;
                slots[slots.length - 1] = outputSlot;
                System.arraycopy(additiveSlots, 0, slots, 1, additiveSlots.length);
                builder.createFocusLink(slots);
            }
            else
            {
                builder.createFocusLink(inputSlot, outputSlot);
            }
            return;
        }

        ItemStack inputStack = focuses.getItemStackFocuses(RecipeIngredientRole.INPUT)
                .map(focus -> focus.getTypedValue().getIngredient())
                .filter(stack -> cache.getMaterialValue(stack.getItem()) > 0)
                .findFirst()
                .orElse(ItemStack.EMPTY);

        if (!inputStack.isEmpty())
        {
            setRecipe(inputStack.getItem(), recipe, inputSlot, additiveSlots, outputSlot);
        }
        else
        {
            setRecipe(FBContent.BLOCK_FRAMED_CUBE.value().asItem(), recipe, inputSlot, additiveSlots, outputSlot);
        }
    }

    private static void setRecipe(
            Item input,
            FramingSawRecipe recipe,
            IRecipeSlotBuilder inputSlot,
            IRecipeSlotBuilder[] additiveSlots,
            IRecipeSlotBuilder outputSlot
    )
    {
        FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(
                new SingleRecipeInput(new ItemStack(input)), true
        );

        ItemStack inputStack = new ItemStack(input, calc.getInputCount());
        ItemStack outputStack = recipe.getResult().copy();
        int outputCount = calc.getOutputCount();
        outputStack.setCount(outputCount);

        List<FramingSawRecipeAdditive> additives = recipe.getAdditives();
        List<List<ItemStack>> flatAdditives = new ArrayList<>(FramingSawRecipe.MAX_ADDITIVE_COUNT);
        for (FramingSawRecipeAdditive additive : additives)
        {
            int addCount = additive.count() * (outputCount / recipe.getResult().getCount());
            List<ItemStack> additiveStacks = Stream.of(additive.ingredient().getItems())
                    .map(ItemStack::copy)
                    .peek(s -> s.setCount(addCount))
                    .toList();
            flatAdditives.add(additiveStacks);
        }
        List<List<ItemStack>> combinations = Lists.cartesianProduct(flatAdditives);
        combinations.forEach(stacks ->
        {
            inputSlot.addItemStack(inputStack);
            outputSlot.addItemStack(outputStack);
            for (int i = 0; i < stacks.size(); i++)
            {
                additiveSlots[i].addItemStack(stacks.get(i));
            }
        });
    }

    @Override
    public void draw(FramingSawRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY)
    {
        ItemStack input = slots.findSlotByName("input")
                .orElseThrow()
                .getDisplayedItemStack()
                .orElseThrow();

        if (FramingSawRecipeCache.get(true).containsAdditive(input.getItem()))
        {
            graphics.pose().pushPose();
            graphics.pose().scale(WARNING_SCALE, WARNING_SCALE, 1F);
            graphics.pose().translate(WARNING_X * (1F / WARNING_SCALE), WARNING_Y * (1F / WARNING_SCALE), 0);
            warning.draw(graphics);
            graphics.pose().popPose();
        }
    }

    @Override
    public List<Component> getTooltipStrings(FramingSawRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY)
    {
        if (mouseX >= WARNING_X && mouseY >= WARNING_Y && mouseX <= (WARNING_X + WARNING_DRAW_SIZE) && mouseY <= (WARNING_Y + WARNING_DRAW_SIZE))
        {
            ItemStack input = slots.findSlotByName("input")
                    .orElseThrow()
                    .getDisplayedItemStack()
                    .orElseThrow();

            if (FramingSawRecipeCache.get(true).containsAdditive(input.getItem()))
            {
                return List.of(FramingSawScreen.TOOLTIP_LOOSE_ADDITIVE);
            }
        }

        return List.of();
    }
}
