package xfacthd.framedblocks.common.compat.jei.camo;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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

    public CamoRecipeManagerPlugin(CamoCraftingHelper camoCraftingHelper)
    {
        this.camoCraftingHelper = camoCraftingHelper;
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
                    Either.right(JeiCamoTags.getCamoBlockExamplesTag())
            );
            return List.of(recipe);
        }
        else if (camoCraftingHelper.getCopyToolIngredient().test(itemStack))
        {
            return createRecipesForEachFrame(
                    camoCraftingHelper.getEmptyFramedBlocks(),
                    Either.right(JeiCamoTags.getCamoBlockExamplesTag())
            );
        }

        CamoContainerFactory<?> containerFactory = CamoItemStackHelper.getCamoContainerFactory(itemStack);
        if (containerFactory != null)
        {
            RecipeHolder<CraftingRecipe> singleFrameRecipe = createRecipe(
                    Either.right(JeiCamoTags.getAllFramesTag()),
                    Either.left(itemStack),
                    Either.left(ItemStack.EMPTY),
                    List.of()
            );
            RecipeHolder<CraftingRecipe> doubleFrameRecipe = createRecipe(
                    Either.right(JeiCamoTags.getDoubleFramesTag()),
                    Either.left(itemStack),
                    Either.right(JeiCamoTags.getCamoBlockExamplesTag()),
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
                        Either.left(plainFrame),
                        Either.left(camoBlocks.getFirst()),
                        Either.left(ItemStack.EMPTY),
                        List.of(itemStack)
                );
                return List.of(recipe);
            }
            else if (camoCount == 2 && CamoItemStackHelper.isDoubleFramedBlock(framedBlock))
            {
                RecipeHolder<CraftingRecipe> recipe = createRecipe(
                        Either.left(plainFrame),
                        Either.left(camoBlocks.get(0)),
                        Either.left(camoBlocks.get(1)),
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
                Either.right(JeiCamoTags.getCamoBlockExamplesTag())
        );
    }

    private List<RecipeHolder<CraftingRecipe>> createRecipesForEachFrame(
            List<ItemStack> framedBlocks,
            Either<ItemStack, TagKey<Item>> camoOne
    )
    {
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        for (ItemStack framedBlock : framedBlocks)
        {
            RecipeHolder<CraftingRecipe> singleRecipe = createRecipeForFrame(framedBlock, camoOne);
            recipes.add(singleRecipe);
        }

        return recipes;
    }

    private RecipeHolder<CraftingRecipe> createRecipeForFrame(
            ItemStack framedBlock,
            Either<ItemStack, TagKey<Item>> camoOne
    )
    {
        Either<ItemStack, TagKey<Item>> camoTwo = Either.left(ItemStack.EMPTY);
        if (CamoItemStackHelper.isDoubleFramedBlock(framedBlock))
        {
            camoTwo = Either.right(JeiCamoTags.getCamoBlockExamplesTag());
        }
        // calculating the correct outputs here is impossible, leave them to be generated by
        // CamoCraftingRecipeExtension.onDisplayedIngredientsUpdate
        return createRecipe(
                Either.left(framedBlock),
                camoOne,
                camoTwo,
                List.of()
        );
    }

    private RecipeHolder<CraftingRecipe> createRecipe(
            Either<ItemStack, TagKey<Item>> frame,
            Either<ItemStack, TagKey<Item>> camoOne,
            Either<ItemStack, TagKey<Item>> camoTwo,
            List<ItemStack> results
    )
    {
        Ingredient frameIngredient = frame.map(Ingredient::of, Ingredient::of);
        Ingredient copyTool = camoCraftingHelper.getCopyToolIngredient();
        Ingredient camoOneIngredient = camoOne.map(Ingredient::of, Ingredient::of);
        Ingredient secondInputStacks = camoTwo.map(Ingredient::of, Ingredient::of);
        JeiCamoApplicationRecipe recipe = new JeiCamoApplicationRecipe(frameIngredient, copyTool, camoOneIngredient, secondInputStacks, results);

        ResourceLocation resourceLocation = generateId(frame, camoOne, camoTwo);
        return new RecipeHolder<>(resourceLocation, recipe);
    }

    private static ResourceLocation generateId(Either<ItemStack, TagKey<Item>> frame, Either<ItemStack, TagKey<Item>> camoOne, Either<ItemStack, TagKey<Item>> camoTwo)
    {
        String frameId = mapStackOrTag(frame, List.of(
                Pair.of(JeiCamoTags.getAllFramesTag(), "all"),
                Pair.of(JeiCamoTags.getDoubleFramesTag(), "all_double")
        ));
        String camoOneId = mapStackOrTag(camoOne, List.of(
                Pair.of(JeiCamoTags.getCamoBlockExamplesTag(), "examples")
        ));
        String camoTwoId = mapStackOrTag(camoTwo, List.of(
                Pair.of(JeiCamoTags.getCamoBlockExamplesTag(), "examples")
        ));
        return Utils.rl("camo_application/jei_generated/" + frameId + "/" + camoOneId + "/" + camoTwoId);
    }

    private static String mapStackOrTag(Either<ItemStack, TagKey<Item>> value, List<Pair<TagKey<Item>, String>> converters)
    {
        return value.map(
                CamoRecipeManagerPlugin::stackToString,
                tag -> tagToString(tag, converters)
        );
    }

    private static String stackToString(ItemStack itemStack)
    {
        if (itemStack.isEmpty())
        {
            return "empty";
        }
        return BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toLanguageKey();
    }

    private static String tagToString(TagKey<Item> tag, List<Pair<TagKey<Item>, String>> converters)
    {
        for (Pair<TagKey<Item>, String> conv : converters)
        {
            if (conv.getFirst().equals(tag))
            {
                return conv.getSecond();
            }
        }
        return Utils.sanitizePath(tag.location().toString());
    }
}
