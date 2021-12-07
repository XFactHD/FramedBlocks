package xfacthd.framedblocks.client.util;

import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.chunk.Chunk;

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
}