package xfacthd.framedblocks.common.compat.rubidium;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;

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

    public static boolean isLoaded()
    {
        return loadedClient;
    }



    private static final class GuardedClientAccess
    {
        public static void init()
        {

        }
    }



    private RubidiumCompat() { }
}
