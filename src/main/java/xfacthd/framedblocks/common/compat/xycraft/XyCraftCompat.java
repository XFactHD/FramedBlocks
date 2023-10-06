package xfacthd.framedblocks.common.compat.xycraft;

import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.data.ConTexDataHandler;

// TODO: remove explicit compat when XyCraft ships compat via IMC
public final class XyCraftCompat
{
    public static void init()
    {
        if (ModList.get().isLoaded("xycraft_core"))
        {
            // Safeguard against potential changes in Create since the ct context property is not exposed as API
            try
            {
                if (FMLEnvironment.dist.isClient())
                {
                    GuardedClientAccess.init();
                }
            }
            catch (Throwable e)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing client-only XyCraft Core integration!", e);
            }
        }
    }



    private static final class GuardedClientAccess
    {
        public static void init() throws ClassNotFoundException
        {
            Class<?> modelDataClass = Class.forName("tv.soaryn.xycraft.core.client.render.model.ConnectedTextureModel$Data");
            ModelProperty<?> property = Utils.getPrivateValue(modelDataClass, null, "PROPERTY");
            ConTexDataHandler.addConTexProperty(property);
        }
    }



    private XyCraftCompat() { }
}
