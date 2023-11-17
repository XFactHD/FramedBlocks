package xfacthd.framedblocks.common.util.registration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public final class BlockEntityDeferredRegister //extends DeferredRegister<BlockEntityType<?>>
{
    /*private BlockEntityDeferredRegister(String namespace)
    {
        super(Registries.BLOCK_ENTITY_TYPE, namespace);
    }

    @Override
    public <BE extends BlockEntity, T extends BlockEntityType<BE>> DeferredBlockEntity<BE> register(
            String name, Function<ResourceLocation, ? extends T> sup
    )
    {
        return (DeferredBlockEntity<BE>) super.register(name, sup);
    }

    @Override
    public <BE extends BlockEntity, T extends BlockEntityType<BE>> DeferredBlockEntity<BE> register(
            String name, Supplier<? extends T> sup
    )
    {
        return register(name, key -> sup.get());
    }

    @Override
    protected <BE extends BlockEntity, T extends BlockEntityType<BE>> DeferredBlockEntity<BE> createHolder(
            ResourceKey<? extends Registry<BlockEntityType<?>>> registryKey, ResourceLocation key
    )
    {
        return DeferredBlockEntity.createBlockEntity(ResourceKey.create(registryKey, key));
    }*/
}
