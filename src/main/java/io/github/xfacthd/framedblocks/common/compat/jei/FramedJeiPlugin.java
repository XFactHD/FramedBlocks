package io.github.xfacthd.framedblocks.common.compat.jei;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.screen.FramingSawScreen;
import io.github.xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;
import io.github.xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.compat.jei.camo.CamoCraftingHelper;
import io.github.xfacthd.framedblocks.common.compat.jei.camo.CamoCraftingRecipeExtension;
import io.github.xfacthd.framedblocks.common.compat.jei.camo.CamoRecipeManagerPlugin;
import io.github.xfacthd.framedblocks.common.compat.jei.camo.JeiCamoApplicationRecipe;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import io.github.xfacthd.framedblocks.common.crafting.camo.CamoApplicationRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@JeiPlugin
public final class FramedJeiPlugin implements IModPlugin {
    private static final Identifier ID = Utils.id("jei_plugin");
    static final IRecipeType<FramingSawRecipe> FRAMING_SAW_RECIPE_TYPE = IRecipeType.create(
            Utils.id("framing_saw"), FramingSawRecipe.class
    );
    @Nullable
    private static Boolean enableCamoCraftingDisplay;
    @Nullable
    private static CamoCraftingHelper camoCraftingHelperInstance;

    private static boolean isCamoCraftingDisplayEnabled() {
        if (enableCamoCraftingDisplay == null) {
            enableCamoCraftingDisplay = ClientConfig.VIEW.showCamoCraftingInJei();
        }
        return enableCamoCraftingDisplay;
    }

    private static CamoCraftingHelper getCamoCraftingHelper() {
        if (camoCraftingHelperInstance == null) {
            camoCraftingHelperInstance = new CamoCraftingHelper();
        }
        return camoCraftingHelperInstance;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FramingSawRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        if (isCamoCraftingDisplayEnabled()) {
            CamoCraftingHelper camoCraftingHelper = getCamoCraftingHelper();
            camoCraftingHelper.scanForItems(registration.getJeiHelpers().getIngredientManager());

            registration.getCraftingCategory().addExtension(
                    JeiCamoApplicationRecipe.class,
                    new CamoCraftingRecipeExtension(camoCraftingHelper)
            );
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
                FRAMING_SAW_RECIPE_TYPE,
                FramingSawRecipeCache.get(true).getRecipes().stream().map(RecipeHolder::value).toList()
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
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
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(
                FRAMING_SAW_RECIPE_TYPE,
                new ItemStack(FBContent.BLOCK_FRAMING_SAW.value())
        );
        registration.addCraftingStation(
                FRAMING_SAW_RECIPE_TYPE,
                new ItemStack(FBContent.BLOCK_POWERED_FRAMING_SAW.value())
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        IIngredientManager ingredientManager = registration.getJeiHelpers().getIngredientManager();
        registration.addGhostIngredientHandler(
                FramingSawWithEncoderScreen.class,
                new FramingSawGhostIngredientHandler()
        );
        registration.addGuiContainerHandler(
                FramingSawWithEncoderScreen.class,
                new FramingSawWithEncoderGuiContainerHandler()
        );
        registration.addGuiContainerHandler(
                FramingSawScreen.class,
                new FramingSawGuiContainerHandler<>()
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
    public void registerAdvanced(IAdvancedRegistration registration) {
        if (isCamoCraftingDisplayEnabled()) {
            registration.addSimpleRecipeManagerPlugin(
                    RecipeTypes.CRAFTING,
                    new CamoRecipeManagerPlugin(getCamoCraftingHelper())
            );
        }
    }

    @Override
    public void onRuntimeUnavailable() {
        enableCamoCraftingDisplay = null;
        camoCraftingHelperInstance = null;
    }

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    static void onRecipesReceived(RecipesReceivedEvent event) {
        if (!isCamoCraftingDisplayEnabled()) {
            return;
        }

        Optional<CamoApplicationRecipe> camoRecipe = event.getRecipeMap()
                .byType(RecipeType.CRAFTING)
                .stream()
                .map(RecipeHolder::value)
                .filter(CamoApplicationRecipe.class::isInstance)
                .map(CamoApplicationRecipe.class::cast)
                .findFirst();
        getCamoCraftingHelper().captureRecipe(camoRecipe);
    }
}
