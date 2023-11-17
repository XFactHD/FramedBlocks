package xfacthd.framedblocks.common.compat.ctm;

import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
//import team.chisel.ctm.client.model.AbstractCTMBakedModel;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.data.ConTexDataHandler;

public final class CtmCompat
{
    private static final boolean ENABLE_CTM_COMPAT = false; // TODO: convince tterrag to not store the whole world in CT data...

    public static void init()
    {
        if (ENABLE_CTM_COMPAT && ModList.get().isLoaded("ctm"))
        {
            // Safeguard against potential changes in CTM since the ct context property is not exposed as API
            try
            {
                if (FMLEnvironment.dist.isClient())
                {
                    GuardedClientAccess.init();
                }
            }
            catch (Throwable e)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing CTM integration!", e);
            }
        }
    }



    private static final class GuardedClientAccess
    {
        public static void init()
        {
            //ModelProperty<?> property = Utils.getPrivateValue(
            //        AbstractCTMBakedModel.class, null, "CTM_CONTEXT"
            //);
            //ConTexDataHandler.addConTexProperty(property);
        }
    }



    private CtmCompat() { }
}
