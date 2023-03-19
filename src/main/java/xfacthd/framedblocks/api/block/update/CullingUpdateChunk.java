package xfacthd.framedblocks.api.block.update;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
record CullingUpdateChunk(ChunkPos chunk, LongSet positions)
{

}
