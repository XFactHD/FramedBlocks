package xfacthd.framedblocks.common.compat.supplementaries;

//import net.mehvahdjukaar.supplementaries.common.block.IRopeConnection;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.neoforged.fml.ModList;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.Utils;

public final class SupplementariesCompat
{
    private static boolean loaded = false;

    public static void init()
    {
        loaded = ModList.get().isLoaded("supplementaries");
    }

    public static boolean isLoaded()
    {
        return loaded;
    }

    public static boolean canSurviveHanging(LevelReader level, BlockPos pos)
    {
        if (loaded)
        {
            return GuardedAccess.canSurviveHanging(level, pos);
        }
        return false;
    }



    private static final class GuardedAccess
    {
        private static boolean failedPreviously = false;

        public static boolean canSurviveHanging(LevelReader level, BlockPos pos)
        {
            if (failedPreviously)
            {
                return true;
            }

            try
            {
                //return IRopeConnection.isSupportingCeiling(pos, level);
                return true;
            }
            catch (Throwable e)
            {
                if (!failedPreviously)
                {
                    failedPreviously = true;
                    FramedBlocks.LOGGER.error("[SupplementariesCompat] Encountered an error while checking hanging pot surviving", e);
                }
                return true;
            }
        }
    }

    public static class Client
    {
        public static final ModelResourceLocation HANGING_MODEL_LOCATION = ModelResourceLocation.standalone(
                Utils.rl("supplementaries", "block/hanging_flower_pot_rope")
        );
    }



    private SupplementariesCompat() { }
}
