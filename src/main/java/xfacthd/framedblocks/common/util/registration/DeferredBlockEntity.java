package xfacthd.framedblocks.common.util.registration;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class DeferredBlockEntity<T extends BlockEntity> extends DeferredHolder<BlockEntityType<?>, BlockEntityType<T>>
{
    DeferredBlockEntity(ResourceKey<BlockEntityType<?>> key)
    {
        super(key);
    }



    public static <T extends BlockEntity> DeferredBlockEntity<T> createBlockEntity(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder)
    {
        return new DeferredBlockEntity<>(holder.getKey());
    }
}
