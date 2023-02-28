package xfacthd.framedblocks.common.compat.ctm;

import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.FramedBlocks;

public final class CtmCompat
{
    private static boolean loadedClient = false;

    public static void init()
    {
        if (ModList.get().isLoaded("ctm"))
        {
            // Safeguard against potential changes in Create since the ct context property is not exposed as API
            try
            {
                if (FMLEnvironment.dist.isClient())
                {
                    GuardedClientAccess.init();
                    loadedClient = true;
                }
            }
            catch (Throwable e)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing CTM integration!", e);
            }
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
        private static ModelProperty<?> CTM_CT_PROPERTY;

        public static void init()
        {
            CTM_CT_PROPERTY = null;
        }

        public static Object tryGetCTContext(ModelData data)
        {
            return data.get(CTM_CT_PROPERTY);
        }
    }
}
