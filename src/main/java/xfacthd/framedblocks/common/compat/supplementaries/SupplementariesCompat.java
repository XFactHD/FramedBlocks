package xfacthd.framedblocks.common.compat.supplementaries;

import net.mehvahdjukaar.supplementaries.common.block.IRopeConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.fml.ModList;
import xfacthd.framedblocks.FramedBlocks;

public final class SupplementariesCompat
{
    public static final ResourceLocation HANGING_MODEL_LOCATION = new ResourceLocation("supplementaries", "block/hanging_flower_pot_rope");
    private static boolean loaded = false;

    public static void init() { loaded = ModList.get().isLoaded("supplementaries"); }

    public static boolean isLoaded() { return loaded; }

    public static boolean canSurviveHanging(LevelReader level, BlockPos pos)
    {
        if (loaded)
        {
            return Guarded.canSurviveHanging(level, pos);
        }
        return false;
    }



    private static class Guarded
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
                return IRopeConnection.isSupportingCeiling(pos, level);
            }
            catch (Throwable e)
            {
                if (!failedPreviously)
                {
                    failedPreviously = true;
                    FramedBlocks.LOGGER.error("Encountered an error while checking hanging pot surviving", e);
                }
                return true;
            }
        }
    }

    private SupplementariesCompat() { }
}
