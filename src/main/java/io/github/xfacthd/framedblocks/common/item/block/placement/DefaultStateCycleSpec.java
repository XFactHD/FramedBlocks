package io.github.xfacthd.framedblocks.common.item.block.placement;

import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.common.data.attachment.PlacementStateCycleStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

sealed interface DefaultStateCycleSpec extends StateCycleSpec permits SingleBlockStateCycleSpec, MultiBlockStateCycleSpec {
    @Override
    default boolean canCycle() {
        return true;
    }

    @Override
    default @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player == null || !(context.getItemInHand().getItem() instanceof BlockItem item)) {
            return null;
        }
        BlockState state = PlacementStateCycleStorage.getSelectedState(player, item);
        if (state != null) {
            state = postProcessPlacementState(state, context);
        }
        if (state != null && !state.canSurvive(context.getLevel(), context.getClickedPos())) {
            state = null;
        }
        return state;
    }

    @Nullable BlockState postProcessPlacementState(BlockState state, BlockPlaceContext context);
}
