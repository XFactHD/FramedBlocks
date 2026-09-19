package io.github.xfacthd.framedblocks.common.compat.jade;

import io.github.xfacthd.framedblocks.api.block.AbstractFramedBlock;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.compat.jade.JadeDisplayConfig;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.block.interactive.FramedItemFrameBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

@WailaPlugin
public final class FramedJadePlugin implements IWailaPlugin {
    private static final Map<IFramedBlock, JadeDisplayConfig> DISPLAY_CONFIGS = new IdentityHashMap<>();

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        BuiltInRegistries.BLOCK.stream()
                .filter(IFramedBlock.class::isInstance)
                .map(IFramedBlock.class::cast)
                .forEach(block -> DISPLAY_CONFIGS.put(block, block.getJadeDisplayConfig()));

        for (Class<? extends Block> target : collectTargetClasses()) {
            registration.registerBlockIcon(FramedBlockComponentProvider.INSTANCE, target);
            registration.registerBlockComponent(FramedBlockComponentProvider.INSTANCE, target);
        }

        registration.registerBlockIcon(FramedItemFrameComponentProvider.INSTANCE, FramedItemFrameBlock.class);
        registration.registerBlockComponent(FramedItemFrameComponentProvider.INSTANCE, FramedItemFrameBlock.class);

        registration.addRayTraceCallback(new FramedOneWayWindowRayTraceCallback(registration));
    }

    private static Set<Class<? extends Block>> collectTargetClasses() {
        Set<Class<? extends Block>> targets = new HashSet<>();
        targets.add(AbstractFramedBlock.class);

        DISPLAY_CONFIGS.forEach((block, displayConfig) -> {
            Class<? extends Block> target = displayConfig.targetClass();
            if (!target.isInstance(block)) {
                throw new IllegalArgumentException("Block '" + block + "' specifies invalid Jade target class '" + target + "'");
            }
            if (!(block instanceof AbstractFramedBlock)) {
                targets.add(target);
            }
        });

        return targets;
    }

    static JadeDisplayConfig getDisplayConfig(IFramedBlock block) {
        if (!Utils.PRODUCTION) {
            return block.getJadeDisplayConfig();
        }
        return DISPLAY_CONFIGS.get(block);
    }
}
