package xfacthd.framedblocks.common.compat.emi;

import com.google.common.base.Stopwatch;
import dev.emi.emi.api.*;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.screen.FramingSawScreen;
import xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import xfacthd.framedblocks.common.config.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.*;

import java.util.List;
import java.util.Set;

@EmiEntrypoint
public final class FramedEmiPlugin implements EmiPlugin
{
    public static final EmiStack SAW_WORKSTATION = EmiStack.of(FBContent.BLOCK_FRAMING_SAW.value());
    private static final EmiStack POWERED_SAW_WORKSTATION = EmiStack.of(FBContent.BLOCK_POWERED_FRAMING_SAW.value());
    public static final EmiRecipeCategory SAW_CATEGORY = new FramingSawRecipeCategory(SAW_WORKSTATION, SAW_WORKSTATION);
    private static final Set<Item> CUBE_ITEM = Set.of(FBContent.BLOCK_FRAMED_CUBE.value().asItem());

    @Override
    public void register(EmiRegistry registry)
    {
        registry.addCategory(SAW_CATEGORY);
        registry.addWorkstation(SAW_CATEGORY, SAW_WORKSTATION);
        registry.addWorkstation(SAW_CATEGORY, POWERED_SAW_WORKSTATION);
        registry.addRecipeHandler(FBContent.MENU_TYPE_FRAMING_SAW.get(), new FramedEmiRecipeHandler<>());
        registry.addRecipeHandler(FBContent.MENU_TYPE_POWERED_FRAMING_SAW.get(), new FramedEmiRecipeHandler<>());
        registry.addStackProvider(FramingSawScreen.class, new FramingSawStackProvider());
        registry.addStackProvider(PoweredFramingSawScreen.class, new PoweredFramingSawStackProvider());
        registry.addDragDropHandler(PoweredFramingSawScreen.class, new PoweredFramingSawDragDropHandler());
        registerRecipes(registry);
    }

    private static void registerRecipes(EmiRegistry registry)
    {
        FramedBlocks.LOGGER.debug("Registering framing saw recipes to EMI...");
        Stopwatch watch = Stopwatch.createStarted();
        int[] recipeCount = new int[1];

        FramingSawRecipeCache cache = FramingSawRecipeCache.get(true);
        Set<Item> inputItems = ClientConfig.VIEW.showAllRecipePermutationsInEmi() ? cache.getKnownItems() : CUBE_ITEM;
        Container dummyContainer = new SimpleContainer(1);
        cache.getRecipes().forEach(holder ->
        {
            FramingSawRecipe recipe = holder.value();
            for (Item item : inputItems)
            {
                if (recipe.getResult().is(item))
                {
                    continue;
                }

                ItemStack inputStack = new ItemStack(item);
                dummyContainer.setItem(0, inputStack);
                FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(dummyContainer, true);
                int outputCount = calc.getOutputCount();

                EmiStack input = EmiStack.of(inputStack, calc.getInputCount());
                List<EmiIngredient> additives = recipe.getAdditives()
                        .stream()
                        .map(additive ->
                        {
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

    static int compareRecipes(EmiRecipe recipeOne, EmiRecipe recipeTwo)
    {
        ItemStack resultOne;
        ItemStack resultTwo;
        if (!(recipeOne instanceof FramingSawEmiRecipe sawRecipeOne) || (resultOne = sawRecipeOne.getOutputInternal()).isEmpty())
        {
            return 1;
        }
        if (!(recipeTwo instanceof FramingSawEmiRecipe sawRecipeTwo) || (resultTwo = sawRecipeTwo.getOutputInternal()).isEmpty())
        {
            return -1;
        }
        return FramingSawRecipeCache.sortRecipes(resultOne, resultTwo, sawRecipeOne.getResultType(), sawRecipeTwo.getResultType());
    }
}
