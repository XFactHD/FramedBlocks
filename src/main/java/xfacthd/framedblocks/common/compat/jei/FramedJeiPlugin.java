package xfacthd.framedblocks.common.compat.jei;

//import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.screen.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;

@JeiPlugin
//@REIPluginCompatIgnore
public final class FramedJeiPlugin implements IModPlugin
{
    private static final ResourceLocation ID = Utils.rl("jei_plugin");
    static final RecipeType<FramingSawRecipe> FRAMING_SAW_RECIPE_TYPE = new RecipeType<>(
            Utils.rl("framing_saw"), FramingSawRecipe.class
    );

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        registration.addRecipeCategories(new FramingSawRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
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
        registration.addGhostIngredientHandler(
                FramingSawWithEncoderScreen.class,
                new FramingSawGhostIngredientHandler()
        );
        registration.addGuiContainerHandler(
                FramingSawWithEncoderScreen.class,
                new FramingSawGuiContainerHandler()
        );
        registration.addGhostIngredientHandler(
                PoweredFramingSawScreen.class,
                new PoweredFramingSawGhostIngredientHandler()
        );
        registration.addGuiContainerHandler(
                PoweredFramingSawScreen.class,
                new PoweredFramingSawGuiContainerHandler()
        );
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
    {
        JeiCompat.GuardedAccess.acceptRuntime(jeiRuntime);
    }

    @Override
    public void onRuntimeUnavailable()
    {
        JeiCompat.GuardedAccess.acceptRuntime(null);
    }

    @Override
    public ResourceLocation getPluginUid()
    {
        return ID;
    }
}
