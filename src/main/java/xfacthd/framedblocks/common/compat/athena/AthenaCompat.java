package xfacthd.framedblocks.common.compat.athena;

import earth.terrarium.athena.api.client.neoforge.AthenaBakedModel;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.data.ConTexDataHandler;

public final class AthenaCompat
{
    public static void init()
    {
        if (ModList.get().isLoaded("athena"))
        {
            try
            {
                if (FMLEnvironment.dist.isClient())
                {
                    GuardedClientAccess.init();
                }
            }
            catch (Throwable e)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing Athena integration!", e);
            }
        }
    }

    private static final class GuardedClientAccess
    {
        public static void init()
        {
            ConTexDataHandler.addConTexProperty(AthenaBakedModel.DATA);
        }
    }



    private AthenaCompat() { }
}
