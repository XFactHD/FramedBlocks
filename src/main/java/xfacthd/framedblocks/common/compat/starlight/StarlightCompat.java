package xfacthd.framedblocks.common.compat.starlight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import xfacthd.framedblocks.api.internal.InternalAPI;

public final class StarlightCompat
{
    private static boolean loaded = false;

    public static void init()
    {
        loaded = ModList.get().isLoaded("starlight");
    }

    // Using IForgeBlockGetter#getExistingBlockEntity() with Starlight causes chunk-loading deadlocks
    public static BlockEntity getBlockEntityForLight(BlockGetter level, BlockPos pos)
    {
        if (loaded)
        {
            return level.getBlockEntity(pos);
        }
        return InternalAPI.INSTANCE.getExistingBlockEntity(level, pos);
    }



    private StarlightCompat() { }
}
