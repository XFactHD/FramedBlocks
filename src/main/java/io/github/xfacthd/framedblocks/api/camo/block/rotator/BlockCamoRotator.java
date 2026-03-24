package io.github.xfacthd.framedblocks.api.camo.block.rotator;

import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public interface BlockCamoRotator
{
    BlockCamoRotator DEFAULT = new DefaultBlockCamoRotator();

    boolean canRotate(BlockState state);

    @Nullable
    BlockState rotate(BlockState state);

    static BlockCamoRotator of(Block block)
    {
        return InternalAPI.INSTANCE.getCamoRotator(block);
    }
}
