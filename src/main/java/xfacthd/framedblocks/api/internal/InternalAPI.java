package xfacthd.framedblocks.api.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;

@ApiStatus.Internal
public interface InternalAPI
{
    InternalAPI INSTANCE = Utils.loadService(InternalAPI.class);

    BlockEntity getExistingBlockEntity(BlockGetter level, BlockPos pos);

    BlockEntity getBlockEntityForLight(BlockGetter level, BlockPos pos);

    void updateCamoNbt(CompoundTag tag, String stateKey, String stackKey, String camoKey);

    void enqueueCullingUpdate(Level level, BlockPos pos);

    BlockState getAppearance(
            IFramedBlock framedBlock,
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction side,
            @Nullable BlockState queryState,
            @Nullable BlockPos queryPos
    );
}
