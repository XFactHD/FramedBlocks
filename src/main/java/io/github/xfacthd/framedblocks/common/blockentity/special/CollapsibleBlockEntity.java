package io.github.xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.world.level.block.state.BlockState;

public interface CollapsibleBlockEntity {
    int getVertexOffset(BlockState state, int vertex);

    int getPackedOffsets(BlockState state);
}
