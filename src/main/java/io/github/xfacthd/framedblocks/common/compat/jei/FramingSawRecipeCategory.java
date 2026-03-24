package io.github.xfacthd.framedblocks.common.compat.jei;

import com.google.common.collect.Lists;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.screen.FramingSawScreen;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.special.FramingSawBlock;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeAdditive;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCalculation;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FramingSawRecipeCategory implements IRecipeCategory<FramingSawRecipe>
{
    private static final Identifier BACKGROUND = Utils.id("textures/gui/framing_saw_jei.png");
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
    public IRecipeType<FramingSawRecipe> getRecipeType()
    {
        return FramedJeiPlugin.FRAMING_SAW_RECIPE_TYPE;
    }

    @Override
    public Component getTitle()
    {
        return FramingSawBlock.SAW_MENU_TITLE;
    }

    @Override
    public int getWidth()
    {
        return WIDTH;
    }

    @Override
    public int getHeight()
    {
        return HEIGHT;
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
            IRecipeSlotBuilder @Nullable [] additiveSlots,
            IRecipeSlotBuilder outputSlot
    )
    {
        FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(
                new SingleRecipeInput(new ItemStack(input)), true
        );

        ItemStack inputStack = new ItemStack(input, calc.getInputCount());
        ItemStack outputStack = recipe.getResultStack().copy();
        int outputCount = calc.getOutputCount();
        outputStack.setCount(outputCount);

        List<FramingSawRecipeAdditive> additives = recipe.getAdditives();
        List<List<ItemStack>> flatAdditives = new ArrayList<>(FramingSawRecipe.MAX_ADDITIVE_COUNT);
        ContextMap context = SlotDisplayContext.fromLevel(Objects.requireNonNull(Minecraft.getInstance().level));
        for (FramingSawRecipeAdditive additive : additives)
        {
            int addCount = additive.count() * (outputCount / recipe.getResult().count());
            List<ItemStack> additiveStacks = additive.ingredient()
                    .display()
                    .resolve(context, SlotDisplay.ItemStackContentsFactory.INSTANCE)
                    .map(ItemStack::copy)
                    .peek(s -> s.setCount(addCount))
                    .toList();
            flatAdditives.add(additiveStacks);
        }
        List<List<ItemStack>> combinations = Lists.cartesianProduct(flatAdditives);
        combinations.forEach(stacks ->
        {
            inputSlot.add(inputStack);
            outputSlot.add(outputStack);
            if (additiveSlots != null)
            {
                for (int i = 0; i < stacks.size(); i++)
                {
                    additiveSlots[i].add(stacks.get(i));
                }
            }
        });
    }

    @Override
    public void draw(FramingSawRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY)
    {
        background.draw(graphics);

        ItemStack input = slots.findSlotByName("input")
                .orElseThrow()
                .getDisplayedItemStack()
                .orElseThrow();

        if (FramingSawRecipeCache.get(true).containsAdditive(input.getItem()))
        {
            graphics.pose().pushMatrix();
            graphics.pose().scale(WARNING_SCALE, WARNING_SCALE);
            graphics.pose().translate(WARNING_X * (1F / WARNING_SCALE), WARNING_Y * (1F / WARNING_SCALE));
            warning.draw(graphics);
            graphics.pose().popMatrix();
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FramingSawRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY)
    {
        if (mouseX >= WARNING_X && mouseY >= WARNING_Y && mouseX <= (WARNING_X + WARNING_DRAW_SIZE) && mouseY <= (WARNING_Y + WARNING_DRAW_SIZE))
        {
            ItemStack input = slots.findSlotByName("input")
                    .orElseThrow()
                    .getDisplayedItemStack()
                    .orElse(ItemStack.EMPTY);

            if (FramingSawRecipeCache.get(true).containsAdditive(input.getItem()))
            {
                tooltip.add(FramingSawScreen.TOOLTIP_LOOSE_ADDITIVE);
            }
        }
    }
}
