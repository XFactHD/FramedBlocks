package xfacthd.framedblocks.common.data.cullupdate;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.ChunkPos;

record CullingUpdateChunk(ChunkPos chunk, LongSet positions)
{

}
