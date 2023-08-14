package xfacthd.framedblocks.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import xfacthd.framedblocks.api.internal.InternalAPI;
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
}
