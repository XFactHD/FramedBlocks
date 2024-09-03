package xfacthd.framedblocks.common.compat.jei.camo;

import com.mojang.datafixers.util.Pair;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.compat.jei.JeiMessages;
import xfacthd.framedblocks.common.crafting.CamoApplicationRecipe;

import java.util.*;

public final class CamoCraftingHelper
{
    /**
     * Empty tag to help with serialization of the fake recipes, this gets replaced dynamically for displaying in JEI
     */
    private static final TagKey<Item> JEI_CAMO_BLOCK_EXAMPLES = Utils.itemTag("jei_camo_block_examples");
    private static final TagKey<Item> JEI_CAMO_EMPTY_FRAMES = Utils.itemTag("jei_camo_empty_frames");
    private static final TagKey<Item> JEI_CAMO_EMPTY_DOUBLE_FRAMES = Utils.itemTag("jei_camo_empty_double_frames");
    private static final int MAX_CAMO_EXAMPLE_INGREDIENTS_COUNT = 100;

    private final CamoApplicationRecipe helperRecipe;
    private final Ingredient camoExamplesIngredient;
    private final Ingredient emptyFramesIngredient;
    private final Ingredient emptyDoubleFramesIngredient;
    private List<ItemStack> camoExamples = new ArrayList<>();
    private List<ItemStack> emptyFramedBlocks = new ArrayList<>();
    private List<ItemStack> emptyDoubleFramedBlocks = new ArrayList<>();

    public CamoCraftingHelper()
    {
        CamoApplicationRecipe applicationRecipe = CamoApplicationRecipe.getInstance();
        this.helperRecipe = Objects.requireNonNullElseGet(applicationRecipe, () -> new CamoApplicationRecipe(CraftingBookCategory.MISC, Ingredient.of(Items.BRUSH)));
        this.camoExamplesIngredient = Ingredient.of(JEI_CAMO_BLOCK_EXAMPLES);
        this.emptyFramesIngredient = Ingredient.of(JEI_CAMO_EMPTY_FRAMES);
        this.emptyDoubleFramesIngredient = Ingredient.of(JEI_CAMO_EMPTY_DOUBLE_FRAMES);
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

        this.camoExamples = camoExamples;
        this.emptyFramedBlocks = Collections.unmodifiableList(emptyFramedBlocks);
        this.emptyDoubleFramedBlocks = Collections.unmodifiableList(emptyDoubleFramedBlocks);
    }

    public List<ItemStack> getEmptyFramedBlocks()
    {
        return emptyFramedBlocks;
    }

    public ItemStack calculateOutput(ItemStack frame, ItemStack inputOne, ItemStack inputTwo)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        assert level != null;
        RegistryAccess registryAccess = level.registryAccess();

        Ingredient copyTool = helperRecipe.getCopyTool();
        ItemStack copyToolItem = copyTool.getItems()[0];
        List<ItemStack> inputs = List.of(frame, copyToolItem, inputOne, inputTwo);
        CraftingInput craftingInput = CraftingInput.of(2, 2, inputs);
        return helperRecipe.assemble(craftingInput, registryAccess);
    }

    public Ingredient getCamoExamplesIngredient()
    {
        return camoExamplesIngredient;
    }

    public Ingredient getEmptyFramesIngredient()
    {
        return emptyFramesIngredient;
    }

    public Ingredient getEmptyDoubleFramesIngredient()
    {
        return emptyDoubleFramesIngredient;
    }

    public List<List<ItemStack>> getDisplayInputs(JeiCamoApplicationRecipe recipe)
    {
        return List.of(
                getEmptyFrameStacks(recipe.getFrameStacks()),
                Arrays.asList(recipe.getCopyTool().getItems()),
                // pick a prime number count so that more combinations are shown over time
                getCamoExampleStacks(recipe.getFirstIngredient(), 97),
                // pick a lower number so that the blank ingredient is shown more often
                getDoubleCamoExampleStacks(recipe.getSecondIngredient(), 11)
        );
    }

    private List<ItemStack> getCamoExampleStacks(Ingredient ingredient, int count)
    {
        if (ingredient.equals(camoExamplesIngredient))
        {
            Collections.shuffle(this.camoExamples);
            if (count < this.camoExamples.size())
            {
                return new ArrayList<>(this.camoExamples.subList(0, count));
            }
            return new ArrayList<>(this.camoExamples);
        }

        return Arrays.asList(ingredient.getItems());
    }

    private List<ItemStack> getDoubleCamoExampleStacks(Ingredient ingredient, int count)
    {
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

        return Arrays.asList(ingredient.getItems());
    }

    private List<ItemStack> getEmptyFrameStacks(Ingredient ingredient)
    {
        if (ingredient.equals(emptyFramesIngredient))
        {
            return emptyFramedBlocks;
        }
        else if (ingredient.equals(emptyDoubleFramesIngredient))
        {
            return emptyDoubleFramedBlocks;
        }

        return Arrays.asList(ingredient.getItems());
    }

    public void setRecipe(JeiCamoApplicationRecipe recipe, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper)
    {
        List<Pair<String, List<ItemStack>>> namedInputs = List.of(
                Pair.of("frames", getEmptyFrameStacks(recipe.getFrameStacks())),
                Pair.of("copyTool", Arrays.asList(recipe.getCopyTool().getItems())),
                // pick a prime number count so that more combinations are shown over time
                Pair.of("camoOne", getCamoExampleStacks(recipe.getFirstIngredient(), 97)),
                // pick a lower number so that the blank ingredient is shown more often
                Pair.of("camoTwo", getDoubleCamoExampleStacks(recipe.getSecondIngredient(), 11))
        );
        List<List<ItemStack>> inputs = getDisplayInputs(recipe);
        List<IRecipeSlotBuilder> inputSlots = craftingGridHelper.createAndSetNamedInputs(builder, namedInputs, 2, 2);

        IRecipeSlotRichTooltipCallback tooltipCallback = new InputSlotTooltipCallback();
        for (IRecipeSlotBuilder slotBuilder : inputSlots)
        {
            slotBuilder.addRichTooltipCallback(tooltipCallback);
        }

        List<ItemStack> results = recipe.getResults();
        if (results.isEmpty())
        {
            // For bookmarking, the recipe must have at least one known output.
            // Outputs are mostly calculated displayed using onDisplayedIngredientsUpdate,
            // but we calculate one here to support bookmarking.
            List<ItemStack> frameStacks = inputs.get(0);
            List<ItemStack> camoStackOne = inputs.get(2);
            List<ItemStack> camoStackTwo = inputs.get(3);
            ItemStack firstOutput = calculateOutput(
                    frameStacks.isEmpty() ? ItemStack.EMPTY : frameStacks.getFirst(),
                    camoStackOne.isEmpty() ? ItemStack.EMPTY : camoStackOne.getFirst(),
                    camoStackTwo.isEmpty() ? ItemStack.EMPTY : camoStackTwo.getFirst()
            );
            results = List.of(firstOutput);
        }
        craftingGridHelper.createAndSetOutputs(builder, results);
    }

    private static class InputSlotTooltipCallback implements IRecipeSlotRichTooltipCallback
    {
        @Override
        public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip)
        {
            recipeSlotView.getSlotName()
                    .ifPresent(name -> {
                        if (name.equals("camoOne") || name.equals("camoTwo"))
                        {
                            if (recipeSlotView.getItemStacks().count() > 1)
                            {
                                tooltip.clear();
                                MutableComponent component = JeiMessages.MSG_SUPPORTS_MOST_CAMOS.copy()
                                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
                                tooltip.add(component);
                            }
                        }
                    });
        }
    }
}
