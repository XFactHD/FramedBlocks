package io.github.xfacthd.framedblocks.common.compat.jei.camo;

import com.mojang.datafixers.util.Either;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class CamoRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<RecipeHolder<CraftingRecipe>> {
    private final CamoCraftingHelper camoCraftingHelper;

    public CamoRecipeManagerPlugin(CamoCraftingHelper camoCraftingHelper) {
        this.camoCraftingHelper = camoCraftingHelper;
    }

    @Override
    public boolean isHandledInput(ITypedIngredient<?> ingredient) {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);
        if (CamoItemStackHelper.isEmptyFramedBlock(itemStack)) {
            return true;
        } else if (camoCraftingHelper.getCopyToolIngredient().test(itemStack)) {
            return true;
        }
        return CamoItemStackHelper.getCamoContainerFactory(itemStack) != null;
    }

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> ingredient) {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);
        if (CamoItemStackHelper.getFramedBlock(itemStack) != null) {
            int camoCount = CamoItemStackHelper.dropCamo(itemStack).size();
            return camoCount > 0 && camoCount <= 2;
        }
        return false;
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getRecipesForInput(ITypedIngredient<?> ingredient) {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);
        if (CamoItemStackHelper.isEmptyFramedBlock(itemStack)) {
            return List.of(createRecipeForFrame(itemStack));
        } else if (camoCraftingHelper.getCopyToolIngredient().test(itemStack)) {
            return createRecipesForEachFrame(camoCraftingHelper.getEmptyFramedBlocks());
        }

        CamoContainerFactory<?> containerFactory = CamoItemStackHelper.getCamoContainerFactory(itemStack);
        if (containerFactory != null) {
            RecipeHolder<CraftingRecipe> singleFrameRecipe = createRecipe(
                    StackOrDummy.EMPTY_FRAMES,
                    StackOrDummy.stack(itemStack),
                    StackOrDummy.EMPTY, // Ingredient no longer permits air, so we need to fake it this way instead
                    Optional.empty()
            );
            RecipeHolder<CraftingRecipe> doubleFrameRecipe = createRecipe(
                    StackOrDummy.EMPTY_DOUBLE_FRAMES,
                    StackOrDummy.stack(itemStack),
                    StackOrDummy.CAMO_EXAMPLES,
                    Optional.empty()
            );
            return List.of(singleFrameRecipe, doubleFrameRecipe);
        }

        return List.of();
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getRecipesForOutput(ITypedIngredient<?> ingredient) {
        ItemStack itemStack = ingredient.getItemStack().orElse(ItemStack.EMPTY);

        IFramedBlock framedBlock = CamoItemStackHelper.getFramedBlock(itemStack);
        if (framedBlock != null) {
            ItemStack plainFrame = new ItemStack(itemStack.getItem());
            List<ItemStack> camoBlocks = CamoItemStackHelper.dropCamo(itemStack);
            int camoCount = camoBlocks.size();
            if (camoCount == 1) {
                return List.of(createRecipe(
                        StackOrDummy.stack(plainFrame),
                        StackOrDummy.stack(camoBlocks.getFirst()),
                        StackOrDummy.EMPTY, // Ingredient no longer permits air, so we need to fake it this way instead
                        Optional.of(itemStack)
                ));
            } else if (camoCount == 2 && CamoItemStackHelper.isDoubleFramedBlock(framedBlock)) {
                return List.of(createRecipe(
                        StackOrDummy.stack(plainFrame),
                        StackOrDummy.stack(camoBlocks.get(0)),
                        StackOrDummy.stack(camoBlocks.get(1)),
                        Optional.of(itemStack)
                ));
            }
        }
        return List.of();
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getAllRecipes() {
        return createRecipesForEachFrame(camoCraftingHelper.getEmptyFramedBlocks());
    }

    private List<RecipeHolder<CraftingRecipe>> createRecipesForEachFrame(List<ItemStack> framedBlocks) {
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        for (ItemStack framedBlock : framedBlocks) {
            recipes.add(createRecipeForFrame(framedBlock));
        }
        return recipes;
    }

    private RecipeHolder<CraftingRecipe> createRecipeForFrame(ItemStack framedBlock) {
        // Ingredient no longer permits air, so we need to fake it this way instead
        StackOrDummy camoTwo = StackOrDummy.EMPTY;
        if (CamoItemStackHelper.isDoubleFramedBlock(framedBlock)) {
            camoTwo = StackOrDummy.CAMO_EXAMPLES;
        }
        // calculating the correct outputs here is impossible, leave them to be generated by
        // CamoCraftingRecipeExtension.onDisplayedIngredientsUpdate
        return createRecipe(StackOrDummy.stack(framedBlock), StackOrDummy.CAMO_EXAMPLES, camoTwo, Optional.empty());
    }

    private RecipeHolder<CraftingRecipe> createRecipe(StackOrDummy frame, StackOrDummy camoOne, StackOrDummy camoTwo, Optional<ItemStack> result) {
        Ingredient frameIngredient = frame.mapToIngredient();
        Ingredient copyTool = camoCraftingHelper.getCopyToolIngredient();
        Ingredient camoOneIngredient = camoOne.mapToIngredient();
        Ingredient secondInputStacks = camoTwo.mapToIngredient();
        JeiCamoApplicationRecipe recipe = new JeiCamoApplicationRecipe(frameIngredient, copyTool, camoOneIngredient, secondInputStacks, result.map(ItemStackTemplate::fromNonEmptyStack));

        Identifier recipeId = generateId(frame, camoOne, camoTwo);
        return new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, recipeId), recipe);
    }

    private static Identifier generateId(StackOrDummy frame, StackOrDummy camoOne, StackOrDummy camoTwo) {
        String frameId = frame.mapToString();
        String camoOneId = camoOne.mapToString();
        String camoTwoId = camoTwo.mapToString();
        return Utils.id("camo_application/jei_generated/" + frameId + "/" + camoOneId + "/" + camoTwoId);
    }

    private record StackOrDummy(Either<ItemStack, DummyIngredientType> value) {
        public static final StackOrDummy EMPTY = StackOrDummy.dummy(DummyIngredientType.EMPTY);
        public static final StackOrDummy CAMO_EXAMPLES = StackOrDummy.dummy(DummyIngredientType.CAMO_EXAMPLES);
        public static final StackOrDummy EMPTY_FRAMES = StackOrDummy.dummy(DummyIngredientType.EMPTY_FRAMES);
        public static final StackOrDummy EMPTY_DOUBLE_FRAMES = StackOrDummy.dummy(DummyIngredientType.EMPTY_DOUBLE_FRAMES);

        public <T> T map(Function<ItemStack, T> stackMapper, Function<DummyIngredientType, T> dummyMapper) {
            return value.map(stackMapper, dummyMapper);
        }

        public String mapToString() {
            return map(StackOrDummy::stackToString, DummyIngredientType::getSerializedName);
        }

        public Ingredient mapToIngredient() {
            // TODO: check how discarding stack data influences recipe display with things like fluid tanks
            return map(stack -> Ingredient.of(stack.getItem()), DummyIngredientType::toIngredient);
        }

        public static StackOrDummy stack(ItemStack stack) {
            return new StackOrDummy(Either.left(stack));
        }

        public static StackOrDummy dummy(DummyIngredientType dummyType) {
            return new StackOrDummy(Either.right(dummyType));
        }

        private static String stackToString(ItemStack stack) {
            return stack.isEmpty() ? "empty" : BuiltInRegistries.ITEM.getKey(stack.getItem()).toLanguageKey();
        }
    }
}
