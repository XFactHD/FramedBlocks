package io.github.xfacthd.framedblocks.common.block;

import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface PillarLikeBlock {
    PillarConnection getPillarConnection(BlockState state, Direction side);
}
