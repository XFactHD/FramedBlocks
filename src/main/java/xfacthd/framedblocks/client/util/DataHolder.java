package xfacthd.framedblocks.client.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.client.util.mixin.MixinBreakableBlock;

/**
 * Holds data used by {@link MixinBreakableBlock}
 */
public class DataHolder
{
    public static ThreadLocal<IBlockReader> world = new ThreadLocal<>();
    public static ThreadLocal<BlockPos> pos = new ThreadLocal<>();
}