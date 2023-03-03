package xfacthd.framedblocks.common.compat.jei;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.*;
import mezz.jei.api.runtime.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Optional;

public final class JeiCompat
{
    private static boolean loadedClient = false;

    public static void init()
    {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("jei"))
        {
            loadedClient = true;
        }
    }

    public static boolean isShowRecipePressed(InputConstants.Key key)
    {
        if (loadedClient)
        {
            return Guarded.isShowRecipePressed(key);
        }
        return false;
    }

    public static boolean handleShowRecipeRequest(ItemStack result)
    {
        if (loadedClient)
        {
            return Guarded.handleButtonRecipeRequest(result);
        }
        return false;
    }



    static final class Guarded
    {
        private static IJeiRuntime runtime = null;

        public static boolean isShowRecipePressed(InputConstants.Key key)
        {
            Preconditions.checkNotNull(runtime, "Runtime not set");

            return runtime.getKeyMappings().getShowRecipe().isActiveAndMatches(key);
        }

        private static boolean handleButtonRecipeRequest(ItemStack result)
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
                IFocus<ItemStack> focus = focusFactory.createFocus(RecipeIngredientRole.OUTPUT, ingredient.get());
                gui.show(focus);

                return true;
            }

            return false;
        }

        public static void acceptRuntime(IJeiRuntime runtime)
        {
            Guarded.runtime = runtime;
        }
    }
}
