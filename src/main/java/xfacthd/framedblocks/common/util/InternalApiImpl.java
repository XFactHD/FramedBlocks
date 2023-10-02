package xfacthd.framedblocks.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.data.appearance.AppearanceHelper;
import xfacthd.framedblocks.common.data.cullupdate.CullingUpdateTracker;
import xfacthd.framedblocks.api.internal.InternalAPI;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.compat.flywheel.FlywheelCompat;
import xfacthd.framedblocks.common.compat.starlight.StarlightCompat;

public final class InternalApiImpl implements InternalAPI
{
    @Override
    public BlockEntity getExistingBlockEntity(BlockGetter level, BlockPos pos)
    {
        if (FlywheelCompat.isVirtualLevel(level))
        {
            return level.getBlockEntity(pos);
        }
        return level.getExistingBlockEntity(pos);
    }

    @Override
    public BlockEntity getBlockEntityForLight(BlockGetter level, BlockPos pos)
    {
        return StarlightCompat.getBlockEntityForLight(level, pos);
    }

    @Override
    public void updateCamoNbt(CompoundTag tag, String stateKey, String stackKey, String camoKey)
    {
        if (tag.contains(stateKey))
        {
            CompoundTag stateTag = tag.getCompound(stateKey);
            tag.remove(stateKey);
            tag.remove(stackKey);
            CompoundTag camoTag = new CompoundTag();
            camoTag.putString("type", FBContent.FACTORY_BLOCK.getId().toString());
            camoTag.put("state", stateTag);
            tag.put(camoKey, camoTag);
        }
    }

    @Override
    public void enqueueCullingUpdate(Level level, BlockPos pos)
    {
        CullingUpdateTracker.enqueueCullingUpdate(level, pos);
    }

    @Override
    public BlockState getAppearance(
            IFramedBlock block,
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction side,
            @Nullable BlockState queryState,
            @Nullable BlockPos queryPos
    )
    {
        return AppearanceHelper.getAppearance(block, state, level, pos, side, queryState, queryPos);
    }
}
