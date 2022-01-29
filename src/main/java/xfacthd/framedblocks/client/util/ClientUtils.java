package xfacthd.framedblocks.client.util;

import com.google.common.base.Suppliers;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.chunk.Chunk;

import java.util.function.Supplier;

public class ClientUtils
{
    public static TileEntity getTileEntitySafe(IBlockReader blockGetter, BlockPos pos)
    {
        if (blockGetter instanceof ChunkRenderCache)
        {
            return ((ChunkRenderCache) blockGetter).getTileEntity(pos, Chunk.CreateEntityType.CHECK);
        }
        return null;
    }

    public static final Supplier<Boolean> OPTIFINE_LOADED = Suppliers.memoize(() ->
    {
        try
        {
            Class.forName("net.optifine.Config");
            return true;
        }
        catch (ClassNotFoundException ingored)
        {
            return false;
        }
    });
}