// FIXME: RL->ID
/*package io.github.xfacthd.framedblocks.common.compat.emi;

import com.google.common.base.Stopwatch;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import io.github.xfacthd.framedblocks.FramedBlocks;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.screen.CamoApplicatorScreen;
import io.github.xfacthd.framedblocks.client.screen.FramingSawScreen;
import io.github.xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;
import io.github.xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCalculation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.List;
import java.util.Set;

@EmiEntrypoint
public final class FramedEmiPlugin implements EmiPlugin {
    public static final Identifier SAW_ID = Utils.id("framing_saw");
    private static final Lazy<EmiStack> SAW_WORKSTATION = Lazy.of(() -> EmiStack.of(FBContent.BLOCK_FRAMING_SAW.value()));
    public static final Lazy<EmiRecipeCategory> SAW_CATEGORY = Lazy.of(() -> new FramingSawRecipeCategory(SAW_WORKSTATION.get(), SAW_WORKSTATION.get()));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(SAW_CATEGORY.get());
        registry.addWorkstation(SAW_CATEGORY.get(), SAW_WORKSTATION.get());
        registry.addWorkstation(SAW_CATEGORY.get(), EmiStack.of(FBContent.BLOCK_POWERED_FRAMING_SAW.value()));
        registry.addRecipeHandler(FBContent.MENU_TYPE_FRAMING_SAW.get(), new FramedEmiRecipeHandler<>());
        registry.addRecipeHandler(FBContent.MENU_TYPE_POWERED_FRAMING_SAW.get(), new FramedEmiRecipeHandler<>());
        registry.addStackProvider(FramingSawScreen.class, new FramingSawStackProvider());
        registry.addStackProvider(PoweredFramingSawScreen.class, new PoweredFramingSawStackProvider());
        registry.addDragDropHandler(FramingSawWithEncoderScreen.class, new FramingSawDragDropHandler());
        registry.addDragDropHandler(PoweredFramingSawScreen.class, new PoweredFramingSawDragDropHandler());
        registry.addExclusionArea(FramingSawWithEncoderScreen.class, new FramingSawExclusionArea());
        registry.addExclusionArea(CamoApplicatorScreen.class, new CamoApplicatorExclusionArea());
        registerRecipes(registry);
    }

    private static void registerRecipes(EmiRegistry registry) {
        FramedBlocks.LOGGER.debug("Registering framing saw recipes to EMI...");
        Stopwatch watch = Stopwatch.createStarted();
        int[] recipeCount = new int[1];

        FramingSawRecipeCache cache = FramingSawRecipeCache.get(true);
        Set<Item> inputItems = ClientConfig.VIEW.showAllRecipePermutationsInEmi() ? cache.getKnownItems() : Set.of(FBContent.BLOCK_FRAMED_CUBE.value().asItem());
        cache.getRecipes().forEach(holder -> {
            FramingSawRecipe recipe = holder.value();
            for (Item item : inputItems) {
                if (recipe.getResult().is(item)) {
                    continue;
                }

                ItemStack inputStack = new ItemStack(item);
                RecipeInput dummyInput = new SingleRecipeInput(inputStack);
                FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(dummyInput, true);
                int outputCount = calc.getOutputCount();

                EmiStack input = EmiStack.of(inputStack, calc.getInputCount());
                List<EmiIngredient> additives = recipe.getAdditives()
                        .stream()
                        .map(additive -> {
                            int addCount = additive.count() * (outputCount / recipe.getResult().getCount());
                            return EmiIngredient.of(additive.ingredient(), addCount);
                        })
                        .toList();
                EmiStack output = EmiStack.of(recipe.getResult(), outputCount);
                registry.addRecipe(FramingSawEmiRecipe.make(holder, input, additives, output));

                recipeCount[0]++;
            }
        });

        watch.stop();
        FramedBlocks.LOGGER.debug("Registered {} framing saw recipes to EMI in {}", recipeCount[0], watch);
    }

    static int compareRecipes(EmiRecipe recipeOne, EmiRecipe recipeTwo) {
        ItemStack resultOne;
        ItemStack resultTwo;
        if (!(recipeOne instanceof FramingSawEmiRecipe sawRecipeOne) || (resultOne = sawRecipeOne.getOutputInternal()).isEmpty()) {
            return 1;
        }
        if (!(recipeTwo instanceof FramingSawEmiRecipe sawRecipeTwo) || (resultTwo = sawRecipeTwo.getOutputInternal()).isEmpty()) {
            return -1;
        }
        return FramingSawRecipeCache.sortRecipes(resultOne, resultTwo, sawRecipeOne.getResultType(), sawRecipeTwo.getResultType());
    }
}
*/