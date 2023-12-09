package xfacthd.framedblocks.common.compat.modernfix;

import org.embeddedt.modernfix.core.ModernFixMixinPlugin;

public final class ModernFixCompat
{
    static volatile boolean dynamicResources = false;

    public static void init()
    {
        dynamicResources = ModernFixMixinPlugin.instance.isOptionEnabled("perf.dynamic_resources.FramedBlocksEnabledCheck");
    }

    public static boolean dynamicResourcesEnabled()
    {
        return dynamicResources;
    }



    private ModernFixCompat() { }
}
