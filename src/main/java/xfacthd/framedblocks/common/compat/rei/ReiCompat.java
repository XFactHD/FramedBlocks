package xfacthd.framedblocks.common.compat.rei;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.api.util.Utils;

public final class ReiCompat
{
    public static final ResourceLocation SAW_ID = Utils.rl("framing_saw");

    private static boolean loadedClient = false;

    public static void init()
    {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("roughlyenoughitems"))
        {
            GuardedAccess.init();
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
            return GuardedAccess.handleButtonRecipeRequest(result);
        }
        return false;
    }



    static final class GuardedAccess // TODO: implement
    {
        public static void init()
        {

        }

        public static boolean isShowRecipePressed(int keyCode, int scanCode)
        {
            return false;
        }

        private static boolean handleButtonRecipeRequest(ItemStack result)
        {
            return false;
        }



        private GuardedAccess() { }
    }



    private ReiCompat() { }
}
