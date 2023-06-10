package xfacthd.framedblocks.common.compat.rubidium;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class RubidiumCompat
{
    private static boolean loadedClient = false;

    public static void init()
    {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("rubidium"))
        {
            GuardedClientAccess.init();
            loadedClient = true;
        }
    }

    public static boolean invertCollapsibleBlockSplitLineRotation()
    {
        if (loadedClient)
        {
            return GuardedClientAccess.invertCollapsibleBlockSplitLineRotation();
        }
        return false;
    }

    private static final class GuardedClientAccess
    {
        public static void init()
        {

        }

        public static boolean invertCollapsibleBlockSplitLineRotation()
        {
            return true;
        }
    }



    private RubidiumCompat() { }
}
