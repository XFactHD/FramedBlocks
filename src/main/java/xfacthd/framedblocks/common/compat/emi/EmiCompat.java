package xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.api.util.Utils;

public final class EmiCompat
{
    public static final ResourceLocation SAW_ID = Utils.rl("framing_saw");
    public static final String SAW_CATEGORY = "emi.category." + SAW_ID.toString().replace(":", ".");

    private static boolean loadedClient = false;

    public static void init()
    {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("emi"))
        {
            loadedClient = true;
        }
    }

    public static boolean isLoaded()
    {
        return loadedClient;
    }

    public static boolean isShowRecipePressed(int keyCode, int scanCode)
    {
        if (loadedClient)
        {
            return GuardedAccess.isShowRecipePressed(keyCode, scanCode);
        }
        return false;
    }

    public static boolean handleShowRecipeRequest(ItemStack result)
    {
        if (loadedClient)
        {
            GuardedAccess.handleButtonRecipeRequest(result);
            return true;
        }
        return false;
    }



    static final class GuardedAccess
    {
        public static boolean isShowRecipePressed(int keyCode, int scanCode)
        {
            return false; // TODO: implement: https://github.com/emilyploszaj/emi/issues/270
        }

        private static void handleButtonRecipeRequest(ItemStack result)
        {
            EmiApi.displayRecipes(EmiStack.of(result));
        }



        private GuardedAccess() { }
    }



    private EmiCompat() { }
}
