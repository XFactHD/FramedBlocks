package xfacthd.framedblocks.common.util.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class DeferredBlockEntity<T extends BlockEntity> extends DeferredHolder<BlockEntityType<?>, BlockEntityType<T>>
{
    private DeferredBlockEntity(ResourceKey<BlockEntityType<?>> key)
    {
        super(key);
    }



    public static <T extends BlockEntity> DeferredBlockEntity<T> createBlockEntity(ResourceLocation name)
    {
        return createBlockEntity(ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, name));
    }

    public static <T extends BlockEntity> DeferredBlockEntity<T> createBlockEntity(ResourceKey<BlockEntityType<?>> key)
    {
        return new DeferredBlockEntity<>(key);
    }
}
