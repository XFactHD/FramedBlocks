package xfacthd.framedblocks.common.compat.modernfix;

import net.neoforged.fml.ModList;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;

public final class ModernFixCompat
{
    private static final boolean ENABLED = false; // TODO: reactivate when ModernFix provides a texture getter
    static volatile boolean dynamicResources = false;

    public static void init()
    {
        if (ENABLED && ModList.get().isLoaded("modernfix"))
        {
            GuardedAccess.init();
        }
    }

    public static boolean dynamicResourcesEnabled()
    {
        return dynamicResources;
    }



    private static final class GuardedAccess
    {
        public static void init()
        {
            dynamicResources = ModernFixMixinPlugin.instance.isOptionEnabled("perf.dynamic_resources.FramedBlocksEnabledCheck");
        }
    }



    private ModernFixCompat() { }
}
