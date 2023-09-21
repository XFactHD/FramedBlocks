package xfacthd.framedblocks.api.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.util.Utils;

@ApiStatus.Internal
public interface InternalAPI
{
    InternalAPI INSTANCE = Utils.loadService(InternalAPI.class);

    BlockEntity getExistingBlockEntity(BlockGetter level, BlockPos pos);

    BlockEntity getBlockEntityForLight(BlockGetter level, BlockPos pos);

    void updateCamoNbt(CompoundTag tag, String stateKey, String stackKey, String camoKey);
}
