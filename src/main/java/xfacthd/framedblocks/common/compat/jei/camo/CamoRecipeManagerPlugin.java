package xfacthd.framedblocks.common.compat.jei.camo;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.util.Utils;

import java.util.ArrayList;
import java.util.List;

public final class CamoRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<RecipeHolder<CraftingRecipe>>
{
    private final CamoCraftingHelper camoCraftingHelper;
    private final RegistryAccess registryAccess;

    public CamoRecipeManagerPlugin(CamoCraftingHelper camoCraftingHelper)
    {
        this.camoCraftingHelper = camoCraftingHelper;
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        assert level != null;
        this.registryAccess = level.registryAccess();
    }

    @Override
    public boolean isHandledInput(ITypedIngredient<?> ingredient)
    {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);
        if (CamoItemStackHelper.isEmptyFramedBlock(itemStack))
        {
            return true;
        }
        else if (camoCraftingHelper.getCopyToolIngredient().test(itemStack))
        {
            return true;
        }
        return CamoItemStackHelper.getCamoContainerFactory(itemStack) != null;
    }

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> ingredient)
    {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);
        IFramedBlock framedBlock = CamoItemStackHelper.getFramedBlock(itemStack);
        if (framedBlock != null)
        {
            long camoCount = CamoItemStackHelper.dropCamo(itemStack).size();
            return camoCount > 0 && camoCount <= 2;
        }
        return false;
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getRecipesForInput(ITypedIngredient<?> ingredient)
    {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);
        if (CamoItemStackHelper.isEmptyFramedBlock(itemStack))
        {
            RecipeHolder<CraftingRecipe> recipe = createRecipeForFrame(
                    itemStack,
                    camoCraftingHelper.getCamoExamplesIngredient()
            );
            return List.of(recipe);
        }
        else if (camoCraftingHelper.getCopyToolIngredient().test(itemStack))
        {
            return createRecipesForEachFrame(
                    camoCraftingHelper.getEmptyFramedBlocks(),
                    camoCraftingHelper.getCamoExamplesIngredient()
            );
        }

        CamoContainerFactory<?> containerFactory = CamoItemStackHelper.getCamoContainerFactory(itemStack);
        if (containerFactory != null)
        {
            RecipeHolder<CraftingRecipe> singleFrameRecipe = createRecipe(
                    camoCraftingHelper.getEmptyFramesIngredient(),
                    Ingredient.of(itemStack),
                    Ingredient.EMPTY,
                    List.of()
            );
            RecipeHolder<CraftingRecipe> doubleFrameRecipe = createRecipe(
                    camoCraftingHelper.getEmptyDoubleFramesIngredient(),
                    Ingredient.of(itemStack),
                    camoCraftingHelper.getCamoExamplesIngredient(),
                    List.of()
            );
            return List.of(singleFrameRecipe, doubleFrameRecipe);
        }

        return List.of();
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getRecipesForOutput(ITypedIngredient<?> ingredient)
    {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);

        IFramedBlock framedBlock = CamoItemStackHelper.getFramedBlock(itemStack);
        if (framedBlock != null)
        {
            ItemStack plainFrame = new ItemStack(itemStack.getItem());
            List<ItemStack> camoBlocks = CamoItemStackHelper.dropCamo(itemStack);
            int camoCount = camoBlocks.size();
            if (camoCount == 1)
            {
                RecipeHolder<CraftingRecipe> recipe = createRecipe(
                        Ingredient.of(plainFrame),
                        Ingredient.of(camoBlocks.stream()),
                        Ingredient.EMPTY,
                        List.of(itemStack)
                );
                return List.of(recipe);
            }
            else if (camoCount == 2 && CamoItemStackHelper.isDoubleFramedBlock(framedBlock))
            {
                RecipeHolder<CraftingRecipe> recipe = createRecipe(
                        Ingredient.of(plainFrame),
                        Ingredient.of(camoBlocks.get(0)),
                        Ingredient.of(camoBlocks.get(1)),
                        List.of(itemStack)
                );
                return List.of(recipe);
            }
        }
        return List.of();
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getAllRecipes()
    {
        return createRecipesForEachFrame(
                camoCraftingHelper.getEmptyFramedBlocks(),
                camoCraftingHelper.getCamoExamplesIngredient()
        );
    }

    private List<RecipeHolder<CraftingRecipe>> createRecipesForEachFrame(
            List<ItemStack> framedBlocks,
            Ingredient firstInputs
    )
    {
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        for (ItemStack framedBlock : framedBlocks)
        {
            RecipeHolder<CraftingRecipe> singleRecipe = createRecipeForFrame(
                    framedBlock,
                    firstInputs
            );
            recipes.add(singleRecipe);
        }

        return recipes;
    }

    private RecipeHolder<CraftingRecipe> createRecipeForFrame(
            ItemStack framedBlock,
            Ingredient firstInputStacks
    )
    {
        Ingredient secondInputs = Ingredient.EMPTY;
        if (CamoItemStackHelper.isDoubleFramedBlock(framedBlock))
        {
            secondInputs = camoCraftingHelper.getCamoExamplesIngredient();
        }
        // calculating the correct outputs here is impossible, leave them to be generated by
        // CamoCraftingRecipeExtension.onDisplayedIngredientsUpdate
        return createRecipe(
                Ingredient.of(framedBlock),
                firstInputStacks,
                secondInputs,
                List.of()
        );
    }

    private RecipeHolder<CraftingRecipe> createRecipe(
            Ingredient frameStacks,
            Ingredient firstInputStacks,
            Ingredient secondInputStacks,
            List<ItemStack> results
    )
    {
        Ingredient copyTool = camoCraftingHelper.getCopyToolIngredient();
        JeiCamoApplicationRecipe recipe = new JeiCamoApplicationRecipe(frameStacks, copyTool, firstInputStacks, secondInputStacks, results);
        ResourceLocation resourceLocation = generateId(recipe);
        return new RecipeHolder<>(resourceLocation, recipe);
    }

    private ResourceLocation generateId(JeiCamoApplicationRecipe recipe)
    {
        RegistryOps<JsonElement> registryOps = registryAccess.createSerializationContext(JsonOps.INSTANCE);
        DataResult<JsonElement> result = JeiCamoApplicationRecipeSerializer.CODEC.codec().encodeStart(registryOps, recipe);
        JsonElement element = result.getOrThrow();
        String elementString = element.toString();
        return Utils.rlSanitizePath(elementString);
    }
}
