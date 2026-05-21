package io.github.xfacthd.framedblocks.common.datagen.dummy;

import io.github.xfacthd.framedblocks.common.compat.ae2.AppliedEnergisticsCompat;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;

public final class DummyObjects {
    private static final DummyDeferredRegister<Block> BLOCKS = DummyDeferredRegister.blocks();
    private static final DummyDeferredRegister<Item> ITEMS = DummyDeferredRegister.items();

    public static final Holder<Item> ITEM_FRAMING_SAW_PATTERN = ITEMS.register(AppliedEnergisticsCompat.SAW_PATTERN_ID, AppliedEnergisticsCompat.MOD_ID);

    public static void init(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }

    private DummyObjects() { }
}
