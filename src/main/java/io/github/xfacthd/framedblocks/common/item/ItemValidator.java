package io.github.xfacthd.framedblocks.common.item;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.common.item.block.placement.MultiBlockStateCycleSpec;
import io.github.xfacthd.framedblocks.common.item.block.placement.SingleBlockStateCycleSpec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

import java.util.Locale;

public final class ItemValidator {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void validate() {
        MutableInt count = new MutableInt();
        BuiltInRegistries.BLOCK.stream()
                .filter(IFramedBlock.class::isInstance)
                .map(Block::asItem)
                .distinct()
                .filter(IFramedBlockItem.class::isInstance)
                .map(IFramedBlockItem.class::cast)
                .forEach(item -> {
                    if (!(item instanceof BlockItem blockItem)) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Expected BlockItem & IFramedBlockItem, got %s", item.getClass().getName()));
                    }

                    StateCycleSpec cycleSpec = item.getStateCycleSpec();
                    if (cycleSpec == StateCycleSpec.NOT_IMPLEMENTED) {
                        LOGGER.warn("BlockItem for {} does not implement placement state cycling", blockItem.getBlock());
                        count.increment();
                    } else if (cycleSpec != StateCycleSpec.UNSUPPORTED) {
                        if (!(cycleSpec instanceof SingleBlockStateCycleSpec || cycleSpec instanceof MultiBlockStateCycleSpec)) {
                            throw new IllegalStateException(String.format(
                                    Locale.ROOT,
                                    "%s uses unsupported StateCycleSpec implementation %s",
                                    item,
                                    cycleSpec.getClass().getSimpleName()
                            ));
                        }
                    }
                });
        if (count.intValue() > 0) {
            LOGGER.warn("Found {} blocks not implementing placement state cycling", count);
        }
    }
    private ItemValidator() { }
}
