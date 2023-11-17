package xfacthd.framedblocks.common.compat.contex;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
//import xfacthd.contex.api.utils.Constants;
import xfacthd.framedblocks.client.data.ConTexDataHandler;

public final class ConTexCompat
{
    public static void init()
    {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("contex"))
        {
            GuardedClientAccess.init();
        }
    }

    private static final class GuardedClientAccess
    {
        public static void init()
        {
            //ConTexDataHandler.addConTexProperty(Constants.CT_STATE_PROPERTY);
        }
    }



    private ConTexCompat() { }
}
