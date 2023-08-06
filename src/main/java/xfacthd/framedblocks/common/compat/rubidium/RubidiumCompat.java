package xfacthd.framedblocks.common.compat.rubidium;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import xfacthd.framedblocks.FramedBlocks;

public final class RubidiumCompat
{
    private static boolean loadedClient = false;
    private static boolean supportsCustomVertexConsumer = false;

    public static void init()
    {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("rubidium"))
        {
            GuardedClientAccess.init();
            loadedClient = true;

            try
            {
                String versionString = ModList.get().getModFileById("rubidium").versionString();
                DefaultArtifactVersion version = new DefaultArtifactVersion(versionString);
                DefaultArtifactVersion fixVersion = new DefaultArtifactVersion("0.7.0");
                supportsCustomVertexConsumer = version.compareTo(fixVersion) >= 0;
            }
            catch (Throwable t)
            {
                FramedBlocks.LOGGER.error("Rubidium version check failed, assuming worst case", t);
            }
        }
    }

    public static boolean isLoaded()
    {
        return loadedClient;
    }

    // TODO: remove version check in 1.20.2, assuming they don't break shit again...
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static boolean supportsCustomVertexConsumer()
    {
        return supportsCustomVertexConsumer;
    }



    private static final class GuardedClientAccess
    {
        public static void init()
        {

        }
    }



    private RubidiumCompat() { }
}
