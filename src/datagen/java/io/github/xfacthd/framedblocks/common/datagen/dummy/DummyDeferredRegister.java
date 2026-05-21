package io.github.xfacthd.framedblocks.common.datagen.dummy;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

/// [DeferredRegister] wrapper for registering datagen-only dummy objects in the absence of the compat mod required to create the real object.
final class DummyDeferredRegister<T> {
    private final DeferredRegister<T> register;
    private final Function<String, Holder<T>> registrar;

    static DummyDeferredRegister<Block> blocks() {
        DeferredRegister.Blocks blocks = DeferredRegister.createBlocks(FramedConstants.MOD_ID);
        return new DummyDeferredRegister<>(blocks, blocks::registerSimpleBlock);
    }

    static DummyDeferredRegister<Item> items() {
        DeferredRegister.Items items = DeferredRegister.createItems(FramedConstants.MOD_ID);
        return new DummyDeferredRegister<>(items, items::registerSimpleItem);
    }

    private DummyDeferredRegister(DeferredRegister<T> register, Function<String, Holder<T>> registrar) {
        this.register = register;
        this.registrar = registrar;
    }

    Holder<T> register(Identifier id, String compatMod) {
        if (ModList.get().isLoaded(compatMod)) {
            return DeferredHolder.create(register.getRegistryName(), id);
        } else {
            return registrar.apply(id.getPath());
        }
    }

    void register(IEventBus modBus) {
        register.register(modBus);
    }
}
