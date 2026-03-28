package io.github.xfacthd.framedblocks.common.compat.jade;

import io.github.xfacthd.framedblocks.api.block.AbstractFramedBlock;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.FramedItemFrameBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import java.util.HashSet;
import java.util.Set;

@WailaPlugin
public final class FramedJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
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

        BuiltInRegistries.BLOCK.stream()
                .filter(IFramedBlock.class::isInstance)
                .filter(block -> !(block instanceof AbstractFramedBlock))
                .map(IFramedBlock.class::cast)
                .map(block -> {
                    Class<? extends Block> target = block.getJadeTargetClass();
                    if (!target.isInstance(block)) {
                        throw new IllegalArgumentException("Block '" + block + "' specifies invalid Jade target class '" + target + "'");
                    }
                    return target;
                })
                .forEach(targets::add);

        return targets;
    }
}
