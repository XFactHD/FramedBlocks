package xfacthd.framedblocks.common.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.function.Supplier;

public record RegisteredBE<T extends BlockEntity>(RegistryObject<BlockEntityType<T>> value) implements Supplier<BlockEntityType<T>>
{
    @Override
    public BlockEntityType<T> get()
    {
        return value.get();
    }
}
