package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

/// Specialized deffered holder for [BlockEntityType]s.
public final class DeferredBlockEntity<T extends BlockEntity> extends DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> {
    private DeferredBlockEntity(ResourceKey<BlockEntityType<?>> key) {
        super(key);
    }

    /// {@return a deferred holder for the block entity type registered under the given name}
    ///
    /// @param name The registry name of the block entity type
    public static <T extends BlockEntity> DeferredBlockEntity<T> createBlockEntity(Identifier name) {
        return createBlockEntity(ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, name));
    }

    /// {@return a deferred holder for the attachment type registered under the given key}
    ///
    /// @param key The registry key of the block entity type
    public static <T extends BlockEntity> DeferredBlockEntity<T> createBlockEntity(ResourceKey<BlockEntityType<?>> key) {
        return new DeferredBlockEntity<>(key);
    }
}
