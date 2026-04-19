package io.github.xfacthd.framedblocks.common.compat.amendments;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.mehvahdjukaar.amendments.Amendments;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LevelReader;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

public final class AmendmentsCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "amendments";
    public static final Identifier HANGING_MODEL_LOCATION = Utils.id(MOD_ID, "block/hanging_flower_pot_rope");
    private static boolean loaded = false;

    public static void init() {
        loaded = ModList.get().isLoaded(MOD_ID);
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static boolean canSurviveHanging(LevelReader level, BlockPos pos) {
        if (loaded) {
            return GuardedAccess.canSurviveHanging(level, pos);
        }
        return false;
    }

    private static final class GuardedAccess {
        private static boolean failedPreviously = false;

        public static boolean canSurviveHanging(LevelReader level, BlockPos pos) {
            if (failedPreviously) {
                return true;
            }

            try {
                return Amendments.isSupportingCeiling(pos, level);
            } catch (Throwable e) {
                if (!failedPreviously) {
                    failedPreviously = true;
                    LOGGER.error("[AmendmentsCompat] Encountered an error while checking hanging pot surviving", e);
                }
                return true;
            }
        }
    }

    private AmendmentsCompat() { }
}
