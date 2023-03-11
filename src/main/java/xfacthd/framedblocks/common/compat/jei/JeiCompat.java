package xfacthd.framedblocks.common.compat.jei;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.*;
import mezz.jei.api.runtime.*;
import mezz.jei.common.input.keys.IJeiKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.forgespi.language.IModFileInfo;
import xfacthd.framedblocks.FramedBlocks;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

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
            return GuardedAccess.isShowRecipePressed(key);
        }
        return false;
    }

    public static boolean handleShowRecipeRequest(ItemStack result)
    {
        if (loadedClient)
        {
            return GuardedAccess.handleButtonRecipeRequest(result);
        }
        return false;
    }



    static final class GuardedAccess
    {
        private static IJeiRuntime runtime = null;
        private static Predicate<InputConstants.Key> showRecipeKey = null;
        private static Throwable savedError;

        public static boolean isShowRecipePressed(InputConstants.Key key)
        {
            Preconditions.checkNotNull(runtime, "Runtime not set");

            return showRecipeKey != null && showRecipeKey.test(key);
        }

        public static boolean handleButtonRecipeRequest(ItemStack result)
        {
            Preconditions.checkNotNull(runtime, "Runtime not set");

            IRecipesGui gui = runtime.getRecipesGui();
            IFocusFactory focusFactory = runtime.getJeiHelpers().getFocusFactory();

            IFocus<ItemStack> focus = focusFactory.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, result);
            gui.show(focus);

            return true;
        }

        public static void acceptRuntime(IJeiRuntime runtime)
        {
            GuardedAccess.runtime = runtime;

            attemptJei9KeybindLookup();
            if (savedError != null)
            {
                attemptJei10KeybindLookup();
            }
        }

        public static void attemptJei9KeybindLookup()
        {
            try
            {
                Class<?> keyBindingsClass = Class.forName("mezz.jei.config.KeyBindings");
                List<KeyMapping> mappings = ObfuscationReflectionHelper.getPrivateValue(keyBindingsClass, null, "showRecipe");
                showRecipeKey = Objects.requireNonNull(mappings).get(0)::isActiveAndMatches;
            }
            catch (Throwable t)
            {
                savedError = t;
            }
        }

        @SuppressWarnings("unchecked")
        private static void attemptJei10KeybindLookup()
        {
            try
            {
                Class<?> recipeGuiClass = Class.forName("mezz.jei.common.gui.recipes.RecipesGui");
                Class<?> keyBindingsClass = Class.forName("mezz.jei.common.config.KeyBindings");
                Object keyBindingsInst = ObfuscationReflectionHelper.getPrivateValue(
                        (Class<Object>) recipeGuiClass,
                        recipeGuiClass.cast(runtime.getRecipesGui()),
                        "keyBindings"
                );
                List<IJeiKeyMapping> mappings = ObfuscationReflectionHelper.getPrivateValue(
                        (Class<Object>) keyBindingsClass,
                        keyBindingsInst,
                        "showRecipe"
                );
                showRecipeKey = Objects.requireNonNull(mappings).get(0)::isActiveAndMatches;
            }
            catch (Throwable t)
            {
                IModFileInfo jeiMod = ModList.get().getModFileById("jei");

                FramedBlocks.LOGGER.warn("Encountered an error while retrieving \"show recipe\" keybind from JEI");
                FramedBlocks.LOGGER.warn("JEI version: " + (jeiMod != null ? jeiMod.versionString() : "[UNKNOWN]"));
                FramedBlocks.LOGGER.warn("JEI 9-based lookup failed with the following exception", savedError);
                FramedBlocks.LOGGER.warn("JEI 10-based lookup failed with the following exception", t);
            }

            savedError = null;
        }
    }
}
