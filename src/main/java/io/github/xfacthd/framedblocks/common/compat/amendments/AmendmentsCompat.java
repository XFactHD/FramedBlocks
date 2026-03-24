package io.github.xfacthd.framedblocks.common.compat.amendments;

import io.github.xfacthd.framedblocks.FramedBlocks;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.mehvahdjukaar.amendments.Amendments;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LevelReader;
import net.neoforged.fml.ModList;

public final class AmendmentsCompat
{
    public static final String MOD_ID = "amendments";
    public static final Identifier HANGING_MODEL_LOCATION = Utils.id(MOD_ID, "block/hanging_flower_pot_rope");
    private static boolean loaded = false;

    public static void init()
    {
        loaded = ModList.get().isLoaded(MOD_ID);
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
                return Amendments.isSupportingCeiling(pos, level);
            }
            catch (Throwable e)
            {
                if (!failedPreviously)
                {
                    failedPreviously = true;
                    FramedBlocks.LOGGER.error("[AmendmentsCompat] Encountered an error while checking hanging pot surviving", e);
                }
                return true;
            }
        }
    }

    private AmendmentsCompat() { }
}
