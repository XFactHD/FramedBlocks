package xfacthd.framedblocks.common.compat.jei.camo;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.util.FramedConstants;

import java.util.ArrayList;
import java.util.List;

public final class CamoRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<RecipeHolder<CraftingRecipe>>
{
    private final Ingredient copyToolIngredient;
    private final CamoCraftingHelper camoCraftingHelper;
    private int generatedRecipeCount;

    public CamoRecipeManagerPlugin(CamoCraftingHelper camoCraftingHelper)
    {
        this.camoCraftingHelper = camoCraftingHelper;
        this.copyToolIngredient = Ingredient.of(new ItemStack(Items.BRUSH));
    }

    @Override
    public boolean isHandledInput(ITypedIngredient<?> ingredient)
    {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);
        if (camoCraftingHelper.getFramedBlock(itemStack) != null)
        {
            return true;
        }
        else if (copyToolIngredient.test(itemStack))
        {
            return true;
        }
        return CamoCraftingHelper.getCamoContainerFactory(itemStack) != null;
    }

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> ingredient)
    {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);
        IFramedBlock framedBlock = camoCraftingHelper.getFramedBlock(itemStack);
        if (framedBlock != null)
        {
            long camoCount = camoCraftingHelper.dropCamo(itemStack).count();
            return camoCount > 0 && camoCount <= 2;
        }
        return false;
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getRecipesForInput(ITypedIngredient<?> ingredient)
    {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);
        IFramedBlock framedBlock = camoCraftingHelper.getFramedBlock(itemStack);
        if (framedBlock != null)
        {
            return createRepresentativeRecipes(
                    List.of(itemStack),
                    camoCraftingHelper.getCamoExamplesIngredient(),
                    List.of()
            );
        }
        else if (copyToolIngredient.test(itemStack))
        {
            return createRepresentativeRecipes(
                    camoCraftingHelper.getFramedBlocks(),
                    camoCraftingHelper.getCamoExamplesIngredient(),
                    List.of()
            );
        }

        CamoContainerFactory<?> containerFactory = CamoCraftingHelper.getCamoContainerFactory(itemStack);
        if (containerFactory != null)
        {
            return createRepresentativeRecipes(
                    camoCraftingHelper.getFramedBlocks(),
                    Ingredient.of(itemStack),
                    List.of()
            );
        }

        return List.of();
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getRecipesForOutput(ITypedIngredient<?> ingredient)
    {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);

        IFramedBlock framedBlock = camoCraftingHelper.getFramedBlock(itemStack);
        if (framedBlock != null)
        {
            ItemStack plainFrame = new ItemStack(itemStack.getItem());
            List<ItemStack> camoBlocks = camoCraftingHelper.dropCamo(itemStack).toList();
            int camoCount = camoBlocks.size();
            if (camoCount == 1)
            {
                return createRepresentativeRecipes(
                        List.of(plainFrame),
                        Ingredient.of(camoBlocks.stream()),
                        List.of(itemStack)
                );
            }
            else if (camoCount == 2)
            {
                return createRepresentativeDoubleRecipes(
                        List.of(plainFrame),
                        Ingredient.of(camoBlocks.get(0)),
                        Ingredient.of(camoBlocks.get(1)),
                        List.of(itemStack)
                );
            }
        }
        return List.of();
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getAllRecipes()
    {
        return createRepresentativeRecipes(
                camoCraftingHelper.getFramedBlocks(),
                camoCraftingHelper.getCamoExamplesIngredient(),
                List.of()
        );
    }

    private List<RecipeHolder<CraftingRecipe>> createRepresentativeRecipes(
            List<ItemStack> framedBlocks,
            Ingredient firstInputs,
            List<ItemStack> optionalOutputs
    )
    {
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        for (ItemStack framedBlock : framedBlocks)
        {
            // calculating the correct outputs here is impossible, leave them to be generated by
            // CamoCraftingRecipeExtension.onDisplayedIngredientsUpdate
            RecipeHolder<CraftingRecipe> singleRecipe = createRecipe(
                    Ingredient.of(framedBlock),
                    copyToolIngredient,
                    firstInputs,
                    Ingredient.EMPTY,
                    optionalOutputs
            );
            recipes.add(singleRecipe);

            if (camoCraftingHelper.isDoubleFramedBlock(framedBlock))
            {
                // calculating the correct outputs here is impossible, leave them to be generated by
                // CamoCraftingRecipeExtension.onDisplayedIngredientsUpdate
                RecipeHolder<CraftingRecipe> doubleRecipe = createRecipe(
                        Ingredient.of(framedBlock),
                        copyToolIngredient,
                        firstInputs,
                        camoCraftingHelper.getCamoExamplesIngredient(),
                        optionalOutputs
                );
                recipes.add(doubleRecipe);
            }
        }

        return recipes;
    }

    private List<RecipeHolder<CraftingRecipe>> createRepresentativeDoubleRecipes(
            List<ItemStack> framedBlocks,
            Ingredient firstInputs,
            Ingredient secondInputs,
            List<ItemStack> optionalOutputs
    )
    {
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        for (ItemStack framedBlock : framedBlocks)
        {
            // calculating the correct outputs here is impossible, leave them to be generated by
            // CamoCraftingRecipeExtension.onDisplayedIngredientsUpdate
            RecipeHolder<CraftingRecipe> doubleRecipe = createRecipe(
                    Ingredient.of(framedBlock),
                    copyToolIngredient,
                    firstInputs,
                    secondInputs,
                    optionalOutputs
            );
            recipes.add(doubleRecipe);
        }

        return recipes;
    }

    private RecipeHolder<CraftingRecipe> createRecipe(
            Ingredient frameStacks,
            Ingredient copyTool,
            Ingredient firstInputStacks,
            Ingredient secondInputStacks,
            List<ItemStack> results
    )
    {
        JeiCamoCraftingRecipe recipe = new JeiCamoCraftingRecipe(frameStacks, copyTool, firstInputStacks, secondInputStacks, results);
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(FramedConstants.MOD_ID, "generated_recipe_" + generatedRecipeCount++);
        return new RecipeHolder<>(resourceLocation, recipe);
    }

}
