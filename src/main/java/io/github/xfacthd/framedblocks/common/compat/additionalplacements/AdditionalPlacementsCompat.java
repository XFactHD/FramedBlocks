package io.github.xfacthd.framedblocks.common.compat.additionalplacements;

import com.firemerald.additionalplacements.generation.IBlockBlacklister;
import com.firemerald.additionalplacements.generation.Registration;
import com.firemerald.additionalplacements.generation.RegistrationInitializer;
import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

import java.util.function.Consumer;

public final class AdditionalPlacementsCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        if (ModList.get().isLoaded("additionalplacements")) {
            try {
                GuardedAccess.init();
            } catch (Throwable e) {
                LOGGER.warn("An error occured while initializing AdditionalPlacements integration!", e);
            }
        }
    }

    private static final class GuardedAccess {
        public static void init() {
            Registration.addRegistration(new RegistrationInitializer() {
                @Override
                public void addGlobalBlacklisters(Consumer<IBlockBlacklister<Block>> register) {
                    register.accept((block, _) -> block instanceof IFramedBlock);
                }
            });
        }
    }

    private AdditionalPlacementsCompat() { }
}
