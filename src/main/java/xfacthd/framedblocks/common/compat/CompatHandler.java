package xfacthd.framedblocks.common.compat;

import net.minecraftforge.fml.ModList;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.compat.buildinggadgets.BuildingGadgetsCompat;

public class CompatHandler
{
    public static void init()
    {
        if (ModList.get().isLoaded("buildinggadgets"))
        {
            /* Safeguard against potential API changes in Building Gadgets
             * Providing a config option is not possible because the integration requires registering
             * a custom registry object which happens before configs load
             */

            try
            {
                BuildingGadgetsCompat.init();
            }
            catch (Throwable e)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing Building Gadgets integration!", e);
            }
        }
    }
}
