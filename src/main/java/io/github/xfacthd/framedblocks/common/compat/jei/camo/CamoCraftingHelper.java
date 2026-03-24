package io.github.xfacthd.framedblocks.common.compat.jei.camo;

import com.mojang.datafixers.util.Pair;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.common.compat.jei.JeiCompat;
import io.github.xfacthd.framedblocks.common.crafting.camo.CamoApplicationRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CamoCraftingHelper
{
    private static final int MAX_CAMO_EXAMPLE_INGREDIENTS_COUNT = 100;
    private static final CamoApplicationRecipe DUMMY_RECIPE = new CamoApplicationRecipe(Ingredient.of(Items.BRUSH));

    private CamoApplicationRecipe helperRecipe;
    private final Ingredient fakeEmptyIngredient;
    private final Ingredient camoExamplesIngredient;
    private final Ingredient emptyFramesIngredient;
    private final Ingredient emptyDoubleFramesIngredient;
    private List<ItemStack> camoExamples = new ArrayList<>();
    private List<ItemStack> emptyFramedBlocks = new ArrayList<>();
    private List<ItemStack> emptyDoubleFramedBlocks = new ArrayList<>();

    public CamoCraftingHelper()
    {
        this.helperRecipe = DUMMY_RECIPE;
        this.fakeEmptyIngredient = makeDummyIngredient(DummyIngredientType.EMPTY);
        this.camoExamplesIngredient = makeDummyIngredient(DummyIngredientType.CAMO_EXAMPLES);
        this.emptyFramesIngredient = makeDummyIngredient(DummyIngredientType.EMPTY_FRAMES);
        this.emptyDoubleFramesIngredient = makeDummyIngredient(DummyIngredientType.EMPTY_DOUBLE_FRAMES);
    }

    public void captureRecipe(Optional<CamoApplicationRecipe> recipe)
    {
        helperRecipe = recipe.orElse(DUMMY_RECIPE);
    }

    public Ingredient getCopyToolIngredient()
    {
        return helperRecipe.getCopyTool();
    }

    public void scanForItems(IIngredientManager ingredientManager)
    {
        List<ItemStack> camoExamples = new ArrayList<>();
        List<ItemStack> emptyFramedBlocks = new ArrayList<>();
        List<ItemStack> emptyDoubleFramedBlocks = new ArrayList<>();

        for (ItemStack stack : ingredientManager.getAllItemStacks())
        {
            if (camoExamples.size() < MAX_CAMO_EXAMPLE_INGREDIENTS_COUNT)
            {
                CamoContainerFactory<?> factory = CamoItemStackHelper.getCamoContainerFactory(stack);
                if (factory != null)
                {
                    camoExamples.add(stack);
                }
            }

            if (CamoItemStackHelper.isEmptyFramedBlock(stack))
            {
                emptyFramedBlocks.add(stack);
                if (CamoItemStackHelper.isDoubleFramedBlock(stack))
                {
                    emptyDoubleFramedBlocks.add(stack);
                }
            }
        }

        // Must be kept mutable for later shuffling for randomized ingredient displays
        this.camoExamples = camoExamples;
        this.emptyFramedBlocks = List.copyOf(emptyFramedBlocks);
        this.emptyDoubleFramedBlocks = List.copyOf(emptyDoubleFramedBlocks);
    }

    public List<ItemStack> getEmptyFramedBlocks()
    {
        return emptyFramedBlocks;
    }

    public ItemStack calculateOutput(ItemStack frame, ItemStack inputOne, ItemStack inputTwo)
    {
        ItemStack copyToolItem = helperRecipe.getCopyTool().display().resolveForFirstStack(makeSlotDisplayContext());
        CraftingInput craftingInput = CraftingInput.of(2, 2, List.of(frame, copyToolItem, inputOne, inputTwo));
        return helperRecipe.assemble(craftingInput);
    }

    private List<ItemStack> getCamoExampleStacks(Ingredient ingredient, int count)
    {
        if (ingredient.equals(fakeEmptyIngredient))
        {
            return List.of();
        }
        if (ingredient.equals(camoExamplesIngredient))
        {
            Collections.shuffle(camoExamples);
            if (count < this.camoExamples.size())
            {
                return new ArrayList<>(this.camoExamples.subList(0, count));
            }
            return new ArrayList<>(this.camoExamples);
        }
        return asStackList(ingredient);
    }

    private List<ItemStack> getDoubleCamoExampleStacks(Ingredient ingredient, int count)
    {
        if (ingredient.equals(fakeEmptyIngredient))
        {
            return List.of();
        }
        if (ingredient.equals(camoExamplesIngredient))
        {
            Collections.shuffle(this.camoExamples);
            List<ItemStack> results = new ArrayList<>();

            results.add(ItemStack.EMPTY);
            count--;

            if (count < this.camoExamples.size())
            {
                results.addAll(this.camoExamples.subList(0, count));
            }
            else
            {
                results.addAll(this.camoExamples);
            }
            return results;
        }
        return asStackList(ingredient);
    }

    private List<ItemStack> getEmptyFrameStacks(Ingredient ingredient)
    {
        if (ingredient.equals(fakeEmptyIngredient))
        {
            return List.of();
        }
        if (ingredient.equals(emptyFramesIngredient))
        {
            return emptyFramedBlocks;
        }
        else if (ingredient.equals(emptyDoubleFramesIngredient))
        {
            return emptyDoubleFramedBlocks;
        }
        return asStackList(ingredient);
    }

    public List<SlotDisplay> getIngredients(JeiCamoApplicationRecipe recipe)
    {
        return List.of(
                asSlotDisplay(getEmptyFrameStacks(recipe.frame())),
                recipe.copyTool().display(),
                asSlotDisplay(getCamoExampleStacks(recipe.camoOne(), 97)),
                asSlotDisplay(getDoubleCamoExampleStacks(recipe.camoTwo(), 11))
        );
    }

    public void setRecipe(JeiCamoApplicationRecipe recipe, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper)
    {
        List<Pair<String, List<ItemStack>>> namedInputs = List.of(
                Pair.of("frames", getEmptyFrameStacks(recipe.frame())),
                Pair.of("copyTool", asStackList(recipe.copyTool())),
                // pick a prime number count so that more combinations are shown over time
                Pair.of("camoOne", getCamoExampleStacks(recipe.camoOne(), 97)),
                // pick a lower number so that the blank ingredient is shown more often
                Pair.of("camoTwo", getDoubleCamoExampleStacks(recipe.camoTwo(), 11))
        );
        List<IRecipeSlotBuilder> inputSlots = craftingGridHelper.createAndSetNamedInputs(builder, namedInputs, 2, 2);

        IRecipeSlotRichTooltipCallback tooltipCallback = new InputSlotTooltipCallback();
        for (IRecipeSlotBuilder slotBuilder : inputSlots)
        {
            slotBuilder.addRichTooltipCallback(tooltipCallback);
        }

        Optional<ItemStack> result = recipe.result().map(ItemStackTemplate::create);
        if (result.isEmpty())
        {
            // For bookmarking, the recipe must have at least one known output.
            // Outputs are mostly calculated and displayed using onDisplayedIngredientsUpdate,
            // but we calculate one here to support bookmarking.
            List<ItemStack> frameStacks = namedInputs.get(0).getSecond();
            List<ItemStack> camoStackOne = namedInputs.get(2).getSecond();
            List<ItemStack> camoStackTwo = namedInputs.get(3).getSecond();
            ItemStack firstOutput = calculateOutput(
                    frameStacks.isEmpty() ? ItemStack.EMPTY : frameStacks.getFirst(),
                    camoStackOne.isEmpty() ? ItemStack.EMPTY : camoStackOne.getFirst(),
                    camoStackTwo.isEmpty() ? ItemStack.EMPTY : camoStackTwo.getFirst()
            );
            result = Optional.of(firstOutput);
        }
        craftingGridHelper.createAndSetOutputs(builder, List.of(result.get()));
    }

    private static List<ItemStack> asStackList(Ingredient ingredient)
    {
        ContextMap context = makeSlotDisplayContext();
        return ingredient.display().resolveForStacks(context);
    }

    private static SlotDisplay asSlotDisplay(List<ItemStack> stacks)
    {
        if (stacks.isEmpty()) return SlotDisplay.Empty.INSTANCE;

        List<SlotDisplay> displays = stacks.stream()
                .map(ItemStackTemplate::fromNonEmptyStack)
                .map(SlotDisplay.ItemStackSlotDisplay::new)
                .map(SlotDisplay.class::cast)
                .toList();
        return new SlotDisplay.Composite(displays);
    }

    public static Ingredient makeDummyIngredient(DummyIngredientType dummyType)
    {
        return new JeiCamoApplicationDummyIngredient(dummyType).toVanilla();
    }

    public static ContextMap makeSlotDisplayContext()
    {
        Level level = Objects.requireNonNull(Minecraft.getInstance().level);
        return SlotDisplayContext.fromLevel(level);
    }

    private static class InputSlotTooltipCallback implements IRecipeSlotRichTooltipCallback
    {
        @Override
        public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip)
        {
            recipeSlotView.getSlotName().ifPresent(name ->
            {
                if (name.equals("camoOne") || name.equals("camoTwo"))
                {
                    if (recipeSlotView.getItemStacks().count() > 1)
                    {
                        tooltip.clear();
                        tooltip.add(JeiCompat.MSG_SUPPORTS_MOST_CAMOS);
                    }
                }
            });
        }
    }
}
