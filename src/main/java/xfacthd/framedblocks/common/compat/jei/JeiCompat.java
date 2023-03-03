package xfacthd.framedblocks.common.compat.jei;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.*;
import mezz.jei.api.runtime.*;
import mezz.jei.config.KeyBindings;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import xfacthd.framedblocks.FramedBlocks;

import java.util.List;
import java.util.Objects;

public final class JeiCompat
{
    private static boolean loadedClient = false;

    public static void init()
    {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("jei"))
        {
            Guarded.init();
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
        private static KeyMapping showRecipeKey = null;

        public static void init()
        {
            try
            {
                List<KeyMapping> mappings = ObfuscationReflectionHelper.getPrivateValue(KeyBindings.class, null, "showRecipe");
                showRecipeKey = Objects.requireNonNull(mappings).get(0);
            }
            catch (Throwable t)
            {
                FramedBlocks.LOGGER.error("Encountered an error while retrieving \"show recipe\" keybind from JEI", t);
            }
        }

        public static boolean isShowRecipePressed(InputConstants.Key key)
        {
            Preconditions.checkNotNull(runtime, "Runtime not set");

            return showRecipeKey != null && showRecipeKey.isActiveAndMatches(key);
        }

        private static boolean handleButtonRecipeRequest(ItemStack result)
        {
            Preconditions.checkNotNull(runtime, "Runtime not set");

            IRecipesGui gui = runtime.getRecipesGui();
            IJeiHelpers helpers = runtime.getJeiHelpers();
            IFocusFactory focusFactory = helpers.getFocusFactory();

            IFocus<ItemStack> focus = focusFactory.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, result);
            gui.show(focus);

            return true;
        }

        public static void acceptRuntime(IJeiRuntime runtime)
        {
            Guarded.runtime = runtime;
        }
    }
}
