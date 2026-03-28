package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class DeferredMenuType<T extends AbstractContainerMenu> extends DeferredHolder<MenuType<?>, MenuType<T>> {
    private DeferredMenuType(ResourceKey<MenuType<?>> key) {
        super(key);
    }

    public static <T extends AbstractContainerMenu> DeferredMenuType<T> createMenuType(Identifier name) {
        return createMenuType(ResourceKey.create(Registries.MENU, name));
    }

    public static <T extends AbstractContainerMenu> DeferredMenuType<T> createMenuType(ResourceKey<MenuType<?>> key) {
        return new DeferredMenuType<>(key);
    }
}
