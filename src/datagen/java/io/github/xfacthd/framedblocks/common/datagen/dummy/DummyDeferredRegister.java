package io.github.xfacthd.framedblocks.common.datagen.dummy;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/// [DeferredRegister] wrapper for registering datagen-only dummy objects in the absence of the compat mod required to create the real object.
final class DummyDeferredRegister<T> {
    private final Map<String, DeferredRegister<T>> registers = new HashMap<>();
    private final ResourceKey<? extends Registry<T>> registryName;
    private final Function<String, DeferredRegister<T>> registerFactory;
    private final BiFunction<DeferredRegister<T>, String, Holder<T>> registrar;

    static DummyDeferredRegister<Block> blocks() {
        return new DummyDeferredRegister<>(Registries.BLOCK, DeferredRegister::createBlocks, (reg, name) ->
                ((DeferredRegister.Blocks) reg).registerSimpleBlock(name)
        );
    }

    static DummyDeferredRegister<Item> items() {
        return new DummyDeferredRegister<>(Registries.ITEM, DeferredRegister::createItems, (reg, name) ->
                ((DeferredRegister.Items) reg).registerSimpleItem(name)
        );
    }

    private DummyDeferredRegister(
            ResourceKey<? extends Registry<T>> registryName,
            Function<String, DeferredRegister<T>> registerFactory,
            BiFunction<DeferredRegister<T>, String, Holder<T>> registrar
    ) {
        this.registryName = registryName;
        this.registerFactory = registerFactory;
        this.registrar = registrar;
    }

    Holder<T> register(Identifier id, String compatMod) {
        if (ModList.get().isLoaded(compatMod)) {
            return DeferredHolder.create(registryName, id);
        } else {
            DeferredRegister<T> register = registers.computeIfAbsent(id.getNamespace(), registerFactory);
            return registrar.apply(register, id.getPath());
        }
    }

    void register(IEventBus modBus) {
        registers.values().forEach(reg -> reg.register(modBus));
    }
}
