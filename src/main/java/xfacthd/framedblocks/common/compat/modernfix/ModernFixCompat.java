package xfacthd.framedblocks.common.compat.modernfix;

import net.neoforged.fml.ModList;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;

public final class ModernFixCompat
{
    static volatile boolean dynamicResources = false;

    public static void init()
    {
        if (ModList.get().isLoaded("modernfix"))
        {
            Guarded.init();
        }
    }

    public static boolean dynamicResourcesEnabled()
    {
        return dynamicResources;
    }



    private static final class Guarded
    {
        public static void init()
        {
            dynamicResources = ModernFixMixinPlugin.instance.isOptionEnabled("perf.dynamic_resources.FramedBlocksEnabledCheck");
        }
    }



    private ModernFixCompat() { }
}
