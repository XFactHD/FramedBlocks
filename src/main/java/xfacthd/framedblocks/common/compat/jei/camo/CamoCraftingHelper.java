package xfacthd.framedblocks.common.compat.jei.camo;

import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.util.Utils;
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
        this.helperRecipe = new CamoApplicationRecipe(CraftingBookCategory.MISC, Ingredient.of(Items.BRUSH));
        this.camoExamplesIngredient = Ingredient.of(JEI_CAMO_BLOCK_EXAMPLES);
        this.emptyFramesIngredient = Ingredient.of(JEI_CAMO_EMPTY_FRAMES);
        this.emptyDoubleFramesIngredient = Ingredient.of(JEI_CAMO_EMPTY_DOUBLE_FRAMES);
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

    public List<ItemStack> getEmptyDoubleFramedBlocks()
    {
        return emptyDoubleFramedBlocks;
    }

    public ItemStack calculateOutput(ItemStack frame, ItemStack inputOne, ItemStack inputTwo)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        assert level != null;
        RegistryAccess registryAccess = level.registryAccess();

        List<ItemStack> inputs = List.of(frame, new ItemStack(Items.BRUSH), inputOne, inputTwo);
        CraftingInput craftingInput = CraftingInput.of(2, 2, inputs);
        return helperRecipe.assemble(craftingInput, registryAccess);
    }

    public List<ItemStack> getCamoExampleStacks(Ingredient ingredient, int count)
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

    public List<ItemStack> getDoubleCamoExampleStacks(Ingredient ingredient, int count)
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

    public List<ItemStack> getEmptyFrameStacks(Ingredient ingredient)
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
}
