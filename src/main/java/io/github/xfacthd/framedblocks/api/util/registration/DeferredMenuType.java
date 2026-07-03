package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

/// Specialized deferred holder for [MenuType]s.
public final class DeferredMenuType<T extends AbstractContainerMenu> extends DeferredHolder<MenuType<?>, MenuType<T>> {
    private DeferredMenuType(ResourceKey<MenuType<?>> key) {
        super(key);
    }

    /// {@return a deferred holder for the menu type registered under the given name}
    ///
    /// @param name The registry name of the menu type
    public static <T extends AbstractContainerMenu> DeferredMenuType<T> createMenuType(Identifier name) {
        return createMenuType(ResourceKey.create(Registries.MENU, name));
    }

    /// {@return a deferred holder for the menu type registered under the given key}
    ///
    /// @param key The registry key of the menu type
    public static <T extends AbstractContainerMenu> DeferredMenuType<T> createMenuType(ResourceKey<MenuType<?>> key) {
        return new DeferredMenuType<>(key);
    }
}
