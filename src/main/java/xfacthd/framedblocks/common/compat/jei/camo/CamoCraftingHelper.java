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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CamoCraftingHelper
{
    /**
     * Empty tag to help with serialization of the fake recipes, this gets replaced dynamically for displaying in JEI
     */
    private static final TagKey<Item> JEI_CAMO_BLOCK_EXAMPLES = Utils.itemTag("jei_camo_block_examples");
    private static final int MAX_CAMO_EXAMPLE_INGREDIENTS_COUNT = 100;

    private final CamoApplicationRecipe helperRecipe;
    private final Ingredient camoExamplesIngredient;
    private List<ItemStack> camoExamples = new ArrayList<>();
    private List<ItemStack> emptyFramedBlocks = new ArrayList<>();

    public CamoCraftingHelper()
    {
        this.helperRecipe = new CamoApplicationRecipe(CraftingBookCategory.MISC, Ingredient.of(Items.BRUSH));
        this.camoExamplesIngredient = Ingredient.of(JEI_CAMO_BLOCK_EXAMPLES);
    }

    public void scanForItems(IIngredientManager ingredientManager)
    {
        List<ItemStack> camoExamples = new ArrayList<>();
        List<ItemStack> framedBlocks = new ArrayList<>();

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
                framedBlocks.add(stack);
            }
        }

        this.camoExamples = Collections.unmodifiableList(camoExamples);
        this.emptyFramedBlocks = Collections.unmodifiableList(framedBlocks);
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

        List<ItemStack> inputs = List.of(frame, new ItemStack(Items.BRUSH), inputOne, inputTwo);
        CraftingInput craftingInput = CraftingInput.of(2, 2, inputs);
        return helperRecipe.assemble(craftingInput, registryAccess);
    }

    public List<ItemStack> getCamoExampleStacks(Ingredient ingredient)
    {
        if (ingredient.equals(camoExamplesIngredient))
        {
            List<ItemStack> camoExamples = new ArrayList<>(this.camoExamples);
            Collections.shuffle(camoExamples);
            return camoExamples;
        }

        return Arrays.asList(ingredient.getItems());
    }

    public Ingredient getCamoExamplesIngredient()
    {
        return camoExamplesIngredient;
    }

}
