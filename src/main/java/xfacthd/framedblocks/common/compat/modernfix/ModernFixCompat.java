package xfacthd.framedblocks.common.compat.modernfix;

public final class ModernFixCompat
{
    static volatile boolean dynamicResources = false;

    public static boolean dynamicResourcesEnabled()
    {
        return dynamicResources;
    }



    private ModernFixCompat() { }
}
