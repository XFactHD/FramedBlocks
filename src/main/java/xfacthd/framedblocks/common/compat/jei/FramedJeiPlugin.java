package xfacthd.framedblocks.common.compat.jei;

import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.IExtendableCraftingRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.screen.FramingSawScreen;
import xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;
import xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.compat.jei.camo.CamoCraftingHelper;
import xfacthd.framedblocks.common.compat.jei.camo.CamoCraftingRecipeExtension;
import xfacthd.framedblocks.common.compat.jei.camo.CamoRecipeManagerPlugin;
import xfacthd.framedblocks.common.compat.jei.camo.JeiCamoApplicationRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;

@JeiPlugin
@REIPluginCompatIgnore
public final class FramedJeiPlugin implements IModPlugin
{
    private static final ResourceLocation ID = Utils.rl("jei_plugin");
    static final RecipeType<FramingSawRecipe> FRAMING_SAW_RECIPE_TYPE = new RecipeType<>(
            Utils.rl("framing_saw"), FramingSawRecipe.class
    );
    private static final CamoCraftingHelper camoCraftingHelper = new CamoCraftingHelper();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        registration.addRecipeCategories(new FramingSawRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration)
    {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IIngredientManager ingredientManager = jeiHelpers.getIngredientManager();
        camoCraftingHelper.scanForItems(ingredientManager);

        IExtendableCraftingRecipeCategory craftingCategory = registration.getCraftingCategory();
        craftingCategory.addExtension(JeiCamoApplicationRecipe.class, new CamoCraftingRecipeExtension(camoCraftingHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        registration.addRecipes(
                FRAMING_SAW_RECIPE_TYPE,
                FramingSawRecipeCache.get(true).getRecipes().stream().map(RecipeHolder::value).toList()
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        registration.addRecipeTransferHandler(
                new FramingSawTransferHandler.FramingSaw(registration.getTransferHelper()),
                FRAMING_SAW_RECIPE_TYPE
        );
        registration.addRecipeTransferHandler(
                new FramingSawTransferHandler.PoweredFramingSaw(registration.getTransferHelper()),
                FRAMING_SAW_RECIPE_TYPE
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(
                new ItemStack(FBContent.BLOCK_FRAMING_SAW.value()),
                FRAMING_SAW_RECIPE_TYPE
        );
        registration.addRecipeCatalyst(
                new ItemStack(FBContent.BLOCK_POWERED_FRAMING_SAW.value()),
                FRAMING_SAW_RECIPE_TYPE
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        IIngredientManager ingredientManager = registration.getJeiHelpers().getIngredientManager();
        registration.addGhostIngredientHandler(
                FramingSawWithEncoderScreen.class,
                new FramingSawGhostIngredientHandler()
        );
        registration.addGuiContainerHandler(
                FramingSawWithEncoderScreen.class,
                new FramingSawWithEncoderGuiContainerHandler(ingredientManager)
        );
        registration.addGuiContainerHandler(
                FramingSawScreen.class,
                new FramingSawGuiContainerHandler<>(ingredientManager)
        );
        registration.addGhostIngredientHandler(
                PoweredFramingSawScreen.class,
                new PoweredFramingSawGhostIngredientHandler()
        );
        registration.addGuiContainerHandler(
                PoweredFramingSawScreen.class,
                new PoweredFramingSawGuiContainerHandler(ingredientManager)
        );
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration)
    {
        registration.addTypedRecipeManagerPlugin(RecipeTypes.CRAFTING, new CamoRecipeManagerPlugin(camoCraftingHelper));
    }

    @Override
    public ResourceLocation getPluginUid()
    {
        return ID;
    }
}
