package xfacthd.framedblocks.common.compat.jei;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.InputConstants;
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.helpers.IJeiHelpers;
//import mezz.jei.api.ingredients.ITypedIngredient;
//import mezz.jei.api.recipe.*;
//import mezz.jei.api.runtime.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.RecipeViewer;

import java.util.Optional;

public final class JeiCompat
{
    public static final Component MSG_INVALID_RECIPE = Utils.translate("msg", "framing_saw.transfer.invalid_recipe");
    public static final Component MSG_TRANSFER_NOT_IMPLEMENTED = Utils.translate("msg", "framing_saw.transfer.not_implemented");

    private static boolean loadedClient = false;

    public static void init()
    {
        // Ignore REI's JEI plugin compat layer, we have a proper REI plugin and our JEI plugin won't be called by REI
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("jei") && !ModList.get().isLoaded("rei_plugin_compatibilities"))
        {
            loadedClient = true;
        }
    }

    public static boolean isLoaded()
    {
        return loadedClient;
    }

    public static RecipeViewer.LookupTarget isShowRecipePressed(int keyCode, int scanCode)
    {
        if (loadedClient)
        {
            //return GuardedAccess.isShowRecipePressed(keyCode, scanCode);
        }
        return null;
    }

    public static boolean handleShowRecipeRequest(ItemStack result, RecipeViewer.LookupTarget target)
    {
        if (loadedClient)
        {
            //return GuardedAccess.handleButtonRecipeRequest(result, target);
        }
        return false;
    }



    static final class GuardedAccess
    {
        /*private static IJeiRuntime runtime = null;
        private static IJeiKeyMappings keys = null;

        public static RecipeViewer.LookupTarget isShowRecipePressed(int keyCode, int scanCode)
        {
            Preconditions.checkNotNull(runtime, "Runtime not set");

            InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
            if (keys.getShowRecipe().isActiveAndMatches(key))
            {
                return RecipeViewer.LookupTarget.RECIPE;
            }
            if (keys.getShowUses().isActiveAndMatches(key))
            {
                return RecipeViewer.LookupTarget.USAGE;
            }
            return null;
        }

        private static boolean handleButtonRecipeRequest(ItemStack result, RecipeViewer.LookupTarget target)
        {
            Preconditions.checkNotNull(runtime, "Runtime not set");

            IRecipesGui gui = runtime.getRecipesGui();
            IJeiHelpers helpers = runtime.getJeiHelpers();
            IIngredientManager ingredients = helpers.getIngredientManager();
            IFocusFactory focusFactory = helpers.getFocusFactory();

            Optional<ITypedIngredient<ItemStack>> ingredient = ingredients.createTypedIngredient(
                    VanillaTypes.ITEM_STACK, result
            );
            if (ingredient.isPresent())
            {
                RecipeIngredientRole role = switch (target)
                {
                    case RECIPE -> RecipeIngredientRole.OUTPUT;
                    case USAGE -> RecipeIngredientRole.INPUT;
                };
                IFocus<ItemStack> focus = focusFactory.createFocus(role, ingredient.get());
                gui.show(focus);

                return true;
            }

            return false;
        }

        public static void acceptRuntime(IJeiRuntime runtime)
        {
            GuardedAccess.runtime = runtime;
            GuardedAccess.keys = runtime != null ? runtime.getKeyMappings() : null;
        }

        public static IJeiRuntime getRuntime()
        {
            return runtime;
        }*/



        private GuardedAccess() { }
    }



    private JeiCompat() { }
}
