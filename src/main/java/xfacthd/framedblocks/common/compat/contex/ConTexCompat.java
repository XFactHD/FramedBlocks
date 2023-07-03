package xfacthd.framedblocks.common.compat.contex;

import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.contex.api.utils.Constants;

public final class ConTexCompat
{
    private static boolean loadedClient = false;

    public static void init()
    {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("contex"))
        {
            loadedClient = true;
        }
    }

    public static Object tryGetCTContext(ModelData data)
    {
        if (loadedClient)
        {
            return GuardedClientAccess.tryGetCTContext(data);
        }
        return null;
    }

    private static final class GuardedClientAccess
    {
        public static Object tryGetCTContext(ModelData data)
        {
            return data.get(Constants.CT_STATE_PROPERTY);
        }
    }



    private ConTexCompat() { }
}
