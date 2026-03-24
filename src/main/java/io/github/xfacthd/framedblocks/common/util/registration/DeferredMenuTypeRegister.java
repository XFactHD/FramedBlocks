package io.github.xfacthd.framedblocks.common.util.registration;

import io.github.xfacthd.framedblocks.api.util.registration.DeferredMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DeferredMenuTypeRegister extends DeferredRegister<MenuType<?>>
{
    private DeferredMenuTypeRegister(String namespace)
    {
        super(Registries.MENU, namespace);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <I extends MenuType<?>> DeferredHolder<MenuType<?>, I> createHolder(
            ResourceKey<? extends Registry<MenuType<?>>> registryKey, Identifier key
    )
    {
        return (DeferredHolder<MenuType<?>, I>) DeferredMenuType.createMenuType(ResourceKey.create(registryKey, key));
    }

    public <T extends AbstractContainerMenu> DeferredMenuType<T> registerSimpleMenuType(
            String name, MenuType.MenuSupplier<T> constructor
    )
    {
        return (DeferredMenuType<T>) register(name, () -> new MenuType<>(constructor, FeatureFlags.DEFAULT_FLAGS));
    }

    public <T extends AbstractContainerMenu> DeferredMenuType<T> registerAdvancedMenuType(
            String name, IContainerFactory<T> constructor
    )
    {
        return (DeferredMenuType<T>) register(name, () -> new MenuType<>(constructor, FeatureFlags.DEFAULT_FLAGS));
    }

    public static DeferredMenuTypeRegister create(String namespace)
    {
        return new DeferredMenuTypeRegister(namespace);
    }
}
