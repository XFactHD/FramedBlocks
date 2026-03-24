package io.github.xfacthd.framedblocks.common.util.registration;

import io.github.xfacthd.framedblocks.api.util.registration.DeferredBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public final class DeferredBlockEntityRegister extends DeferredRegister<BlockEntityType<?>>
{
    private DeferredBlockEntityRegister(String namespace)
    {
        super(Registries.BLOCK_ENTITY_TYPE, namespace);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <I extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, I> createHolder(
            ResourceKey<? extends Registry<BlockEntityType<?>>> registryKey, Identifier key
    )
    {
        return (DeferredHolder<BlockEntityType<?>, I>) DeferredBlockEntity.createBlockEntity(ResourceKey.create(registryKey, key));
    }

    public <T extends BlockEntity> DeferredBlockEntity<T> registerBlockEntity(
            String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<Set<Block>> blocks, boolean opOnlyNbt
    )
    {
        return (DeferredBlockEntity<T>) register(name, () -> new BlockEntityType<>(factory, blocks.get(), opOnlyNbt));
    }

    public static DeferredBlockEntityRegister create(String namespace)
    {
        return new DeferredBlockEntityRegister(namespace);
    }
}
