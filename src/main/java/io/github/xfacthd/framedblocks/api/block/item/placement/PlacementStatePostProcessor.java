package io.github.xfacthd.framedblocks.api.block.item.placement;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public interface PlacementStatePostProcessor {
    @Nullable BlockState postProcessPlacementState(BlockState state, BlockPlaceContext context);
}
